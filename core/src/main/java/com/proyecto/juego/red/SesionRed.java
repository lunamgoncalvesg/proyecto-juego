package com.proyecto.juego.red;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public final class SesionRed implements AutoCloseable {
    public static final int PUERTO = 54555;
    private static final int VERSION_PROTOCOLO = 0x50535202;
    private static final int TAMANO_MAXIMO_MENSAJE = 4096;
    private final boolean anfitrion;
    private volatile boolean cerrado;
    private volatile boolean conectado;
    private volatile String estado = "Conectando...";
    private volatile byte[] recibido;
    private final ArrayBlockingQueue<byte[]> entradas = new ArrayBlockingQueue<byte[]>(240);
    private final ArrayBlockingQueue<byte[]> salida = new ArrayBlockingQueue<byte[]>(120);
    private volatile Socket socket;
    private volatile ServerSocket servidor;

    public SesionRed(boolean anfitrion, String direccion) {
        this.anfitrion = anfitrion;
        Thread hilo = new Thread(()->conectar(direccion), "conexion-juego");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void conectar(String direccion) {
        try {
            abrirConexion(direccion);
            if (cerrado) {
                socket.close();
                return;
            }
            socket.setTcpNoDelay(true);
            // La sala puede quedar abierta mientras el rival carga el juego o configura
            // el router; diez segundos cortaban esa espera aunque la conexión estuviera bien.
            socket.setSoTimeout(120000);
            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            DataOutputStream datos = new DataOutputStream(socket.getOutputStream());
            verificarVersion(entrada, datos);
            conectado = true;
            estado = "Conectado";
            Thread escritor = new Thread(() -> enviarMensajesPendientes(datos), "envio-juego");
            escritor.setDaemon(true);
            escritor.start();
            try {
                recibirMensajes(entrada);
            } finally {
                escritor.interrupt();
            }
        } catch (IOException e) {
            fallar("No se pudo conectar / conexion cerrada: " + e.getMessage());
        }
    }

    private void abrirConexion(String direccion) throws IOException {
        if (anfitrion) {
            servidor = new ServerSocket();
            servidor.setReuseAddress(true);
            servidor.bind(new InetSocketAddress(PUERTO));
            estado = "Esperando rival en el puerto " + PUERTO;
            socket = servidor.accept();
            servidor.close();
        } else {
            socket = new Socket();
            socket.connect(new InetSocketAddress(direccion.trim(), PUERTO), 15000);
        }
    }

    private void verificarVersion(DataInputStream entrada, DataOutputStream salida) throws IOException {
        salida.writeInt(VERSION_PROTOCOLO);
        salida.flush();
        if (entrada.readInt() != VERSION_PROTOCOLO) {
            throw new IOException("Version incompatible");
        }
    }

    private void enviarMensajesPendientes(DataOutputStream datos) {
        try {
            while (!cerrado) {
                byte[] mensaje = salida.take();
                datos.writeInt(mensaje.length);
                datos.write(mensaje);
                datos.flush();
            }
        } catch (Exception e) {
            fallar("Se perdio la conexion");
        }
    }

    private void recibirMensajes(DataInputStream entrada) throws IOException {
        while (!cerrado) {
            int longitud = entrada.readInt();
            if (longitud < 1 || longitud > TAMANO_MAXIMO_MENSAJE) {
                throw new IOException("Mensaje invalido");
            }
            byte[] mensaje = new byte[longitud];
            entrada.readFully(mensaje);
            recibido = mensaje;
            if (anfitrion && !entradas.offer(mensaje)) {
                throw new IOException("Demasiadas entradas pendientes");
            }
        }
    }

    private void fallar(String mensaje) {
        if (!cerrado) {
            estado = mensaje;
            close();
        }
    }

    public boolean esAnfitrion() {
        return anfitrion;
    }

    public boolean conectado() {
        return conectado;
    }

    public String getEstado() {
        return estado;
    }

    public byte[] siguienteMensaje() {
        return entradas.poll();
    }

    public boolean haTerminado() {
        return cerrado;
    }

    public byte[] ultimoMensaje() {
        return recibido;
    }

    public void enviar(byte[] mensaje) {
        if (conectado && !salida.offer(mensaje)) {
            fallar("Conexion demasiado lenta");
        }
    }

    @Override
    public void close() {
        cerrado = true;
        conectado = false;
        try {
            if (servidor != null) {
                servidor.close();
            }
        } catch (IOException ignored) {
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
