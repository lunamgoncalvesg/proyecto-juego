package com.proyecto.juego.server;

import java.io.IOException;

// esta clase agrupa la logica y los datos principales de servidormain
public final class ServidorMain {
    // este campo representa puerto y conserva ese dato entre llamadas
    public static final int PUERTO = 54555;

    // este metodo servidor main concentra una operacion de esta clase y recibe el control desde quien lo llama
    private ServidorMain() {
    }

    // este metodo main concentra una operacion de esta clase y recibe el control desde quien lo llama
    public static void main(String[] args) throws IOException {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        ServidorCentral servidor = new ServidorCentral(PUERTO);
        // realiza esta operacion auxiliar para completar el paso actual
        Runtime.getRuntime().addShutdownHook(new Thread(servidor::detener, "servidor-cierre"));
        // realiza esta operacion auxiliar para completar el paso actual
        servidor.iniciar();
    }
}
