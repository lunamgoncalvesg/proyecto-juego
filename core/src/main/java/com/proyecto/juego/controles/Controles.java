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

    private static final int[] ACCIONES = {IZQUIERDA, DERECHA, SALTAR, DEFENDER, BOMBA, GOLPE, ESPECIAL};
    private static final int[] TECLAS_JUGADOR1 = {
        Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.S, Input.Keys.SPACE, Input.Keys.E, Input.Keys.R
    };
    private static final int[] TECLAS_JUGADOR2 = {
        Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.M, Input.Keys.L, Input.Keys.P
    };

    private final boolean[] teclasPresionadas = new boolean[256];
    private final boolean enRed;
    private int accionesRemotas;
    private int pulsaciones;
    private int pulsacionesJugador2;

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
                pulsaciones |= accionDeTecla(tecla, TECLAS_JUGADOR1);
                int accionJugador2 = accionDeTecla(tecla, TECLAS_JUGADOR2);
                if (enRed) {
                    pulsaciones |= accionJugador2;
                } else {
                    pulsacionesJugador2 |= accionJugador2;
                }
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

    private int leerEsquema(int[] teclas) {
        int acciones = 0;
        for (int i = 0; i < teclas.length; i++) {
            if (teclasPresionadas[teclas[i]]) {
                acciones |= ACCIONES[i];
            }
        }
        return acciones;
    }

    public int mascaraJugador1() {
        if (enRed) {
            return leerEsquema(TECLAS_JUGADOR1) | leerEsquema(TECLAS_JUGADOR2);
        }
        return leerEsquema(TECLAS_JUGADOR1);
    }

    public int mascaraJugador2() {
        if (enRed) {
            return accionesRemotas;
        }
        return leerEsquema(TECLAS_JUGADOR2);
    }

    public void aplicarJugador2(int mascara) {
        accionesRemotas = mascara & TODAS_LAS_ACCIONES;
    }

    public int consumirPulsaciones() {
        int resultado = pulsaciones;
        pulsaciones = 0;
        return resultado;
    }

    public int consumirPulsacionesJugador2() {
        int resultado = pulsacionesJugador2;
        pulsacionesJugador2 = 0;
        return resultado;
    }

    private int accionDeTecla(int tecla, int[] teclas) {
        for (int i = 0; i < teclas.length; i++) {
            if (teclas[i] == tecla) {
                return ACCIONES[i];
            }
        }
        return 0;
    }

    public void limpiar() {
        Arrays.fill(teclasPresionadas, false);
        accionesRemotas = 0;
        pulsaciones = 0;
        pulsacionesJugador2 = 0;
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
