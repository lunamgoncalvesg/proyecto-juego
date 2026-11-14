package com.proyecto.juego.controles;

// esta clase agrupa la logica y los datos principales de acciones
public final class Acciones {
    // este campo representa izquierda y conserva ese dato entre llamadas
    public static final int IZQUIERDA = 1;
    // este campo representa derecha y conserva ese dato entre llamadas
    public static final int DERECHA = 2;
    // este campo representa saltar y conserva ese dato entre llamadas
    public static final int SALTAR = 4;
    // este campo representa defender y conserva ese dato entre llamadas
    public static final int DEFENDER = 8;
    // este campo representa bomba y conserva ese dato entre llamadas
    public static final int BOMBA = 16;
    // este campo representa golpe y conserva ese dato entre llamadas
    public static final int GOLPE = 32;
    // este campo representa especial y conserva ese dato entre llamadas
    public static final int ESPECIAL = 64;
    // este campo representa todas y conserva ese dato entre llamadas
    public static final int TODAS = 127;

    // este metodo acciones concentra una operacion de esta clase y recibe el control desde quien lo llama
    private Acciones() {
    }
}
