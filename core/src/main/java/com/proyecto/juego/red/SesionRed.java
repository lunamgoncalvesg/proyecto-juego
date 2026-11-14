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

// esta clase agrupa la logica y los datos principales de sesionred
public final class SesionRed implements AutoCloseable {
    // este campo representa puerto y conserva ese dato entre llamadas
    private static final int PUERTO = 54555;
    // este campo representa timeout conexion ms y conserva ese dato entre llamadas
    private static final int TIMEOUT_CONEXION_MS = 15000;
    // este campo representa capacidad salida y conserva ese dato entre llamadas
    private static final int CAPACIDAD_SALIDA = 64;

    // este campo representa salidapendiente y conserva ese dato entre llamadas
    private final ArrayBlockingQueue<Salida> salidaPendiente = new ArrayBlockingQueue<Salida>(CAPACIDAD_SALIDA);
    // este campo representa ultimoestado y conserva ese dato entre llamadas
    private volatile byte[] ultimoEstado;
    // este campo representa socket y conserva ese dato entre llamadas
    private volatile Socket socket;
    // este campo representa salida y conserva ese dato entre llamadas
    private volatile DataOutputStream salida;
    // este campo representa conectado y conserva ese dato entre llamadas
    private volatile boolean conectado;
    // este campo representa terminado y conserva ese dato entre llamadas
    private volatile boolean terminado;
    // este campo representa salidasolicitada y conserva ese dato entre llamadas
    private boolean salidaSolicitada;
    // este campo representa puesto y conserva ese dato entre llamadas
    private volatile int puesto;
    // este campo representa estado y conserva ese dato entre llamadas
    private volatile String estado = "Conectando al servidor...";

    // este metodo sesion red concentra una operacion de esta clase y recibe el control desde quien lo llama
    public SesionRed(String direccion) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Thread hilo = new Thread(() -> conectar(direccion), "cliente-conexion-servidor");
        // realiza esta operacion auxiliar para completar el paso actual
        hilo.setDaemon(true);
        // realiza esta operacion auxiliar para completar el paso actual
        hilo.start();
    }

    // este metodo abre el socket e inicia la recepcion y el envio en segundo plano
    private void conectar(String direccion) {
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            Socket conexion = new Socket();
            // actualiza socket con el valor que necesita el estado actual
            socket = conexion;
            // realiza esta operacion auxiliar para completar el paso actual
            conexion.connect(new InetSocketAddress(direccion.trim(), PUERTO), TIMEOUT_CONEXION_MS);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (terminado) {
                // realiza esta operacion auxiliar para completar el paso actual
                conexion.close();
                // devuelve este resultado a quien llamo al metodo
                return;
            }
            // realiza esta operacion auxiliar para completar el paso actual
            conexion.setTcpNoDelay(true);
            // realiza esta operacion auxiliar para completar el paso actual
            conexion.setKeepAlive(true);
            // realiza esta operacion auxiliar para completar el paso actual
            conexion.setSoTimeout(90000);
            // actualiza salida con el valor que necesita el estado actual
            salida = new DataOutputStream(conexion.getOutputStream());
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            // actualiza conectado con el valor que necesita el estado actual
            conectado = true;
            // realiza esta operacion auxiliar para completar el paso actual
            iniciarEscritor();
            // realiza esta operacion auxiliar para completar el paso actual
            enviar(ProtocoloRed.HOLA, ProtocoloRed.contenido(datos -> datos.writeInt(ProtocoloRed.VERSION)));
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            Thread latido = new Thread(this::enviarLatidos, "cliente-latido-servidor");
            // realiza esta operacion auxiliar para completar el paso actual
            latido.setDaemon(true);
            // realiza esta operacion auxiliar para completar el paso actual
            latido.start();
            // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
            while (!terminado) {
                // realiza esta operacion auxiliar para completar el paso actual
                procesar(ProtocoloRed.leer(entrada));
            }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (IOException e) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (!terminado) {
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                String detalle = e instanceof java.net.SocketTimeoutException
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    ? "Tiempo de espera agotado." : e.getMessage();
                // actualiza estado con el valor que necesita el estado actual
                estado = puesto == 0 ? "No se pudo conectar: " + detalle : "Conexion perdida.";
            }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } finally {
            // realiza esta operacion auxiliar para completar el paso actual
            terminarSocket();
        }
    }

    // este metodo iniciar escritor concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void iniciarEscritor() {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Thread escritor = new Thread(this::escribir, "cliente-envio-servidor");
        // realiza esta operacion auxiliar para completar el paso actual
        escritor.setDaemon(true);
        // realiza esta operacion auxiliar para completar el paso actual
        escritor.start();
    }

    // este metodo escribir concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void escribir() {
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
            while (!terminado) {
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                Salida mensaje = salidaPendiente.take();
                // realiza esta operacion auxiliar para completar el paso actual
                salida.write(mensaje.bytes);
                // realiza esta operacion auxiliar para completar el paso actual
                salida.flush();
                // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                if (mensaje.cerrarDespues) {
                    // realiza esta operacion auxiliar para completar el paso actual
                    terminarSocket();
                }
            }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (InterruptedException e) {
            // realiza esta operacion auxiliar para completar el paso actual
            Thread.currentThread().interrupt();
            // realiza esta operacion auxiliar para completar el paso actual
            terminarSocket();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (IOException e) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (!terminado) {
                // actualiza estado con el valor que necesita el estado actual
                estado = "Conexion perdida.";
                // realiza esta operacion auxiliar para completar el paso actual
                terminarSocket();
            }
        }
    }

    // este metodo enviar latidos concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void enviarLatidos() {
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        while (!terminado) {
            // agrupa una operacion que puede fallar para poder manejar su error
            try {
                // realiza esta operacion auxiliar para completar el paso actual
                Thread.sleep(15000);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } catch (InterruptedException e) {
                // realiza esta operacion auxiliar para completar el paso actual
                Thread.currentThread().interrupt();
                // devuelve este resultado a quien llamo al metodo
                return;
            }
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (conectado && !terminado) {
                // agrupa una operacion que puede fallar para poder manejar su error
                try {
                    // realiza esta operacion auxiliar para completar el paso actual
                    enviar(ProtocoloRed.PING, new byte[0]);
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                } catch (IOException e) {
                    // actualiza estado con el valor que necesita el estado actual
                    estado = "Conexion perdida.";
                    // realiza esta operacion auxiliar para completar el paso actual
                    terminarSocket();
                    // devuelve este resultado a quien llamo al metodo
                    return;
                }
            }
        }
    }

    // este metodo interpreta cada tipo de mensaje y actualiza la sesion
    private void procesar(ProtocoloRed.Mensaje mensaje) throws IOException {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        DataInputStream datos = ProtocoloRed.datos(mensaje);
        // elige una ruta distinta segun el valor que se esta evaluando
        switch (mensaje.getTipo()) {
        // atiende este valor posible de la seleccion anterior
        case ProtocoloRed.ASIGNAR_JUGADOR:
            // actualiza puesto con el valor que necesita el estado actual
            puesto = datos.readUnsignedByte();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (puesto < 1 || puesto > 2 || datos.available() != 0) {
                // detiene este flujo e informa que los datos o la operacion no son validos
                throw new IOException("Puesto de jugador invalido");
            }
            // actualiza estado con el valor que necesita el estado actual
            estado = "Conectado. Esperando al rival...";
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        // atiende este valor posible de la seleccion anterior
        case ProtocoloRed.ESTADO_ESPERA:
            // actualiza estado con el valor que necesita el estado actual
            estado = datos.readUTF();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (datos.available() != 0) throw new IOException("Estado de espera invalido");
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        // atiende este valor posible de la seleccion anterior
        case ProtocoloRed.ESTADO_COMBATE:
            // actualiza ultimo estado con el valor que necesita el estado actual
            ultimoEstado = mensaje.getContenido();
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        // atiende este valor posible de la seleccion anterior
        case ProtocoloRed.PING:
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (datos.available() != 0) throw new IOException("Ping de servidor invalido");
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        // atiende este valor posible de la seleccion anterior
        case ProtocoloRed.ERROR:
            // actualiza estado con el valor que necesita el estado actual
            estado = datos.readUTF();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (datos.available() != 0) throw new IOException("Error de servidor invalido");
            // realiza esta operacion auxiliar para completar el paso actual
            terminarSocket();
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        // atiende este valor posible de la seleccion anterior
        case ProtocoloRed.DESCONEXION:
            // actualiza estado con el valor que necesita el estado actual
            estado = datos.readUTF();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (datos.available() != 0) throw new IOException("Aviso de desconexion invalido");
            // realiza esta operacion auxiliar para completar el paso actual
            terminarSocket();
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        default:
            // detiene este flujo e informa que los datos o la operacion no son validos
            throw new IOException("Tipo de mensaje inesperado del servidor: " + mensaje.getTipo());
        }
    }

    // este metodo enviar seleccion concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void enviarSeleccion(TipoPersonaje tipo) {
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // realiza esta operacion auxiliar para completar el paso actual
            enviar(ProtocoloRed.SELECCION, ProtocoloRed.contenido(datos -> {
                datos.writeByte(tipo.ordinal());
            }));
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (IOException e) {
            // actualiza estado con el valor que necesita el estado actual
            estado = "Conexion perdida.";
            // realiza esta operacion auxiliar para completar el paso actual
            terminarSocket();
        }
    }

    // este metodo confirmar jugar concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void confirmarJugar() {
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // realiza esta operacion auxiliar para completar el paso actual
            enviar(ProtocoloRed.JUGAR, new byte[0]);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (IOException e) {
            // actualiza estado con el valor que necesita el estado actual
            estado = "Conexion perdida.";
            // realiza esta operacion auxiliar para completar el paso actual
            terminarSocket();
        }
    }

    // este metodo empaqueta el estado de las teclas y las pulsaciones nuevas
    public void enviarControles(int mascara, int pulsaciones) {
        // este campo representa mascaraenviar y conserva ese dato entre llamadas
        final int mascaraEnviar = mascara & Acciones.TODAS;
        // este campo representa pulsacionesenviar y conserva ese dato entre llamadas
        final int pulsacionesEnviar = pulsaciones & Acciones.TODAS;
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int pulsacionesAgregadas = pulsacionesEnviar;
            // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
            for (Salida pendiente : salidaPendiente) {
                // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                if (pendiente.tipo == ProtocoloRed.ENTRADA) {
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    pulsacionesAgregadas |= pendiente.pulsaciones;
                    // realiza esta operacion auxiliar para completar el paso actual
                    salidaPendiente.remove(pendiente);
                }
            }
            // este campo representa pulsacionesnuevas y conserva ese dato entre llamadas
            final int pulsacionesNuevas = pulsacionesAgregadas;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            byte[] contenido = ProtocoloRed.contenido(datos -> {
                datos.writeInt(mascaraEnviar);
                datos.writeInt(pulsacionesNuevas);
            });
            // realiza esta operacion auxiliar para completar el paso actual
            encolar(ProtocoloRed.ENTRADA, contenido, pulsacionesNuevas, false);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (IOException e) {
            // actualiza estado con el valor que necesita el estado actual
            estado = "Conexion perdida.";
            // realiza esta operacion auxiliar para completar el paso actual
            terminarSocket();
        }
    }

    // este metodo enviar concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void enviar(int tipo, byte[] contenido) throws IOException {
        // realiza esta operacion auxiliar para completar el paso actual
        encolar(tipo, contenido);
    }

    // este metodo encolar concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void encolar(int tipo, byte[] contenido) throws IOException {
        // realiza esta operacion auxiliar para completar el paso actual
        encolar(tipo, contenido, 0, false);
    }

    // este metodo encolar concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void encolar(int tipo, byte[] contenido, int pulsaciones, boolean cerrarDespues) throws IOException {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (terminado || !conectado) throw new IOException("Cliente desconectado");
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Salida mensaje = new Salida(tipo, ProtocoloRed.codificar(tipo, contenido), pulsaciones, cerrarDespues);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (!salidaPendiente.offer(mensaje)) throw new IOException("Cola de envio llena");
    }

    // este metodo get puesto concentra una operacion de esta clase y recibe el control desde quien lo llama
    public int getPuesto() { return puesto; }
    // este metodo conectado concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean conectado() { return conectado && !terminado; }
    // este metodo get estado concentra una operacion de esta clase y recibe el control desde quien lo llama
    public String getEstado() { return estado; }
    // este metodo ultimo estado concentra una operacion de esta clase y recibe el control desde quien lo llama
    public byte[] ultimoEstado() { return ultimoEstado; }
    // este metodo hay estado combate concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean hayEstadoCombate() { return ultimoEstado != null; }
    // este metodo ha terminado concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean haTerminado() { return terminado; }

    @Override
    // este metodo close concentra una operacion de esta clase y recibe el control desde quien lo llama
    public synchronized void close() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (terminado || salidaSolicitada) return;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (conectado) {
            // agrupa una operacion que puede fallar para poder manejar su error
            try {
                // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                if (!salidaPendiente.offer(new Salida(ProtocoloRed.SALIR,
                    ProtocoloRed.codificar(ProtocoloRed.SALIR, new byte[0]), 0, true))) {
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    Iterator<Salida> iterador = salidaPendiente.iterator();
                    // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
                    while (iterador.hasNext()) {
                        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                        if (iterador.next().tipo == ProtocoloRed.ENTRADA) iterador.remove();
                    }
                    // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                    if (!salidaPendiente.offer(new Salida(ProtocoloRed.SALIR,
                        ProtocoloRed.codificar(ProtocoloRed.SALIR, new byte[0]), 0, true))) {
                        // realiza esta operacion auxiliar para completar el paso actual
                        terminarSocket();
                        // devuelve este resultado a quien llamo al metodo
                        return;
                    }
                }
                // actualiza salida solicitada con el valor que necesita el estado actual
                salidaSolicitada = true;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } catch (IOException e) {
                // realiza esta operacion auxiliar para completar el paso actual
                terminarSocket();
            }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else {
            // realiza esta operacion auxiliar para completar el paso actual
            terminarSocket();
        }
    }

    // este metodo terminar socket concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void terminarSocket() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (terminado) return;
        // actualiza terminado con el valor que necesita el estado actual
        terminado = true;
        // actualiza conectado con el valor que necesita el estado actual
        conectado = false;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Socket conexion = socket;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (conexion != null) {
            // agrupa una operacion que puede fallar para poder manejar su error
            try { conexion.close(); } catch (IOException ignored) { }
        }
    }

    // esta clase agrupa la logica y los datos principales de salida
    private static final class Salida {
        // este campo representa tipo y conserva ese dato entre llamadas
        final int tipo;
        // este campo representa bytes y conserva ese dato entre llamadas
        final byte[] bytes;
        // este campo representa pulsaciones y conserva ese dato entre llamadas
        final int pulsaciones;
        // este campo representa cerrardespues y conserva ese dato entre llamadas
        final boolean cerrarDespues;
        // realiza esta operacion auxiliar para completar el paso actual
        Salida(int tipo, byte[] bytes, int pulsaciones, boolean cerrarDespues) {
            // actualiza this.tipo con el valor que necesita el estado actual
            this.tipo = tipo;
            // actualiza this.bytes con el valor que necesita el estado actual
            this.bytes = bytes;
            // actualiza this.pulsaciones con el valor que necesita el estado actual
            this.pulsaciones = pulsaciones;
            // actualiza this.cerrar despues con el valor que necesita el estado actual
            this.cerrarDespues = cerrarDespues;
        }
    }
}
