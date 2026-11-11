package com.proyecto.juego.server;

import java.io.IOException;

public final class ServidorMain {
    public static final int PUERTO = 54555;

    private ServidorMain() {
    }

    public static void main(String[] args) throws IOException {
        ServidorCentral servidor = new ServidorCentral(PUERTO);
        Runtime.getRuntime().addShutdownHook(new Thread(servidor::detener, "servidor-cierre"));
        servidor.iniciar();
    }
}
