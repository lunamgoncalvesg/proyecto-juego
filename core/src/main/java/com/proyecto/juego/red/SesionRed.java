package com.proyecto.juego.red;

import com.proyecto.juego.controles.Acciones;
import com.proyecto.juego.personajes.TipoPersonaje;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

public final class SesionRed implements AutoCloseable {
    private static final int PUERTO = 54555;
    private static final int TIMEOUT_CONEXION_MS = 15000;
    private static final int CAPACIDAD_SALIDA = 64;

    private final ArrayBlockingQueue<Salida> salidaPendiente = new ArrayBlockingQueue<Salida>(CAPACIDAD_SALIDA);
    private volatile byte[] ultimoEstado;
    private volatile Socket socket;
    private volatile DataOutputStream salida;
    private volatile boolean conectado;
    private volatile boolean terminado;
    private boolean salidaSolicitada;
    private volatile int puesto;
    private volatile String estado = "Conectando al servidor...";

    public SesionRed(String direccion) {
        Thread hilo = new Thread(() -> conectar(direccion), "cliente-conexion-servidor");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void conectar(String direccion) {
        try {
            Socket conexion = new Socket();
            socket = conexion;
            conexion.connect(new InetSocketAddress(direccion.trim(), PUERTO), TIMEOUT_CONEXION_MS);
            if (terminado) {
                conexion.close();
                return;
            }
            conexion.setTcpNoDelay(true);
            conexion.setKeepAlive(true);
            conexion.setSoTimeout(90000);
            salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            conectado = true;
            iniciarEscritor();
            enviar(ProtocoloRed.HOLA, ProtocoloRed.contenido(datos -> datos.writeInt(ProtocoloRed.VERSION)));
            Thread latido = new Thread(this::enviarLatidos, "cliente-latido-servidor");
            latido.setDaemon(true);
            latido.start();
            while (!terminado) {
                procesar(ProtocoloRed.leer(entrada));
            }
        } catch (IOException e) {
            if (!terminado) {
                String detalle = e instanceof java.net.SocketTimeoutException
                    ? "Tiempo de espera agotado." : e.getMessage();
                estado = puesto == 0 ? "No se pudo conectar: " + detalle : "Conexion perdida.";
            }
        } finally {
            terminarSocket();
        }
    }

    private void iniciarEscritor() {
        Thread escritor = new Thread(this::escribir, "cliente-envio-servidor");
        escritor.setDaemon(true);
        escritor.start();
    }

    private void escribir() {
        try {
            while (!terminado) {
                Salida mensaje = salidaPendiente.take();
                salida.write(mensaje.bytes);
                salida.flush();
                if (mensaje.cerrarDespues) {
                    terminarSocket();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            terminarSocket();
        } catch (IOException e) {
            if (!terminado) {
                estado = "Conexion perdida.";
                terminarSocket();
            }
        }
    }

    private void enviarLatidos() {
        while (!terminado) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (conectado && !terminado) {
                try {
                    enviar(ProtocoloRed.PING, new byte[0]);
                } catch (IOException e) {
                    estado = "Conexion perdida.";
                    terminarSocket();
                    return;
                }
            }
        }
    }

    private void procesar(ProtocoloRed.Mensaje mensaje) throws IOException {
        DataInputStream datos = ProtocoloRed.datos(mensaje);
        switch (mensaje.getTipo()) {
        case ProtocoloRed.ASIGNAR_JUGADOR:
            puesto = datos.readUnsignedByte();
            if (puesto < 1 || puesto > 2 || datos.available() != 0) {
                throw new IOException("Puesto de jugador invalido");
            }
            estado = "Conectado. Esperando al rival...";
            break;
        case ProtocoloRed.ESTADO_ESPERA:
            estado = datos.readUTF();
            if (datos.available() != 0) throw new IOException("Estado de espera invalido");
            break;
        case ProtocoloRed.ESTADO_COMBATE:
            ultimoEstado = mensaje.getContenido();
            break;
        case ProtocoloRed.PING:
            if (datos.available() != 0) throw new IOException("Ping de servidor invalido");
            break;
        case ProtocoloRed.ERROR:
            estado = datos.readUTF();
            if (datos.available() != 0) throw new IOException("Error de servidor invalido");
            terminarSocket();
            break;
        case ProtocoloRed.DESCONEXION:
            estado = datos.readUTF();
            if (datos.available() != 0) throw new IOException("Aviso de desconexion invalido");
            terminarSocket();
            break;
        default:
            throw new IOException("Tipo de mensaje inesperado del servidor: " + mensaje.getTipo());
        }
    }

    public void enviarSeleccion(TipoPersonaje tipo) {
        try {
            enviar(ProtocoloRed.SELECCION, ProtocoloRed.contenido(datos -> {
                datos.writeByte(tipo.ordinal());
            }));
        } catch (IOException e) {
            estado = "Conexion perdida.";
            terminarSocket();
        }
    }

    public void confirmarJugar() {
        try {
            enviar(ProtocoloRed.JUGAR, new byte[0]);
        } catch (IOException e) {
            estado = "Conexion perdida.";
            terminarSocket();
        }
    }

    public void enviarControles(int mascara, int pulsaciones) {
        final int mascaraEnviar = mascara & Acciones.TODAS;
        final int pulsacionesEnviar = pulsaciones & Acciones.TODAS;
        try {
            int pulsacionesAgregadas = pulsacionesEnviar;
            for (Salida pendiente : salidaPendiente) {
                if (pendiente.tipo == ProtocoloRed.ENTRADA) {
                    pulsacionesAgregadas |= pendiente.pulsaciones;
                    salidaPendiente.remove(pendiente);
                }
            }
            final int pulsacionesNuevas = pulsacionesAgregadas;
            byte[] contenido = ProtocoloRed.contenido(datos -> {
                datos.writeInt(mascaraEnviar);
                datos.writeInt(pulsacionesNuevas);
            });
            encolar(ProtocoloRed.ENTRADA, contenido, pulsacionesNuevas, false);
        } catch (IOException e) {
            estado = "Conexion perdida.";
            terminarSocket();
        }
    }

    private void enviar(int tipo, byte[] contenido) throws IOException {
        encolar(tipo, contenido);
    }

    private void encolar(int tipo, byte[] contenido) throws IOException {
        encolar(tipo, contenido, 0, false);
    }

    private void encolar(int tipo, byte[] contenido, int pulsaciones, boolean cerrarDespues) throws IOException {
        if (terminado || !conectado) throw new IOException("Cliente desconectado");
        Salida mensaje = new Salida(tipo, ProtocoloRed.codificar(tipo, contenido), pulsaciones, cerrarDespues);
        if (!salidaPendiente.offer(mensaje)) throw new IOException("Cola de envio llena");
    }

    public int getPuesto() { return puesto; }
    public boolean conectado() { return conectado && !terminado; }
    public String getEstado() { return estado; }
    public byte[] ultimoEstado() { return ultimoEstado; }
    public boolean hayEstadoCombate() { return ultimoEstado != null; }
    public boolean haTerminado() { return terminado; }

    @Override
    public synchronized void close() {
        if (terminado || salidaSolicitada) return;
        if (conectado) {
            try {
                if (!salidaPendiente.offer(new Salida(ProtocoloRed.SALIR,
                    ProtocoloRed.codificar(ProtocoloRed.SALIR, new byte[0]), 0, true))) {
                    Iterator<Salida> iterador = salidaPendiente.iterator();
                    while (iterador.hasNext()) {
                        if (iterador.next().tipo == ProtocoloRed.ENTRADA) iterador.remove();
                    }
                    if (!salidaPendiente.offer(new Salida(ProtocoloRed.SALIR,
                        ProtocoloRed.codificar(ProtocoloRed.SALIR, new byte[0]), 0, true))) {
                        terminarSocket();
                        return;
                    }
                }
                salidaSolicitada = true;
            } catch (IOException e) {
                terminarSocket();
            }
        } else {
            terminarSocket();
        }
    }

    private void terminarSocket() {
        if (terminado) return;
        terminado = true;
        conectado = false;
        Socket conexion = socket;
        if (conexion != null) {
            try { conexion.close(); } catch (IOException ignored) { }
        }
    }

    private static final class Salida {
        final int tipo;
        final byte[] bytes;
        final int pulsaciones;
        final boolean cerrarDespues;
        Salida(int tipo, byte[] bytes, int pulsaciones, boolean cerrarDespues) {
            this.tipo = tipo;
            this.bytes = bytes;
            this.pulsaciones = pulsaciones;
            this.cerrarDespues = cerrarDespues;
        }
    }
}
