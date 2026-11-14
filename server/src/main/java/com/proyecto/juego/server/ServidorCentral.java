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

// esta clase agrupa la logica y los datos principales de servidorcentral
final class ServidorCentral {
    // este campo representa timeout lectura ms y conserva ese dato entre llamadas
    private static final int TIMEOUT_LECTURA_MS = 90000;
    // este campo representa capacidad salida y conserva ese dato entre llamadas
    private static final int CAPACIDAD_SALIDA = 64;

    // este campo representa puerto y conserva ese dato entre llamadas
    private final int puerto;
    // este campo crea y conserva bloqueo para reutilizar este recurso
    private final Object bloqueo = new Object();
    // este campo representa jugadores y conserva ese dato entre llamadas
    private final Cliente[] jugadores = new Cliente[2];
    // este campo representa socketservidor y conserva ese dato entre llamadas
    private ServerSocket socketServidor;
    // este campo representa enmarcha y conserva ese dato entre llamadas
    private volatile boolean enMarcha;
    // este campo representa partida y conserva ese dato entre llamadas
    private volatile Partida partida;
    // este campo representa tick y conserva ese dato entre llamadas
    private long tick;

    // realiza esta operacion auxiliar para completar el paso actual
    ServidorCentral(int puerto) {
        // actualiza this.puerto con el valor que necesita el estado actual
        this.puerto = puerto;
    }

    // este paso prepara o actualiza el estado que necesita el siguiente paso
    void iniciar() throws IOException {
        // actualiza socket servidor con el valor que necesita el estado actual
        socketServidor = new ServerSocket();
        // realiza esta operacion auxiliar para completar el paso actual
        socketServidor.setReuseAddress(true);
        // realiza esta operacion auxiliar para completar el paso actual
        socketServidor.bind(new InetSocketAddress(puerto));
        // realiza esta operacion auxiliar para completar el paso actual
        System.out.println("SERVIDOR INICIADO");
        // realiza esta operacion auxiliar para completar el paso actual
        System.out.println("Puerto TCP: " + puerto);
        // realiza esta operacion auxiliar para completar el paso actual
        System.out.println("Esperando jugadores...");
        // realiza esta operacion auxiliar para completar el paso actual
        iniciarBucleSimulacion();
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
            while (!socketServidor.isClosed()) {
                // realiza esta operacion auxiliar para completar el paso actual
                aceptar(socketServidor.accept());
            }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (IOException e) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (!socketServidor.isClosed()) throw e;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } finally {
            // realiza esta operacion auxiliar para completar el paso actual
            cerrarTodo();
        }
    }

    // este paso prepara o actualiza el estado que necesita el siguiente paso
    void detener() {
        // actualiza en marcha con el valor que necesita el estado actual
        enMarcha = false;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        ServerSocket servidor = socketServidor;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (servidor != null) {
            // agrupa una operacion que puede fallar para poder manejar su error
            try { servidor.close(); } catch (IOException ignored) { }
        }
        // realiza esta operacion auxiliar para completar el paso actual
        cerrarTodo();
    }

    // este metodo aceptar concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void aceptar(Socket socket) {
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // realiza esta operacion auxiliar para completar el paso actual
            socket.setTcpNoDelay(true);
            // realiza esta operacion auxiliar para completar el paso actual
            socket.setKeepAlive(true);
            // realiza esta operacion auxiliar para completar el paso actual
            socket.setSoTimeout(TIMEOUT_LECTURA_MS);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (IOException e) {
            // realiza esta operacion auxiliar para completar el paso actual
            System.out.println("No se pudo preparar una conexion: " + e.getMessage());
            // realiza esta operacion auxiliar para completar el paso actual
            cerrarSocket(socket);
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int puesto;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean ocupado;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Cliente cliente = null;
        // protege este cambio para que dos hilos no modifiquen el mismo estado a la vez
        synchronized (bloqueo) {
            // actualiza puesto con el valor que necesita el estado actual
            puesto = jugadores[0] == null ? 0 : jugadores[1] == null ? 1 : -1;
            // actualiza ocupado con el valor que necesita el estado actual
            ocupado = puesto < 0;
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (puesto >= 0) {
                // agrupa una operacion que puede fallar para poder manejar su error
                try {
                    // actualiza cliente con el valor que necesita el estado actual
                    cliente = new Cliente(this, socket, puesto + 1);
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    jugadores[puesto] = cliente;
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                } catch (IOException e) {
                    // realiza esta operacion auxiliar para completar el paso actual
                    System.out.println("No se pudo abrir la conexion: " + e.getMessage());
                }
            }
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (ocupado) {
            // agrupa una operacion que puede fallar para poder manejar su error
            try {
                // realiza esta operacion auxiliar para completar el paso actual
                rechazarOcupado(socket);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } catch (IOException e) {
                // realiza esta operacion auxiliar para completar el paso actual
                cerrarSocket(socket);
            }
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (cliente == null) {
            // realiza esta operacion auxiliar para completar el paso actual
            cerrarSocket(socket);
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        cliente.iniciar();
    }

    // este metodo rechazar ocupado concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void rechazarOcupado(Socket socket) throws IOException {
        // agrupa una operacion que puede fallar para poder manejar su error
        try (Socket ocupado = socket) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            DataOutputStream salida = new DataOutputStream(ocupado.getOutputStream());
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            byte[] texto = ProtocoloRed.contenido(datos -> datos.writeUTF("Servidor ocupado: ya hay dos jugadores."));
            // realiza esta operacion auxiliar para completar el paso actual
            salida.write(ProtocoloRed.codificar(ProtocoloRed.ERROR, texto));
            // realiza esta operacion auxiliar para completar el paso actual
            salida.flush();
        }
    }

    // este metodo recibir concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void recibir(Cliente cliente, Mensaje mensaje) throws IOException {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        DataInputStream datos = ProtocoloRed.datos(mensaje);
        // elige una ruta distinta segun el valor que se esta evaluando
        switch (mensaje.getTipo()) {
        // atiende este valor posible de la seleccion anterior
        case ProtocoloRed.HOLA:
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (cliente.estaPresentado() || datos.readInt() != ProtocoloRed.VERSION || datos.available() != 0) {
                // detiene este flujo e informa que los datos o la operacion no son validos
                throw new IOException("Inicio de protocolo invalido");
            }
            // realiza esta operacion auxiliar para completar el paso actual
            cliente.marcarPresentado();
            // realiza esta operacion auxiliar para completar el paso actual
            System.out.println("JUGADOR " + cliente.getPuesto() + " CONECTADO");
            // realiza esta operacion auxiliar para completar el paso actual
            cliente.enviar(ProtocoloRed.ASIGNAR_JUGADOR,
                ProtocoloRed.contenido(salida -> salida.writeByte(cliente.getPuesto())));
            // realiza esta operacion auxiliar para completar el paso actual
            publicarEspera();
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        // atiende este valor posible de la seleccion anterior
        case ProtocoloRed.SELECCION:
            // realiza esta operacion auxiliar para completar el paso actual
            requerirPresentado(cliente);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int ordinal = datos.readUnsignedByte();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (datos.available() != 0 || ordinal >= TipoPersonaje.values().length || partida != null) {
                // detiene este flujo e informa que los datos o la operacion no son validos
                throw new IOException("Seleccion de personaje invalida");
            }
            // realiza esta operacion auxiliar para completar el paso actual
            cliente.establecerSeleccion(TipoPersonaje.values()[ordinal]);
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        // atiende este valor posible de la seleccion anterior
        case ProtocoloRed.JUGAR:
            // realiza esta operacion auxiliar para completar el paso actual
            requerirPresentado(cliente);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (datos.available() != 0 || !cliente.tieneSeleccion() || partida != null) {
                // detiene este flujo e informa que los datos o la operacion no son validos
                throw new IOException("Confirmacion de inicio invalida");
            }
            // realiza esta operacion auxiliar para completar el paso actual
            cliente.confirmarJugar();
            // realiza esta operacion auxiliar para completar el paso actual
            actualizarListos();
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        // atiende este valor posible de la seleccion anterior
        case ProtocoloRed.ENTRADA:
            // realiza esta operacion auxiliar para completar el paso actual
            requerirPresentado(cliente);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int mascara = datos.readInt();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int pulsaciones = datos.readInt();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (datos.available() != 0 || (mascara & ~127) != 0 || (pulsaciones & ~127) != 0) {
                // detiene este flujo e informa que los datos o la operacion no son validos
                throw new IOException("Entrada de controles invalida");
            }
            // realiza esta operacion auxiliar para completar el paso actual
            cliente.actualizarEntrada(mascara, pulsaciones);
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        // atiende este valor posible de la seleccion anterior
        case ProtocoloRed.PING:
            // realiza esta operacion auxiliar para completar el paso actual
            requerirPresentado(cliente);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (datos.available() != 0) {
                // detiene este flujo e informa que los datos o la operacion no son validos
                throw new IOException("Ping invalido");
            }
            // realiza esta operacion auxiliar para completar el paso actual
            cliente.enviar(ProtocoloRed.PING, new byte[0]);
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        // atiende este valor posible de la seleccion anterior
        case ProtocoloRed.SALIR:
            // realiza esta operacion auxiliar para completar el paso actual
            requerirPresentado(cliente);
            // realiza esta operacion auxiliar para completar el paso actual
            desconectar(cliente, "El rival abandono la partida.");
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        default:
            // detiene este flujo e informa que los datos o la operacion no son validos
            throw new IOException("Tipo de mensaje inesperado: " + mensaje.getTipo());
        }
    }

    // este metodo requerir presentado concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void requerirPresentado(Cliente cliente) throws IOException {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (!cliente.estaPresentado()) {
            // detiene este flujo e informa que los datos o la operacion no son validos
            throw new IOException("El cliente aun no inicio el protocolo");
        }
    }

    // este metodo informa a cada jugador si el rival ya se conecto
    private void publicarEspera() {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Cliente primero;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Cliente segundo;
        // protege este cambio para que dos hilos no modifiquen el mismo estado a la vez
        synchronized (bloqueo) {
            // actualiza primero con el valor que necesita el estado actual
            primero = jugadores[0];
            // actualiza segundo con el valor que necesita el estado actual
            segundo = jugadores[1];
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean ambos = primero != null && segundo != null && primero.estaPresentado() && segundo.estaPresentado();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String estado = ambos ? "Rival conectado" : "Esperando al rival...";
        // realiza esta operacion auxiliar para completar el paso actual
        notificarEstado(primero, estado);
        // realiza esta operacion auxiliar para completar el paso actual
        notificarEstado(segundo, estado);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (ambos) {
            // realiza esta operacion auxiliar para completar el paso actual
            System.out.println("Rival conectado");
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else if (primero != null && primero.estaPresentado()) {
            // realiza esta operacion auxiliar para completar el paso actual
            System.out.println("Esperando al segundo jugador...");
        }
    }

    // este metodo notificar estado concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void notificarEstado(Cliente cliente, String estado) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (cliente != null && cliente.estaPresentado()) {
            // agrupa una operacion que puede fallar para poder manejar su error
            try {
                // realiza esta operacion auxiliar para completar el paso actual
                cliente.enviar(ProtocoloRed.ESTADO_ESPERA,
                    ProtocoloRed.contenido(datos -> datos.writeUTF(estado)));
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } catch (IOException e) {
                // realiza esta operacion auxiliar para completar el paso actual
                desconectar(cliente, "No se pudo notificar el estado de la partida.");
            }
        }
    }

    // este metodo crea la partida cuando los dos jugadores confirman que quieren empezar
    private void actualizarListos() {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Cliente primero;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Cliente segundo;
        // protege este cambio para que dos hilos no modifiquen el mismo estado a la vez
        synchronized (bloqueo) {
            // actualiza primero con el valor que necesita el estado actual
            primero = jugadores[0];
            // actualiza segundo con el valor que necesita el estado actual
            segundo = jugadores[1];
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (primero == null || segundo == null || !primero.confirmoJugar() || !segundo.confirmoJugar()) {
            // realiza esta operacion auxiliar para completar el paso actual
            notificarEstado(primero, estadoPara(primero, segundo));
            // realiza esta operacion auxiliar para completar el paso actual
            notificarEstado(segundo, estadoPara(segundo, primero));
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // protege este cambio para que dos hilos no modifiquen el mismo estado a la vez
        synchronized (bloqueo) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (partida == null && jugadores[0] == primero && jugadores[1] == segundo) {
                // actualiza partida con el valor que necesita el estado actual
                partida = new Partida(primero.getSeleccion(), segundo.getSeleccion());
            }
        }
        // realiza esta operacion auxiliar para completar el paso actual
        System.out.println("COMBATE INICIADO");
    }

    // este metodo estado para concentra una operacion de esta clase y recibe el control desde quien lo llama
    private String estadoPara(Cliente jugador, Cliente rival) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (jugador == null) return "";
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (rival == null || !rival.estaPresentado()) return "Esperando al rival...";
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (rival.confirmoJugar()) return "Rival conectado. Ya presiono JUGAR.";
        // devuelve este resultado a quien llamo al metodo
        return jugador.confirmoJugar() ? "Esperando que el rival presione JUGAR..." : "Rival conectado";
    }

    // este metodo iniciar bucle simulacion concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void iniciarBucleSimulacion() {
        // actualiza en marcha con el valor que necesita el estado actual
        enMarcha = true;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Thread simulador = new Thread(this::ejecutarSimulacion, "servidor-simulacion-60hz");
        // realiza esta operacion auxiliar para completar el paso actual
        simulador.setDaemon(true);
        // realiza esta operacion auxiliar para completar el paso actual
        simulador.start();
    }

    // este metodo mantiene una simulacion fija de sesenta pasos por segundo
    private void ejecutarSimulacion() {
        // este campo representa paso y conserva ese dato entre llamadas
        final long paso = 1000000000L / 60L;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        long siguiente = System.nanoTime();
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        while (enMarcha) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            siguiente += paso;
            // realiza esta operacion auxiliar para completar el paso actual
            actualizarSimulacion();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            long espera = siguiente - System.nanoTime();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (espera > 0) {
                // realiza esta operacion auxiliar para completar el paso actual
                LockSupport.parkNanos(espera);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } else if (espera < -paso * 5) {
                // actualiza siguiente con el valor que necesita el estado actual
                siguiente = System.nanoTime();
            }
        }
    }

    // este metodo aplica las entradas de los jugadores y publica el estado resultante
    private void actualizarSimulacion() {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Partida actual = partida;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (actual == null) return;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Cliente primero;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Cliente segundo;
        // protege este cambio para que dos hilos no modifiquen el mismo estado a la vez
        synchronized (bloqueo) {
            // actualiza primero con el valor que necesita el estado actual
            primero = jugadores[0];
            // actualiza segundo con el valor que necesita el estado actual
            segundo = jugadores[1];
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (primero == null || segundo == null) return;
        // realiza esta operacion auxiliar para completar el paso actual
        actual.actualizar(primero.getMascara(), segundo.getMascara(),
            primero.consumirPulsaciones(), segundo.consumirPulsaciones());
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (++tick % 2 != 0) return;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        byte[] estado;
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // actualiza estado con el valor que necesita el estado actual
            estado = actual.crearEstado();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (IOException e) {
            // realiza esta operacion auxiliar para completar el paso actual
            desconectar(primero, "Error creando el estado del combate.");
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // realiza esta operacion auxiliar para completar el paso actual
            primero.enviar(ProtocoloRed.ESTADO_COMBATE, estado);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (IOException e) {
            // realiza esta operacion auxiliar para completar el paso actual
            desconectar(primero, "Error enviando el estado del combate.");
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // realiza esta operacion auxiliar para completar el paso actual
            segundo.enviar(ProtocoloRed.ESTADO_COMBATE, estado);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (IOException e) {
            // realiza esta operacion auxiliar para completar el paso actual
            desconectar(segundo, "Error enviando el estado del combate.");
        }
    }

    // este paso prepara o actualiza el estado que necesita el siguiente paso
    void desconectar(Cliente origen, String motivo) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Cliente primero;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Cliente segundo;
        // protege este cambio para que dos hilos no modifiquen el mismo estado a la vez
        synchronized (bloqueo) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (jugadores[origen.getPuesto() - 1] != origen) {
                // realiza esta operacion auxiliar para completar el paso actual
                origen.cerrar();
                // devuelve este resultado a quien llamo al metodo
                return;
            }
            // actualiza primero con el valor que necesita el estado actual
            primero = jugadores[0];
            // actualiza segundo con el valor que necesita el estado actual
            segundo = jugadores[1];
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            jugadores[0] = null;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            jugadores[1] = null;
            // actualiza partida con el valor que necesita el estado actual
            partida = null;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        System.out.println("JUGADOR " + origen.getPuesto() + " DESCONECTADO: " + motivo);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (primero != null && primero != origen) {
            // realiza esta operacion auxiliar para completar el paso actual
            primero.enviarDesconexion(motivo);
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (segundo != null && segundo != origen) {
            // realiza esta operacion auxiliar para completar el paso actual
            segundo.enviarDesconexion(motivo);
        }
        // realiza esta operacion auxiliar para completar el paso actual
        origen.cerrar();
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (primero == null && segundo == null) {
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        System.out.println("Lugares liberados. Esperando jugadores...");
    }

    // este metodo cerrar todo concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void cerrarTodo() {
        // actualiza en marcha con el valor que necesita el estado actual
        enMarcha = false;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Cliente primero;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Cliente segundo;
        // protege este cambio para que dos hilos no modifiquen el mismo estado a la vez
        synchronized (bloqueo) {
            // actualiza primero con el valor que necesita el estado actual
            primero = jugadores[0];
            // actualiza segundo con el valor que necesita el estado actual
            segundo = jugadores[1];
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            jugadores[0] = null;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            jugadores[1] = null;
            // actualiza partida con el valor que necesita el estado actual
            partida = null;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (primero != null) primero.cerrar();
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (segundo != null) segundo.cerrar();
    }

    // este metodo cerrar socket concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void cerrarSocket(Socket socket) {
        // agrupa una operacion que puede fallar para poder manejar su error
        try { socket.close(); } catch (IOException ignored) { }
    }

    // esta clase agrupa la logica y los datos principales de cliente
    private static final class Cliente implements Runnable {
        // este campo representa servidor y conserva ese dato entre llamadas
        private final ServidorCentral servidor;
        // este campo representa socket y conserva ese dato entre llamadas
        private final Socket socket;
        // este campo representa puesto y conserva ese dato entre llamadas
        private final int puesto;
        // este campo representa cola y conserva ese dato entre llamadas
        private final ArrayBlockingQueue<Salida> cola = new ArrayBlockingQueue<Salida>(CAPACIDAD_SALIDA);
        // este campo crea y conserva pulsaciones para reutilizar este recurso
        private final AtomicInteger pulsaciones = new AtomicInteger();
        // este campo representa cerrado y conserva ese dato entre llamadas
        private volatile boolean cerrado;
        // este campo representa presentado y conserva ese dato entre llamadas
        private volatile boolean presentado;
        // este campo representa seleccionhecha y conserva ese dato entre llamadas
        private volatile boolean seleccionHecha;
        // este campo representa confirmojugar y conserva ese dato entre llamadas
        private volatile boolean confirmoJugar;
        // este campo representa mascara y conserva ese dato entre llamadas
        private volatile int mascara;
        // este campo representa seleccion y conserva ese dato entre llamadas
        private volatile TipoPersonaje seleccion = TipoPersonaje.EQUILIBRADO;
        // este campo representa salida y conserva ese dato entre llamadas
        private DataOutputStream salida;

        // realiza esta operacion auxiliar para completar el paso actual
        Cliente(ServidorCentral servidor, Socket socket, int puesto) throws IOException {
            // actualiza this.servidor con el valor que necesita el estado actual
            this.servidor = servidor;
            // actualiza this.socket con el valor que necesita el estado actual
            this.socket = socket;
            // actualiza this.puesto con el valor que necesita el estado actual
            this.puesto = puesto;
            // actualiza salida con el valor que necesita el estado actual
            salida = new DataOutputStream(socket.getOutputStream());
        }

        // este paso prepara o actualiza el estado que necesita el siguiente paso
        void iniciar() {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            Thread escritor = new Thread(this::escribir, "servidor-escritor-j" + puesto);
            // realiza esta operacion auxiliar para completar el paso actual
            escritor.setDaemon(true);
            // realiza esta operacion auxiliar para completar el paso actual
            escritor.start();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            Thread lector = new Thread(this, "servidor-lector-j" + puesto);
            // realiza esta operacion auxiliar para completar el paso actual
            lector.setDaemon(true);
            // realiza esta operacion auxiliar para completar el paso actual
            lector.start();
        }

        @Override
        // este metodo run concentra una operacion de esta clase y recibe el control desde quien lo llama
        public void run() {
            // agrupa una operacion que puede fallar para poder manejar su error
            try {
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
                while (!cerrado) {
                    // realiza esta operacion auxiliar para completar el paso actual
                    servidor.recibir(this, ProtocoloRed.leer(entrada));
                }
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } catch (IOException e) {
                // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                if (!cerrado) {
                    // realiza esta operacion auxiliar para completar el paso actual
                    servidor.desconectar(this, e instanceof java.net.SocketTimeoutException
                        ? "Tiempo de espera agotado." : "Conexion perdida.");
                }
            }
        }

        // este metodo escribir concentra una operacion de esta clase y recibe el control desde quien lo llama
        private void escribir() {
            // agrupa una operacion que puede fallar para poder manejar su error
            try {
                // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
                while (!cerrado) {
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    Salida mensaje = cola.take();
                    // realiza esta operacion auxiliar para completar el paso actual
                    salida.write(mensaje.bytes);
                    // realiza esta operacion auxiliar para completar el paso actual
                    salida.flush();
                    // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                    if (mensaje.cerrarDespues) {
                        // realiza esta operacion auxiliar para completar el paso actual
                        cerrar();
                    }
                }
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } catch (InterruptedException e) {
                // realiza esta operacion auxiliar para completar el paso actual
                Thread.currentThread().interrupt();
                // realiza esta operacion auxiliar para completar el paso actual
                cerrar();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } catch (IOException e) {
                // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                if (!cerrado) servidor.desconectar(this, "Error enviando datos.");
            }
        }

        // este paso prepara o actualiza el estado que necesita el siguiente paso
        void enviar(int tipo, byte[] contenido) throws IOException {
            // realiza esta operacion auxiliar para completar el paso actual
            encolar(tipo, contenido, false);
        }

        // este metodo encolar concentra una operacion de esta clase y recibe el control desde quien lo llama
        private void encolar(int tipo, byte[] contenido, boolean cerrarDespues) throws IOException {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (cerrado) return;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            Salida mensaje = new Salida(tipo, ProtocoloRed.codificar(tipo, contenido), cerrarDespues);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (tipo == ProtocoloRed.ESTADO_COMBATE) {
                // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
                for (Salida pendiente : cola) {
                    // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                    if (pendiente.tipo == ProtocoloRed.ESTADO_COMBATE) cola.remove(pendiente);
                }
            }
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (cola.offer(mensaje)) return;
            // detiene este flujo e informa que los datos o la operacion no son validos
            throw new IOException("Cola de salida llena");
        }

        // este paso prepara o actualiza el estado que necesita el siguiente paso
        void enviarDesconexion(String motivo) {
            // agrupa una operacion que puede fallar para poder manejar su error
            try {
                // realiza esta operacion auxiliar para completar el paso actual
                encolar(ProtocoloRed.DESCONEXION,
                    ProtocoloRed.contenido(datos -> datos.writeUTF(motivo)), true);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } catch (IOException e) {
                // realiza esta operacion auxiliar para completar el paso actual
                cerrar();
            }
        }

        // este paso prepara o actualiza el estado que necesita el siguiente paso
        void marcarPresentado() { presentado = true; }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean estaPresentado() { return presentado; }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int getPuesto() { return puesto; }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean tieneSeleccion() { return seleccionHecha; }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean confirmoJugar() { return confirmoJugar; }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        void establecerSeleccion(TipoPersonaje tipo) { seleccion = tipo; seleccionHecha = true; }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        void confirmarJugar() { confirmoJugar = true; }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        void actualizarEntrada(int mascara, int pulsacionesNuevas) {
            // actualiza this.mascara con el valor que necesita el estado actual
            this.mascara = mascara;
            // realiza esta operacion auxiliar para completar el paso actual
            pulsaciones.getAndUpdate(actual -> actual | pulsacionesNuevas);
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        TipoPersonaje getSeleccion() { return seleccion; }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int getMascara() { return mascara; }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int consumirPulsaciones() { return pulsaciones.getAndSet(0); }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        void cerrar() {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (cerrado) return;
            // actualiza cerrado con el valor que necesita el estado actual
            cerrado = true;
            // agrupa una operacion que puede fallar para poder manejar su error
            try { socket.close(); } catch (IOException ignored) { }
        }
    }

    // esta clase agrupa la logica y los datos principales de salida
    private static final class Salida {
        // este campo representa tipo y conserva ese dato entre llamadas
        final int tipo;
        // este campo representa bytes y conserva ese dato entre llamadas
        final byte[] bytes;
        // este campo representa cerrardespues y conserva ese dato entre llamadas
        final boolean cerrarDespues;
        // realiza esta operacion auxiliar para completar el paso actual
        Salida(int tipo, byte[] bytes, boolean cerrarDespues) {
            // actualiza this.tipo con el valor que necesita el estado actual
            this.tipo = tipo;
            // actualiza this.bytes con el valor que necesita el estado actual
            this.bytes = bytes;
            // actualiza this.cerrar despues con el valor que necesita el estado actual
            this.cerrarDespues = cerrarDespues;
        }
    }
}
