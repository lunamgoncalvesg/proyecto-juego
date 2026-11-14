package com.proyecto.juego.combate;

// esta clase agrupa la logica y los datos principales de ataque
public class Ataque {
    // este campo representa tipo y conserva ese dato entre llamadas
    private TipoAtaque tipo;
    // este campo representa costo y conserva ese dato entre llamadas
    private float costo;
    // este campo representa danio y conserva ese dato entre llamadas
    private float danio;
    // este campo representa tiemporecarga y conserva ese dato entre llamadas
    private float tiempoRecarga;

    // este metodo ataque concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Ataque(TipoAtaque tipo, float costo, float danio, float tiempoRecarga) {
        // actualiza this.tipo con el valor que necesita el estado actual
        this.tipo = tipo;
        // actualiza this.costo con el valor que necesita el estado actual
        this.costo = costo;
        // actualiza this.danio con el valor que necesita el estado actual
        this.danio = danio;
        // actualiza this.tiempo recarga con el valor que necesita el estado actual
        this.tiempoRecarga = tiempoRecarga;
    }

    // este metodo get tipo concentra una operacion de esta clase y recibe el control desde quien lo llama
    public TipoAtaque getTipo() {
        // devuelve este resultado a quien llamo al metodo
        return tipo;
    }

    // este metodo get costo concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getCosto() {
        // devuelve este resultado a quien llamo al metodo
        return costo;
    }

    // este metodo get danio concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getDanio() {
        // devuelve este resultado a quien llamo al metodo
        return danio;
    }

    // este metodo get tiempo recarga concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getTiempoRecarga() {
        // devuelve este resultado a quien llamo al metodo
        return tiempoRecarga;
    }
}
