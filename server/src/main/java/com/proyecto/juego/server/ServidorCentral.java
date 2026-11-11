package com.proyecto.juego.server;

import com.proyecto.juego.red.ProtocoloRed;
import com.proyecto.juego.red.ProtocoloRed.Mensaje;
import com.proyecto.juego.personajes.TipoPersonaje;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.atomic.AtomicInteger;

final class ServidorCentral {
    private static final int TIMEOUT_LECTURA_MS = 90000;
    private static final int CAPACIDAD_SALIDA = 64;

    private final int puerto;
    private final Object bloqueo = new Object();
    private final Cliente[] jugadores = new Cliente[2];
    private ServerSocket socketServidor;
    private volatile boolean enMarcha;
    private volatile Partida partida;
    private long tick;

    ServidorCentral(int puerto) {
        this.puerto = puerto;
    }

    void iniciar() throws IOException {
        socketServidor = new ServerSocket();
        socketServidor.setReuseAddress(true);
        socketServidor.bind(new InetSocketAddress(puerto));
        System.out.println("SERVIDOR INICIADO");
        System.out.println("Puerto TCP: " + puerto);
        System.out.println("Esperando jugadores...");
        iniciarBucleSimulacion();
        try {
            while (!socketServidor.isClosed()) {
                aceptar(socketServidor.accept());
            }
        } catch (IOException e) {
            if (!socketServidor.isClosed()) throw e;
        } finally {
            cerrarTodo();
        }
    }

    void detener() {
        enMarcha = false;
        ServerSocket servidor = socketServidor;
        if (servidor != null) {
            try { servidor.close(); } catch (IOException ignored) { }
        }
        cerrarTodo();
    }

    private void aceptar(Socket socket) {
        try {
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);
            socket.setSoTimeout(TIMEOUT_LECTURA_MS);
        } catch (IOException e) {
            System.out.println("No se pudo preparar una conexion: " + e.getMessage());
            cerrarSocket(socket);
            return;
        }
        int puesto;
        boolean ocupado;
        Cliente cliente = null;
        synchronized (bloqueo) {
            puesto = jugadores[0] == null ? 0 : jugadores[1] == null ? 1 : -1;
            ocupado = puesto < 0;
            if (puesto >= 0) {
                try {
                    cliente = new Cliente(this, socket, puesto + 1);
                    jugadores[puesto] = cliente;
                } catch (IOException e) {
                    System.out.println("No se pudo abrir la conexion: " + e.getMessage());
                }
            }
        }
        if (ocupado) {
            try {
                rechazarOcupado(socket);
            } catch (IOException e) {
                cerrarSocket(socket);
            }
            return;
        }
        if (cliente == null) {
            cerrarSocket(socket);
            return;
        }
        cliente.iniciar();
    }

    private void rechazarOcupado(Socket socket) throws IOException {
        try (Socket ocupado = socket) {
            DataOutputStream salida = new DataOutputStream(ocupado.getOutputStream());
            byte[] texto = ProtocoloRed.contenido(datos -> datos.writeUTF("Servidor ocupado: ya hay dos jugadores."));
            salida.write(ProtocoloRed.codificar(ProtocoloRed.ERROR, texto));
            salida.flush();
        }
    }

    private void recibir(Cliente cliente, Mensaje mensaje) throws IOException {
        DataInputStream datos = ProtocoloRed.datos(mensaje);
        switch (mensaje.getTipo()) {
        case ProtocoloRed.HOLA:
            if (cliente.estaPresentado() || datos.readInt() != ProtocoloRed.VERSION || datos.available() != 0) {
                throw new IOException("Inicio de protocolo invalido");
            }
            cliente.marcarPresentado();
            System.out.println("JUGADOR " + cliente.getPuesto() + " CONECTADO");
            cliente.enviar(ProtocoloRed.ASIGNAR_JUGADOR,
                ProtocoloRed.contenido(salida -> salida.writeByte(cliente.getPuesto())));
            publicarEspera();
            break;
        case ProtocoloRed.SELECCION:
            requerirPresentado(cliente);
            int ordinal = datos.readUnsignedByte();
            boolean listo = datos.readBoolean();
            if (datos.available() != 0 || ordinal >= TipoPersonaje.values().length || partida != null) {
                throw new IOException("Seleccion de personaje invalida");
            }
            cliente.establecerSeleccion(TipoPersonaje.values()[ordinal], listo);
            actualizarListos();
            break;
        case ProtocoloRed.ENTRADA:
            requerirPresentado(cliente);
            int mascara = datos.readInt();
            int pulsaciones = datos.readInt();
            if (datos.available() != 0 || (mascara & ~127) != 0 || (pulsaciones & ~127) != 0) {
                throw new IOException("Entrada de controles invalida");
            }
            cliente.actualizarEntrada(mascara, pulsaciones);
            break;
        case ProtocoloRed.PING:
            requerirPresentado(cliente);
            if (datos.available() != 0) {
                throw new IOException("Ping invalido");
            }
            cliente.enviar(ProtocoloRed.PING, new byte[0]);
            break;
        case ProtocoloRed.SALIR:
            requerirPresentado(cliente);
            desconectar(cliente, "El rival abandono la partida.");
            break;
        default:
            throw new IOException("Tipo de mensaje inesperado: " + mensaje.getTipo());
        }
    }

    private void requerirPresentado(Cliente cliente) throws IOException {
        if (!cliente.estaPresentado()) {
            throw new IOException("El cliente aun no inicio el protocolo");
        }
    }

    private void publicarEspera() {
        Cliente primero;
        Cliente segundo;
        synchronized (bloqueo) {
            primero = jugadores[0];
            segundo = jugadores[1];
        }
        boolean ambos = primero != null && segundo != null && primero.estaPresentado() && segundo.estaPresentado();
        String estado = ambos ? "Rival conectado" : "Esperando al rival...";
        notificarEstado(primero, estado);
        notificarEstado(segundo, estado);
        if (ambos) {
            System.out.println("Rival conectado");
        } else if (primero != null && primero.estaPresentado()) {
            System.out.println("Esperando al segundo jugador...");
        }
    }

    private void notificarEstado(Cliente cliente, String estado) {
        if (cliente != null && cliente.estaPresentado()) {
            try {
                cliente.enviar(ProtocoloRed.ESTADO_ESPERA,
                    ProtocoloRed.contenido(datos -> datos.writeUTF(estado)));
            } catch (IOException e) {
                desconectar(cliente, "No se pudo notificar el estado de la partida.");
            }
        }
    }

    private void actualizarListos() {
        Cliente primero;
        Cliente segundo;
        synchronized (bloqueo) {
            primero = jugadores[0];
            segundo = jugadores[1];
        }
        if (primero == null || segundo == null || !primero.estaListo() || !segundo.estaListo()) {
            notificarEstado(primero, segundo == null ? "Esperando al rival..." : "Rival conectado");
            notificarEstado(segundo, primero == null ? "Esperando al rival..." : "Rival conectado");
            return;
        }
        synchronized (bloqueo) {
            if (partida == null && jugadores[0] == primero && jugadores[1] == segundo) {
                partida = new Partida(primero.getSeleccion(), segundo.getSeleccion());
            }
        }
        System.out.println("COMBATE INICIADO");
    }

    private void iniciarBucleSimulacion() {
        enMarcha = true;
        Thread simulador = new Thread(this::ejecutarSimulacion, "servidor-simulacion-60hz");
        simulador.setDaemon(true);
        simulador.start();
    }

    private void ejecutarSimulacion() {
        final long paso = 1000000000L / 60L;
        long siguiente = System.nanoTime();
        while (enMarcha) {
            siguiente += paso;
            actualizarSimulacion();
            long espera = siguiente - System.nanoTime();
            if (espera > 0) {
                LockSupport.parkNanos(espera);
            } else if (espera < -paso * 5) {
                siguiente = System.nanoTime();
            }
        }
    }

    private void actualizarSimulacion() {
        Partida actual = partida;
        if (actual == null) return;
        Cliente primero;
        Cliente segundo;
        synchronized (bloqueo) {
            primero = jugadores[0];
            segundo = jugadores[1];
        }
        if (primero == null || segundo == null) return;
        actual.actualizar(primero.getMascara(), segundo.getMascara(),
            primero.consumirPulsaciones(), segundo.consumirPulsaciones());
        if (++tick % 2 != 0) return;
        byte[] estado;
        try {
            estado = actual.crearEstado();
        } catch (IOException e) {
            desconectar(primero, "Error creando el estado del combate.");
            return;
        }
        try {
            primero.enviar(ProtocoloRed.ESTADO_COMBATE, estado);
        } catch (IOException e) {
            desconectar(primero, "Error enviando el estado del combate.");
            return;
        }
        try {
            segundo.enviar(ProtocoloRed.ESTADO_COMBATE, estado);
        } catch (IOException e) {
            desconectar(segundo, "Error enviando el estado del combate.");
        }
    }

    void desconectar(Cliente origen, String motivo) {
        Cliente primero;
        Cliente segundo;
        synchronized (bloqueo) {
            if (jugadores[origen.getPuesto() - 1] != origen) {
                origen.cerrar();
                return;
            }
            primero = jugadores[0];
            segundo = jugadores[1];
            jugadores[0] = null;
            jugadores[1] = null;
            partida = null;
        }
        System.out.println("JUGADOR " + origen.getPuesto() + " DESCONECTADO: " + motivo);
        if (primero != null && primero != origen) {
            primero.enviarDesconexion(motivo);
        }
        if (segundo != null && segundo != origen) {
            segundo.enviarDesconexion(motivo);
        }
        origen.cerrar();
        if (primero == null && segundo == null) {
            return;
        }
        System.out.println("Lugares liberados. Esperando jugadores...");
    }

    private void cerrarTodo() {
        enMarcha = false;
        Cliente primero;
        Cliente segundo;
        synchronized (bloqueo) {
            primero = jugadores[0];
            segundo = jugadores[1];
            jugadores[0] = null;
            jugadores[1] = null;
            partida = null;
        }
        if (primero != null) primero.cerrar();
        if (segundo != null) segundo.cerrar();
    }

    private void cerrarSocket(Socket socket) {
        try { socket.close(); } catch (IOException ignored) { }
    }

    private static final class Cliente implements Runnable {
        private final ServidorCentral servidor;
        private final Socket socket;
        private final int puesto;
        private final ArrayBlockingQueue<Salida> cola = new ArrayBlockingQueue<Salida>(CAPACIDAD_SALIDA);
        private final AtomicInteger pulsaciones = new AtomicInteger();
        private volatile boolean cerrado;
        private volatile boolean presentado;
        private volatile boolean listo;
        private volatile int mascara;
        private volatile TipoPersonaje seleccion = TipoPersonaje.EQUILIBRADO;
        private DataOutputStream salida;

        Cliente(ServidorCentral servidor, Socket socket, int puesto) throws IOException {
            this.servidor = servidor;
            this.socket = socket;
            this.puesto = puesto;
            salida = new DataOutputStream(socket.getOutputStream());
        }

        void iniciar() {
            Thread escritor = new Thread(this::escribir, "servidor-escritor-j" + puesto);
            escritor.setDaemon(true);
            escritor.start();
            Thread lector = new Thread(this, "servidor-lector-j" + puesto);
            lector.setDaemon(true);
            lector.start();
        }

        @Override
        public void run() {
            try {
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                while (!cerrado) {
                    servidor.recibir(this, ProtocoloRed.leer(entrada));
                }
            } catch (IOException e) {
                if (!cerrado) {
                    servidor.desconectar(this, e instanceof java.net.SocketTimeoutException
                        ? "Tiempo de espera agotado." : "Conexion perdida.");
                }
            }
        }

        private void escribir() {
            try {
                while (!cerrado) {
                    Salida mensaje = cola.take();
                    salida.write(mensaje.bytes);
                    salida.flush();
                    if (mensaje.cerrarDespues) {
                        cerrar();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                cerrar();
            } catch (IOException e) {
                if (!cerrado) servidor.desconectar(this, "Error enviando datos.");
            }
        }

        void enviar(int tipo, byte[] contenido) throws IOException {
            encolar(tipo, contenido, false);
        }

        private void encolar(int tipo, byte[] contenido, boolean cerrarDespues) throws IOException {
            if (cerrado) return;
            Salida mensaje = new Salida(tipo, ProtocoloRed.codificar(tipo, contenido), cerrarDespues);
            if (tipo == ProtocoloRed.ESTADO_COMBATE) {
                for (Salida pendiente : cola) {
                    if (pendiente.tipo == ProtocoloRed.ESTADO_COMBATE) cola.remove(pendiente);
                }
            }
            if (cola.offer(mensaje)) return;
            throw new IOException("Cola de salida llena");
        }

        void enviarDesconexion(String motivo) {
            try {
                encolar(ProtocoloRed.DESCONEXION,
                    ProtocoloRed.contenido(datos -> datos.writeUTF(motivo)), true);
            } catch (IOException e) {
                cerrar();
            }
        }

        void marcarPresentado() { presentado = true; }
        boolean estaPresentado() { return presentado; }
        int getPuesto() { return puesto; }
        boolean estaListo() { return listo; }
        void establecerSeleccion(TipoPersonaje tipo, boolean listo) { seleccion = tipo; this.listo = listo; }
        void actualizarEntrada(int mascara, int pulsacionesNuevas) {
            this.mascara = mascara;
            pulsaciones.getAndUpdate(actual -> actual | pulsacionesNuevas);
        }
        TipoPersonaje getSeleccion() { return seleccion; }
        int getMascara() { return mascara; }
        int consumirPulsaciones() { return pulsaciones.getAndSet(0); }
        void cerrar() {
            if (cerrado) return;
            cerrado = true;
            try { socket.close(); } catch (IOException ignored) { }
        }
    }

    private static final class Salida {
        final int tipo;
        final byte[] bytes;
        final boolean cerrarDespues;
        Salida(int tipo, byte[] bytes, boolean cerrarDespues) {
            this.tipo = tipo;
            this.bytes = bytes;
            this.cerrarDespues = cerrarDespues;
        }
    }
}
