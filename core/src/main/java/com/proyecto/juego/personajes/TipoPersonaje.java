package com.proyecto.juego.personajes;

// esta clase agrupa la logica y los datos principales de tipopersonaje
public enum TipoPersonaje {
    // realiza esta operacion auxiliar para completar el paso actual
    EQUILIBRADO("GATO"),
    // realiza esta operacion auxiliar para completar el paso actual
    RAPIDO("TORTUGA"),
    // realiza esta operacion auxiliar para completar el paso actual
    FUERTE("HOMBRE");

    // este campo representa nombrevisible y conserva ese dato entre llamadas
    private final String nombreVisible;

    // realiza esta operacion auxiliar para completar el paso actual
    TipoPersonaje(String nombreVisible) {
        // actualiza this.nombre visible con el valor que necesita el estado actual
        this.nombreVisible = nombreVisible;
    }

    // este metodo get nombre visible concentra una operacion de esta clase y recibe el control desde quien lo llama
    public String getNombreVisible() {
        // devuelve este resultado a quien llamo al metodo
        return nombreVisible;
    }
}
