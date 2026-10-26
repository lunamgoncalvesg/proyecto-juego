package com.proyecto.juego.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.proyecto.juego.Main;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        Main juego = new Main();
        Lwjgl3ApplicationConfiguration configuracion = new Lwjgl3ApplicationConfiguration();
        configuracion.setTitle("Street Fighter Ponele");
        configuracion.useVsync(true);
        configuracion.setForegroundFPS(60);
        configuracion.setWindowedMode(1280, 720);
        configuracion.setWindowSizeLimits(960, 540, -1, -1);
        configuracion.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
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
        new Lwjgl3Application(juego, configuracion);
    }
}
