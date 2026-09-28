package com.proyecto.juego.personajes;

import com.proyecto.juego.combate.Ataque;
import com.proyecto.juego.combate.TipoAtaque;

public class Personaje {

    // Datos básicos
    private String nombre;
    private float vida;

    private final float vidaMaxima = 100;
    private float velocidad;

    // Posición
    private float x;
    private float y;

    // Datos del salto
    private float velocidadVertical;
    private boolean enElAire;

    // Límites de la arena
    private float limiteIzquierdo;
    private float limiteDerecho;

    // Estado actual
    private EstadoPersonaje estado;

    // Ataques
    private Ataque ataqueSuave;
    private Ataque ataqueBomba;
    private Ataque ataqueEspecial;

    // Constructor
    public Personaje(String nombre, float velocidad) {

        this.nombre = nombre;
        this.velocidad = velocidad;

        vida = vidaMaxima;

        x = 0;
        y = 0;

        velocidadVertical = 0;
        enElAire = false;

        limiteIzquierdo = 0;
        limiteDerecho = 1200;

        estado = EstadoPersonaje.NORMAL;

        // Ataque suave
        ataqueSuave = new Ataque(
                TipoAtaque.SUAVE,
                1,
                5,
                0
        );

        // Ataque bomba
        ataqueBomba = new Ataque(
                TipoAtaque.BOMBA,
                5,
                10,
                3
        );

        // Ataque especial
        ataqueEspecial = new Ataque(
                TipoAtaque.ESPECIAL,
                10,
                15,
                15
        );
    }

    // =========================
    // DATOS BÁSICOS
    // =========================

    public String getNombre() {
        return nombre;
    }

    public float getVida() {
        return vida;
    }

    public float getVidaMaxima() {
        return vidaMaxima;
    }

    public float getVelocidad() {
        return velocidad;
    }

    // =========================
    // POSICIÓN
    // =========================

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void mover(float cantidadX) {

        x += cantidadX;

        // Evitar salir de la arena
        if (x < limiteIzquierdo) {
            x = limiteIzquierdo;
        }

        if (x > limiteDerecho) {
            x = limiteDerecho;
        }
    }

    public void establecerPosicion(float x, float y) {

        this.x = x;
        this.y = y;
    }

    public void establecerLimites(
            float izquierdo,
            float derecho) {

        limiteIzquierdo = izquierdo;
        limiteDerecho = derecho;
    }

    // =========================
    // SALTO
    // =========================

    public void saltar() {

        // No puede volver a saltar mientras está en el aire
        if (!enElAire) {

            velocidadVertical = 300;
            enElAire = true;

            estado = EstadoPersonaje.SALTANDO;
        }
    }

    public void actualizarSalto(float delta) {

        if (!enElAire) {
            return;
        }

        // Gravedad
        velocidadVertical -= 800 * delta;

        // Movimiento vertical
        y += velocidadVertical * delta;

        // Llegó nuevamente al suelo
        if (y <= 0) {

            y = 0;
            velocidadVertical = 0;
            enElAire = false;

            if (estado == EstadoPersonaje.SALTANDO) {
                estado = EstadoPersonaje.NORMAL;
            }
        }
    }

    public boolean estaEnElAire() {
        return enElAire;
    }

    // =========================
    // ESTADO
    // =========================

    public EstadoPersonaje getEstado() {
        return estado;
    }

    public void establecerEstado(
            EstadoPersonaje estado) {

        this.estado = estado;
    }

    // =========================
    // ATAQUES
    // =========================

    public Ataque getAtaqueSuave() {
        return ataqueSuave;
    }

    public Ataque getAtaqueBomba() {
        return ataqueBomba;
    }

    public Ataque getAtaqueEspecial() {
        return ataqueEspecial;
    }

    // =========================
    // VIDA
    // =========================

    public void recibirDanio(float danio) {

        vida -= danio;

        if (vida < 0) {
            vida = 0;
        }
    }

    public void recuperarVida(float cantidad) {

        vida += cantidad;

        if (vida > vidaMaxima) {
            vida = vidaMaxima;
        }
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public void reiniciarVida() {

        vida = vidaMaxima;

        y = 0;

        velocidadVertical = 0;

        enElAire = false;

        estado = EstadoPersonaje.NORMAL;
    }
}
