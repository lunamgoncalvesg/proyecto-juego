package com.proyecto.juego;

import com.badlogic.gdx.Game;

public class Main extends Game {

    @Override
    public void create() {
        setScreen(new PantallaCombate());
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}