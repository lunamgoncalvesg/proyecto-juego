package com.proyecto.juego.controles;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class Controles extends InputAdapter {

// =========================
// JUGADOR 1
// =========================

private boolean jugador1Izquierda;
private boolean jugador1Derecha;
private boolean jugador1Saltar;
private boolean jugador1Defender;

private boolean jugador1AtaqueFuerte;
private boolean jugador1AtaqueSuave;
private boolean jugador1Especial;

// =========================
// JUGADOR 2
// =========================

private boolean jugador2Izquierda;
private boolean jugador2Derecha;
private boolean jugador2Saltar;
private boolean jugador2Defender;

private boolean jugador2AtaqueFuerte;
private boolean jugador2AtaqueSuave;
private boolean jugador2Especial;

// =========================
// TECLAS PRESIONADAS
// =========================

@Override
public boolean keyDown(int keycode) {

    // -------------------------
    // JUGADOR 1
    // -------------------------

    if (keycode == Input.Keys.A) {
        jugador1Izquierda = true;
    }

    if (keycode == Input.Keys.D) {
        jugador1Derecha = true;
    }

    if (keycode == Input.Keys.W) {
        jugador1Saltar = true;
    }

    if (keycode == Input.Keys.S) {
        jugador1Defender = true;
    }

    if (keycode == Input.Keys.SPACE) {
        jugador1AtaqueFuerte = true;
    }

    if (keycode == Input.Keys.E) {
        jugador1AtaqueSuave = true;
    }

    if (keycode == Input.Keys.R) {
        jugador1Especial = true;
    }

    // -------------------------
    // JUGADOR 2
    // -------------------------

    if (keycode == Input.Keys.LEFT) {
        jugador2Izquierda = true;
    }

    if (keycode == Input.Keys.RIGHT) {
        jugador2Derecha = true;
    }

    if (keycode == Input.Keys.UP) {
        jugador2Saltar = true;
    }

    if (keycode == Input.Keys.DOWN) {
        jugador2Defender = true;
    }

    if (keycode == Input.Keys.M) {
        jugador2AtaqueFuerte = true;
    }

    if (keycode == Input.Keys.L) {
        jugador2AtaqueSuave = true;
    }

    if (keycode == Input.Keys.P) {
        jugador2Especial = true;
    }

    return true;
}

// =========================
// TECLAS SOLTADAS
// =========================

@Override
public boolean keyUp(int keycode) {

    // -------------------------
    // JUGADOR 1
    // -------------------------

    if (keycode == Input.Keys.A) {
        jugador1Izquierda = false;
    }

    if (keycode == Input.Keys.D) {
        jugador1Derecha = false;
    }

    if (keycode == Input.Keys.W) {
        jugador1Saltar = false;
    }

    if (keycode == Input.Keys.S) {
        jugador1Defender = false;
    }

    if (keycode == Input.Keys.SPACE) {
        jugador1AtaqueFuerte = false;
    }

    if (keycode == Input.Keys.E) {
        jugador1AtaqueSuave = false;
    }

    if (keycode == Input.Keys.R) {
        jugador1Especial = false;
    }

    // -------------------------
    // JUGADOR 2
    // -------------------------

    if (keycode == Input.Keys.LEFT) {
        jugador2Izquierda = false;
    }

    if (keycode == Input.Keys.RIGHT) {
        jugador2Derecha = false;
    }

    if (keycode == Input.Keys.UP) {
        jugador2Saltar = false;
    }

    if (keycode == Input.Keys.DOWN) {
        jugador2Defender = false;
    }

    if (keycode == Input.Keys.M) {
        jugador2AtaqueFuerte = false;
    }

    if (keycode == Input.Keys.L) {
        jugador2AtaqueSuave = false;
    }

    if (keycode == Input.Keys.P) {
        jugador2Especial = false;
    }

    return true;
}

// =========================
// JUGADOR 1
// =========================

public boolean jugador1Izquierda() {
    return jugador1Izquierda;
}

public boolean jugador1Derecha() {
    return jugador1Derecha;
}

public boolean jugador1Saltar() {
    return jugador1Saltar;
}

public boolean jugador1Defender() {
    return jugador1Defender;
}

public boolean jugador1AtaqueFuerte() {
    return jugador1AtaqueFuerte;
}

public boolean jugador1AtaqueSuave() {
    return jugador1AtaqueSuave;
}

public boolean jugador1Especial() {
    return jugador1Especial;
}

// =========================
// JUGADOR 2
// =========================

public boolean jugador2Izquierda() {
    return jugador2Izquierda;
}

public boolean jugador2Derecha() {
    return jugador2Derecha;
}

public boolean jugador2Saltar() {
    return jugador2Saltar;
}

public boolean jugador2Defender() {
    return jugador2Defender;
}

public boolean jugador2AtaqueFuerte() {
    return jugador2AtaqueFuerte;
}

public boolean jugador2AtaqueSuave() {
    return jugador2AtaqueSuave;
}

public boolean jugador2Especial() {
    return jugador2Especial;
}

}