package com.proyecto.juego;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.proyecto.juego.visual.FondoAnimado;

public class Main extends Game {
    public FondoAnimado fondo;
    private boolean cerrando;
    private boolean salidaFinal;

    @Override
    public void setScreen(Screen siguiente) {
        Screen anterior = getScreen();
        super.setScreen(siguiente);
        if (anterior != null && anterior != siguiente) {
            anterior.dispose();
        }
    }

    @Override
    public void create() {
        fondo = new FondoAnimado();
        setScreen(new PantallaCinematica(this));
    }

    @Override
    public void render() {
        fondo.actualizar(Gdx.graphics.getDeltaTime());
        super.render();
    }

    public void salir() {
        if (cerrando) {
            return;
        }
        cerrando = true;
        setScreen(new PantallaCreditos(this));
    }

    public boolean puedeCerrar() {
        return salidaFinal;
    }

    public void terminar() {
        salidaFinal = true;
        Gdx.app.exit();
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        if (fondo != null) {
            fondo.dispose();
        }
        super.dispose();
    }
}
