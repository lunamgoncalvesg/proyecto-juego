package com.proyecto.juego.controles;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import java.util.Arrays;

public class Controles extends InputAdapter {
    public static final int IZQUIERDA = Acciones.IZQUIERDA;
    public static final int DERECHA = Acciones.DERECHA;
    public static final int SALTAR = Acciones.SALTAR;
    public static final int DEFENDER = Acciones.DEFENDER;
    public static final int BOMBA = Acciones.BOMBA;
    public static final int GOLPE = Acciones.GOLPE;
    public static final int ESPECIAL = Acciones.ESPECIAL;
    public static final int TODAS_LAS_ACCIONES = Acciones.TODAS;

    private final boolean[] teclasPresionadas = new boolean[256];
    private final boolean enRed;
    private int accionesRemotas;
    private int pulsaciones;

    public Controles() {
        this(false);
    }

    public Controles(boolean enRed) {
        this.enRed = enRed;
    }

    @Override
    public boolean keyDown(int tecla) {
        if (tecla >= 0 && tecla < teclasPresionadas.length) {
            if (!teclasPresionadas[tecla]) {
                pulsaciones |= accionDeTecla(tecla);
            }
            teclasPresionadas[tecla] = true;
        }
        return true;
    }

    @Override
    public boolean keyUp(int tecla) {
        if (tecla >= 0 && tecla < teclasPresionadas.length) {
            teclasPresionadas[tecla] = false;
        }
        return true;
    }

    private int leerEsquema(int izquierda, int derecha, int salto, int defensa, int bomba, int golpe, int especial) {
        int acciones = 0;
        if (teclasPresionadas[izquierda]) {
            acciones |= IZQUIERDA;
        }
        if (teclasPresionadas[derecha]) {
            acciones |= DERECHA;
        }
        if (teclasPresionadas[salto]) {
            acciones |= SALTAR;
        }
        if (teclasPresionadas[defensa]) {
            acciones |= DEFENDER;
        }
        if (teclasPresionadas[bomba]) {
            acciones |= BOMBA;
        }
        if (teclasPresionadas[golpe]) {
            acciones |= GOLPE;
        }
        if (teclasPresionadas[especial]) {
            acciones |= ESPECIAL;
        }
        return acciones;
    }

    private int esquema1() {
        return leerEsquema(Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.S,
            Input.Keys.SPACE, Input.Keys.E, Input.Keys.R);
    }

    private int esquema2() {
        return leerEsquema(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN,
            Input.Keys.M, Input.Keys.L, Input.Keys.P);
    }

    public int mascaraJugador1() {
        if (enRed) {
            return esquema1() | esquema2();
        }
        return esquema1();
    }

    public int mascaraJugador2() {
        if (enRed) {
            return accionesRemotas;
        }
        return esquema2();
    }

    public void aplicarJugador2(int mascara) {
        accionesRemotas = mascara & TODAS_LAS_ACCIONES;
    }

    public int consumirPulsaciones() {
        int resultado = pulsaciones;
        pulsaciones = 0;
        return resultado;
    }

    private int accionDeTecla(int tecla) {
        if (tecla == Input.Keys.A || tecla == Input.Keys.LEFT) return IZQUIERDA;
        if (tecla == Input.Keys.D || tecla == Input.Keys.RIGHT) return DERECHA;
        if (tecla == Input.Keys.W || tecla == Input.Keys.UP) return SALTAR;
        if (tecla == Input.Keys.S || tecla == Input.Keys.DOWN) return DEFENDER;
        if (tecla == Input.Keys.SPACE || tecla == Input.Keys.M) return BOMBA;
        if (tecla == Input.Keys.E || tecla == Input.Keys.L) return GOLPE;
        if (tecla == Input.Keys.R || tecla == Input.Keys.P) return ESPECIAL;
        return 0;
    }

    public void limpiar() {
        Arrays.fill(teclasPresionadas, false);
        accionesRemotas = 0;
        pulsaciones = 0;
    }

    private boolean accionPresionada(int acciones, int accion) {
        return (acciones & accion) != 0;
    }

    public boolean jugador1Izquierda() {
        return accionPresionada(mascaraJugador1(), IZQUIERDA);
    }

    public boolean jugador1Derecha() {
        return accionPresionada(mascaraJugador1(), DERECHA);
    }

    public boolean jugador1Saltar() {
        return accionPresionada(mascaraJugador1(), SALTAR);
    }

    public boolean jugador1Defender() {
        return accionPresionada(mascaraJugador1(), DEFENDER);
    }

    public boolean jugador1AtaqueFuerte() {
        return accionPresionada(mascaraJugador1(), BOMBA);
    }

    public boolean jugador1AtaqueSuave() {
        return accionPresionada(mascaraJugador1(), GOLPE);
    }

    public boolean jugador1Especial() {
        return accionPresionada(mascaraJugador1(), ESPECIAL);
    }

    public boolean jugador2Izquierda() {
        return accionPresionada(mascaraJugador2(), IZQUIERDA);
    }

    public boolean jugador2Derecha() {
        return accionPresionada(mascaraJugador2(), DERECHA);
    }

    public boolean jugador2Saltar() {
        return accionPresionada(mascaraJugador2(), SALTAR);
    }

    public boolean jugador2Defender() {
        return accionPresionada(mascaraJugador2(), DEFENDER);
    }

    public boolean jugador2AtaqueFuerte() {
        return accionPresionada(mascaraJugador2(), BOMBA);
    }

    public boolean jugador2AtaqueSuave() {
        return accionPresionada(mascaraJugador2(), GOLPE);
    }

    public boolean jugador2Especial() {
        return accionPresionada(mascaraJugador2(), ESPECIAL);
    }
}
