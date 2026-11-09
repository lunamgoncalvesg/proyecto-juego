package com.proyecto.juego.personajes;

public enum TipoPersonaje {
    EQUILIBRADO("GATO GUERRERO", "BOMBA DE PELOS"),
    RAPIDO("TORTUGA LUCHADORA", "RETRACCION EXPLOSIVA"),
    FUERTE("HOMBRE FUERTE", "GOLPE DE AURA");

    private final String nombreVisible;
    private final String habilidadEspecial;

    TipoPersonaje(String nombreVisible, String habilidadEspecial) {
        this.nombreVisible = nombreVisible;
        this.habilidadEspecial = habilidadEspecial;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    public String getHabilidadEspecial() {
        return habilidadEspecial;
    }
}
