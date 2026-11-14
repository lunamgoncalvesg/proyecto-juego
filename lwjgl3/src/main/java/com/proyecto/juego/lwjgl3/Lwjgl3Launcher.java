package com.proyecto.juego.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.proyecto.juego.Main;

// esta clase agrupa la logica y los datos principales de lwjgl3launcher
public class Lwjgl3Launcher {
    // este metodo main concentra una operacion de esta clase y recibe el control desde quien lo llama
    public static void main(String[] args) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (StartupHelper.startNewJvmIfRequired()) return;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Main juego = new Main();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Lwjgl3ApplicationConfiguration configuracion = new Lwjgl3ApplicationConfiguration();
        // realiza esta operacion auxiliar para completar el paso actual
        configuracion.setTitle("Street Fighter Ponele");
        // realiza esta operacion auxiliar para completar el paso actual
        configuracion.useVsync(true);
        // realiza esta operacion auxiliar para completar el paso actual
        configuracion.setForegroundFPS(60);
        // realiza esta operacion auxiliar para completar el paso actual
        configuracion.setWindowedMode(1280, 720);
        // realiza esta operacion auxiliar para completar el paso actual
        configuracion.setWindowSizeLimits(960, 540, -1, -1);
        // realiza esta operacion auxiliar para completar el paso actual
        configuracion.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        // realiza esta operacion auxiliar para completar el paso actual
        configuracion.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public boolean closeRequested() {
                if (juego.puedeCerrar()) return true;
                Gdx.app.postRunnable(juego::salir);
                return false;
            }

            @Override
            public void focusLost() {
                juego.pause();
            }

            @Override
            public void focusGained() {
                juego.resume();
            }
        });
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        new Lwjgl3Application(juego, configuracion);
    }
}
