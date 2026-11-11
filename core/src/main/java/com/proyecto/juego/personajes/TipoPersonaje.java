package com.proyecto.juego.personajes;

public enum TipoPersonaje {
    EQUILIBRADO("GATO"),
    RAPIDO("TORTUGA"),
    FUERTE("HOMBRE");

    private final String nombreVisible;

    TipoPersonaje(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }
}
