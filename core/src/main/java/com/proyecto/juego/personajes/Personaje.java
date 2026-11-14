package com.proyecto.juego.personajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.proyecto.juego.combate.Ataque;
import com.proyecto.juego.combate.TipoAtaque;

public class Personaje {

    private String nombre;
    private float vida;
    private float estamina;
    private final float estaminaMaxima = 100;
    private final float vidaMaxima = 100;
    private float velocidad;
    private TipoPersonaje tipoPersonaje;
    private float x;
    private float y;
    private float velocidadVertical;
    private boolean enElAire;
    private float limiteIzquierdo;
    private float limiteDerecho;
    private EstadoPersonaje estado;
    private Ataque ataqueSuave;
    private Ataque ataqueBomba;
    private Ataque ataqueEspecial;

    public Personaje(String nombre, float velocidad) {
        this.nombre = nombre;
        this.velocidad = velocidad;
        this.tipoPersonaje = TipoPersonaje.EQUILIBRADO;
        vida = vidaMaxima;
        estamina = estaminaMaxima;
        x = 0;
        y = 80;
        velocidadVertical = 0;
        enElAire = false;
        limiteIzquierdo = 0;
        limiteDerecho = 1200;
        estado = EstadoPersonaje.NORMAL;
        ataqueSuave = new Ataque(TipoAtaque.SUAVE, 6, 5, 0);
        ataqueBomba = new Ataque(TipoAtaque.BOMBA, 18, 10, 3);
        ataqueEspecial = new Ataque(TipoAtaque.ESPECIAL, 35, 15, 15);
    }

    public Personaje(String nombre, TipoPersonaje tipoPersonaje) {
        this(nombre, obtenerVelocidad(tipoPersonaje));
        this.tipoPersonaje = tipoPersonaje;
        configurarAtaques(tipoPersonaje);
    }

    private static float obtenerVelocidad(TipoPersonaje tipo) {
        switch (tipo) {
        case RAPIDO:
            return 400.0F;

        case FUERTE:
            return 220.0F;

        case EQUILIBRADO:

        default:
            return 300.0F;

        }
    }

    private void configurarAtaques(TipoPersonaje tipo) {
        float multiplicadorDanio = 1;
        float recargaBomba = ataqueBomba.getTiempoRecarga();
        float recargaEspecial = ataqueEspecial.getTiempoRecarga();
        if (tipo == TipoPersonaje.RAPIDO) {
            multiplicadorDanio = 0.8f;
            recargaBomba = 2;
            recargaEspecial = 10;
        } else if (tipo == TipoPersonaje.FUERTE) {
            multiplicadorDanio = 1.4f;
            recargaBomba = 4;
            recargaEspecial = 20;
        }
        ataqueSuave = new Ataque(TipoAtaque.SUAVE, ataqueSuave.getCosto(), ataqueSuave.getDanio() * multiplicadorDanio, ataqueSuave.getTiempoRecarga());
        ataqueBomba = new Ataque(TipoAtaque.BOMBA, ataqueBomba.getCosto(), ataqueBomba.getDanio() * multiplicadorDanio, recargaBomba);
        ataqueEspecial = new Ataque(TipoAtaque.ESPECIAL, ataqueEspecial.getCosto(), ataqueEspecial.getDanio() * multiplicadorDanio, recargaEspecial);
    }

    public TipoPersonaje getTipoPersonaje() {
        return tipoPersonaje;
    }

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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void mover(float cantidadX) {
        x += cantidadX;
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

    public void establecerLimites(float izquierdo, float derecho) {
        limiteIzquierdo = izquierdo;
        limiteDerecho = derecho;
    }

    public void saltar() {
        if (!enElAire) {
            velocidadVertical = 500;
            enElAire = true;
            estado = EstadoPersonaje.SALTANDO;
        }
    }

    public void actualizarSalto(float delta) {
        if (!enElAire) {
            return;
        }
        velocidadVertical -= 800 * delta;
        y += velocidadVertical * delta;
        if (y <= 80) {
            y = 80;
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

    public EstadoPersonaje getEstado() {
        return estado;
    }

    public void establecerEstado(EstadoPersonaje estado) {
        this.estado = estado;
    }

    public Ataque getAtaqueSuave() {
        return ataqueSuave;
    }

    public Ataque getAtaqueBomba() {
        return ataqueBomba;
    }

    public Ataque getAtaqueEspecial() {
        return ataqueEspecial;
    }

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
        estamina = estaminaMaxima;
        y = 80;
        velocidadVertical = 0;
        enElAire = false;
        estado = EstadoPersonaje.NORMAL;
    }

    public float getEstamina() {
        return estamina;
    }

    public float getEstaminaMaxima() {
        return estaminaMaxima;
    }

    public boolean tieneEstamina(float cantidad) {
        return estamina >= cantidad;
    }

    public void gastarEstamina(float cantidad) {
        estamina -= cantidad;
        if (estamina < 0) {
            estamina = 0;
        }
    }

    public void recuperarEstamina(float cantidad) {
        estamina += cantidad;
        if (estamina > estaminaMaxima) {
            estamina = estaminaMaxima;
        }
    }

    public void escribir(DataOutputStream datos) throws IOException {
        datos.writeFloat(x);
        datos.writeFloat(y);
        datos.writeFloat(vida);
        datos.writeFloat(estamina);
        datos.writeInt(estado.ordinal());
    }

    public void leer(DataInputStream datos) throws IOException {
        x = datos.readFloat();
        y = datos.readFloat();
        vida = datos.readFloat();
        estamina = datos.readFloat();
        int ordinal = datos.readInt();
        if (ordinal < 0 || ordinal >= EstadoPersonaje.values().length) {
            throw new IOException("Estado de personaje invalido");
        }
        estado = EstadoPersonaje.values()[ordinal];
    }
}
