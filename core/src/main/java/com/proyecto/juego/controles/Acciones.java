package com.proyecto.juego.controles;

public final class Acciones {
    public static final int IZQUIERDA = 1;
    public static final int DERECHA = 2;
    public static final int SALTAR = 4;
    public static final int DEFENDER = 8;
    public static final int BOMBA = 16;
    public static final int GOLPE = 32;
    public static final int ESPECIAL = 64;
    public static final int TODAS = 127;

    private Acciones() {
    }
}
