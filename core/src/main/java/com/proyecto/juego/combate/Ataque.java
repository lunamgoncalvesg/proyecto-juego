package com.proyecto.juego.combate;

public class Ataque {

    private TipoAtaque tipo;
    private float costo;
    private float danio;
    private float tiempoRecarga;

    public Ataque(TipoAtaque tipo, float costo, float danio, float tiempoRecarga) {
        this.tipo = tipo;
        this.costo = costo;
        this.danio = danio;
        this.tiempoRecarga = tiempoRecarga;
    }

    public TipoAtaque getTipo() {
        return tipo;
    }

    public float getCosto() {
        return costo;
    }

    public float getDanio() {
        return danio;
    }

    public float getTiempoRecarga() {
        return tiempoRecarga;
    }
}