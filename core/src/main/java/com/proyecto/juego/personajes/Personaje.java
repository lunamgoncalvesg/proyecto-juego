package com.proyecto.juego.personajes;

import com.proyecto.juego.combate.Ataque;
import com.proyecto.juego.combate.TipoAtaque;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// esta clase agrupa la logica y los datos principales de personaje
public class Personaje {

    // este campo representa nombre y conserva ese dato entre llamadas
    private String nombre;
    // este campo representa vida y conserva ese dato entre llamadas
    private float vida;
    // este campo representa estamina y conserva ese dato entre llamadas
    private float estamina;
    // este campo representa estaminamaxima y conserva ese dato entre llamadas
    private final float estaminaMaxima = 100;
    // este campo representa vidamaxima y conserva ese dato entre llamadas
    private final float vidaMaxima = 100;
    // este campo representa velocidad y conserva ese dato entre llamadas
    private float velocidad;
    // este campo representa tipopersonaje y conserva ese dato entre llamadas
    private TipoPersonaje tipoPersonaje;
    // este campo representa x y conserva ese dato entre llamadas
    private float x;
    // este campo representa y y conserva ese dato entre llamadas
    private float y;
    // este campo representa velocidadvertical y conserva ese dato entre llamadas
    private float velocidadVertical;
    // este campo representa enelaire y conserva ese dato entre llamadas
    private boolean enElAire;
    // este campo representa limiteizquierdo y conserva ese dato entre llamadas
    private float limiteIzquierdo;
    // este campo representa limitederecho y conserva ese dato entre llamadas
    private float limiteDerecho;
    // este campo representa estado y conserva ese dato entre llamadas
    private EstadoPersonaje estado;
    // este campo representa ataquesuave y conserva ese dato entre llamadas
    private Ataque ataqueSuave;
    // este campo representa ataquebomba y conserva ese dato entre llamadas
    private Ataque ataqueBomba;
    // este campo representa ataqueespecial y conserva ese dato entre llamadas
    private Ataque ataqueEspecial;

    // este metodo personaje concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Personaje(String nombre, float velocidad) {
        // actualiza this.nombre con el valor que necesita el estado actual
        this.nombre = nombre;
        // actualiza this.velocidad con el valor que necesita el estado actual
        this.velocidad = velocidad;
        // actualiza this.tipo personaje con el valor que necesita el estado actual
        this.tipoPersonaje = TipoPersonaje.EQUILIBRADO;
        // actualiza vida con el valor que necesita el estado actual
        vida = vidaMaxima;
        // actualiza estamina con el valor que necesita el estado actual
        estamina = estaminaMaxima;
        // actualiza x con el valor que necesita el estado actual
        x = 0;
        // actualiza y con el valor que necesita el estado actual
        y = 80;
        // actualiza velocidad vertical con el valor que necesita el estado actual
        velocidadVertical = 0;
        // actualiza en el aire con el valor que necesita el estado actual
        enElAire = false;
        // actualiza limite izquierdo con el valor que necesita el estado actual
        limiteIzquierdo = 0;
        // actualiza limite derecho con el valor que necesita el estado actual
        limiteDerecho = 1200;
        // actualiza estado con el valor que necesita el estado actual
        estado = EstadoPersonaje.NORMAL;
        // actualiza ataque suave con el valor que necesita el estado actual
        ataqueSuave = new Ataque(TipoAtaque.SUAVE, 1, 5, 0);
        // actualiza ataque bomba con el valor que necesita el estado actual
        ataqueBomba = new Ataque(TipoAtaque.BOMBA, 5, 10, 3);
        // actualiza ataque especial con el valor que necesita el estado actual
        ataqueEspecial = new Ataque(TipoAtaque.ESPECIAL, 10, 15, 15);
    }

    // este metodo personaje concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Personaje(String nombre, TipoPersonaje tipoPersonaje) {
        // realiza esta operacion auxiliar para completar el paso actual
        this(nombre, obtenerVelocidad(tipoPersonaje));
        // actualiza this.tipo personaje con el valor que necesita el estado actual
        this.tipoPersonaje = tipoPersonaje;
        // realiza esta operacion auxiliar para completar el paso actual
        configurarAtaques(tipoPersonaje);
    }

    // este metodo devuelve la velocidad base correspondiente al tipo de personaje
    private static float obtenerVelocidad(TipoPersonaje tipo) {
        // elige una ruta distinta segun el valor que se esta evaluando
        switch (tipo) {
        // atiende este valor posible de la seleccion anterior
        case RAPIDO:
            // devuelve este resultado a quien llamo al metodo
            return 400.0F;

        // atiende este valor posible de la seleccion anterior
        case FUERTE:
            // devuelve este resultado a quien llamo al metodo
            return 220.0F;

        // atiende este valor posible de la seleccion anterior
        case EQUILIBRADO:

        // este paso prepara o actualiza el estado que necesita el siguiente paso
        default:
            // devuelve este resultado a quien llamo al metodo
            return 300.0F;

        }
    }

    // este metodo ajusta el danio y el tiempo de recarga segun el tipo elegido
    private void configurarAtaques(TipoPersonaje tipo) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float multiplicadorDanio = 1;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float recargaBomba = 3;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float recargaEspecial = 15;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tipo == TipoPersonaje.RAPIDO) {
            // actualiza multiplicador danio con el valor que necesita el estado actual
            multiplicadorDanio = 0.8f;
            // actualiza recarga bomba con el valor que necesita el estado actual
            recargaBomba = 2;
            // actualiza recarga especial con el valor que necesita el estado actual
            recargaEspecial = 10;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else if (tipo == TipoPersonaje.FUERTE) {
            // actualiza multiplicador danio con el valor que necesita el estado actual
            multiplicadorDanio = 1.4f;
            // actualiza recarga bomba con el valor que necesita el estado actual
            recargaBomba = 4;
            // actualiza recarga especial con el valor que necesita el estado actual
            recargaEspecial = 20;
        }
        // actualiza ataque suave con el valor que necesita el estado actual
        ataqueSuave = new Ataque(TipoAtaque.SUAVE, 1, 5 * multiplicadorDanio, 0);
        // actualiza ataque bomba con el valor que necesita el estado actual
        ataqueBomba = new Ataque(TipoAtaque.BOMBA, 5, 10 * multiplicadorDanio, recargaBomba);
        // actualiza ataque especial con el valor que necesita el estado actual
        ataqueEspecial = new Ataque(TipoAtaque.ESPECIAL, 10, 15 * multiplicadorDanio, recargaEspecial);
    }

    // este metodo get tipo personaje concentra una operacion de esta clase y recibe el control desde quien lo llama
    public TipoPersonaje getTipoPersonaje() {
        // devuelve este resultado a quien llamo al metodo
        return tipoPersonaje;
    }

    // este metodo get nombre concentra una operacion de esta clase y recibe el control desde quien lo llama
    public String getNombre() {
        // devuelve este resultado a quien llamo al metodo
        return nombre;
    }

    // este metodo get vida concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getVida() {
        // devuelve este resultado a quien llamo al metodo
        return vida;
    }

    // este metodo get vida maxima concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getVidaMaxima() {
        // devuelve este resultado a quien llamo al metodo
        return vidaMaxima;
    }

    // este metodo get velocidad concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getVelocidad() {
        // devuelve este resultado a quien llamo al metodo
        return velocidad;
    }

    // este metodo get x concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getX() {
        // devuelve este resultado a quien llamo al metodo
        return x;
    }

    // este metodo get y concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getY() {
        // devuelve este resultado a quien llamo al metodo
        return y;
    }

    // este metodo mover concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void mover(float cantidadX) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        x += cantidadX;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (x < limiteIzquierdo) {
            // actualiza x con el valor que necesita el estado actual
            x = limiteIzquierdo;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (x > limiteDerecho) {
            // actualiza x con el valor que necesita el estado actual
            x = limiteDerecho;
        }
    }

    // este metodo establecer posicion concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void establecerPosicion(float x, float y) {
        // actualiza this.x con el valor que necesita el estado actual
        this.x = x;
        // actualiza this.y con el valor que necesita el estado actual
        this.y = y;
    }

    // este metodo establecer limites concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void establecerLimites(float izquierdo, float derecho) {
        // actualiza limite izquierdo con el valor que necesita el estado actual
        limiteIzquierdo = izquierdo;
        // actualiza limite derecho con el valor que necesita el estado actual
        limiteDerecho = derecho;
    }

    // este metodo saltar concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void saltar() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (!enElAire) {
            // actualiza velocidad vertical con el valor que necesita el estado actual
            velocidadVertical = 500;
            // actualiza en el aire con el valor que necesita el estado actual
            enElAire = true;
            // actualiza estado con el valor que necesita el estado actual
            estado = EstadoPersonaje.SALTANDO;
        }
    }

    // este metodo simula el salto con gravedad y aterriza al volver al piso
    public void actualizarSalto(float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (!enElAire) {
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        velocidadVertical -= 800 * delta;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        y += velocidadVertical * delta;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (y <= 80) {
            // actualiza y con el valor que necesita el estado actual
            y = 80;
            // actualiza velocidad vertical con el valor que necesita el estado actual
            velocidadVertical = 0;
            // actualiza en el aire con el valor que necesita el estado actual
            enElAire = false;
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (estado == EstadoPersonaje.SALTANDO) {
                // actualiza estado con el valor que necesita el estado actual
                estado = EstadoPersonaje.NORMAL;
            }
        }
    }

    // este metodo esta en el aire concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean estaEnElAire() {
        // devuelve este resultado a quien llamo al metodo
        return enElAire;
    }

    // este metodo get estado concentra una operacion de esta clase y recibe el control desde quien lo llama
    public EstadoPersonaje getEstado() {
        // devuelve este resultado a quien llamo al metodo
        return estado;
    }

    // este metodo establecer estado concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void establecerEstado(EstadoPersonaje estado) {
        // actualiza this.estado con el valor que necesita el estado actual
        this.estado = estado;
    }

    // este metodo get ataque suave concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Ataque getAtaqueSuave() {
        // devuelve este resultado a quien llamo al metodo
        return ataqueSuave;
    }

    // este metodo get ataque bomba concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Ataque getAtaqueBomba() {
        // devuelve este resultado a quien llamo al metodo
        return ataqueBomba;
    }

    // este metodo get ataque especial concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Ataque getAtaqueEspecial() {
        // devuelve este resultado a quien llamo al metodo
        return ataqueEspecial;
    }

    // este metodo reduce la vida sin dejar que baje de cero
    public void recibirDanio(float danio) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        vida -= danio;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (vida < 0) {
            // actualiza vida con el valor que necesita el estado actual
            vida = 0;
        }
    }

    // este metodo recuperar vida concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void recuperarVida(float cantidad) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        vida += cantidad;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (vida > vidaMaxima) {
            // actualiza vida con el valor que necesita el estado actual
            vida = vidaMaxima;
        }
    }

    // este metodo esta vivo concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean estaVivo() {
        // devuelve este resultado a quien llamo al metodo
        return vida > 0;
    }

    // este metodo reiniciar vida concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void reiniciarVida() {
        // actualiza vida con el valor que necesita el estado actual
        vida = vidaMaxima;
        // actualiza estamina con el valor que necesita el estado actual
        estamina = estaminaMaxima;
        // actualiza y con el valor que necesita el estado actual
        y = 80;
        // actualiza velocidad vertical con el valor que necesita el estado actual
        velocidadVertical = 0;
        // actualiza en el aire con el valor que necesita el estado actual
        enElAire = false;
        // actualiza estado con el valor que necesita el estado actual
        estado = EstadoPersonaje.NORMAL;
    }

    // este metodo get estamina concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getEstamina() {
        // devuelve este resultado a quien llamo al metodo
        return estamina;
    }

    // este metodo get estamina maxima concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getEstaminaMaxima() {
        // devuelve este resultado a quien llamo al metodo
        return estaminaMaxima;
    }

    // este metodo tiene estamina concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean tieneEstamina(float cantidad) {
        // devuelve este resultado a quien llamo al metodo
        return estamina >= cantidad;
    }

    // este metodo gastar estamina concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void gastarEstamina(float cantidad) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        estamina -= cantidad;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (estamina < 0) {
            // actualiza estamina con el valor que necesita el estado actual
            estamina = 0;
        }
    }

    // este metodo suma energia hasta el maximo permitido
    public void recuperarEstamina(float cantidad) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        estamina += cantidad;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (estamina > estaminaMaxima) {
            // actualiza estamina con el valor que necesita el estado actual
            estamina = estaminaMaxima;
        }
    }

    // este metodo escribir concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void escribir(DataOutputStream datos) throws IOException {
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeFloat(x);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeFloat(y);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeFloat(vida);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeFloat(estamina);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeInt(estado.ordinal());
    }

    // este metodo leer concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void leer(DataInputStream datos) throws IOException {
        // actualiza x con el valor que necesita el estado actual
        x = datos.readFloat();
        // actualiza y con el valor que necesita el estado actual
        y = datos.readFloat();
        // actualiza vida con el valor que necesita el estado actual
        vida = datos.readFloat();
        // actualiza estamina con el valor que necesita el estado actual
        estamina = datos.readFloat();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int ordinal = datos.readInt();
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (ordinal < 0 || ordinal >= EstadoPersonaje.values().length) {
            // detiene este flujo e informa que los datos o la operacion no son validos
            throw new IOException("Estado de personaje invalido");
        }
        // actualiza estado con el valor que necesita el estado actual
        estado = EstadoPersonaje.values()[ordinal];
    }
}
