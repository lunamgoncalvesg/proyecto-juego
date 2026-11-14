package com.proyecto.juego.controles;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import java.util.Arrays;

// esta clase agrupa la logica y los datos principales de controles
public class Controles extends InputAdapter {
    // este campo representa izquierda y conserva ese dato entre llamadas
    public static final int IZQUIERDA = Acciones.IZQUIERDA;
    // este campo representa derecha y conserva ese dato entre llamadas
    public static final int DERECHA = Acciones.DERECHA;
    // este campo representa saltar y conserva ese dato entre llamadas
    public static final int SALTAR = Acciones.SALTAR;
    // este campo representa defender y conserva ese dato entre llamadas
    public static final int DEFENDER = Acciones.DEFENDER;
    // este campo representa bomba y conserva ese dato entre llamadas
    public static final int BOMBA = Acciones.BOMBA;
    // este campo representa golpe y conserva ese dato entre llamadas
    public static final int GOLPE = Acciones.GOLPE;
    // este campo representa especial y conserva ese dato entre llamadas
    public static final int ESPECIAL = Acciones.ESPECIAL;
    // este campo representa todas las acciones y conserva ese dato entre llamadas
    public static final int TODAS_LAS_ACCIONES = Acciones.TODAS;

    // este campo representa teclaspresionadas y conserva ese dato entre llamadas
    private final boolean[] teclasPresionadas = new boolean[256];
    // este campo representa enred y conserva ese dato entre llamadas
    private final boolean enRed;
    // este campo representa accionesremotas y conserva ese dato entre llamadas
    private int accionesRemotas;
    // este campo representa pulsaciones y conserva ese dato entre llamadas
    private int pulsaciones;

    // este metodo controles concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Controles() {
        // realiza esta operacion auxiliar para completar el paso actual
        this(false);
    }

    // este metodo controles concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Controles(boolean enRed) {
        // actualiza this.en red con el valor que necesita el estado actual
        this.enRed = enRed;
    }

    @Override
    // este metodo key down concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean keyDown(int tecla) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla >= 0 && tecla < teclasPresionadas.length) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (!teclasPresionadas[tecla]) {
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                pulsaciones |= accionDeTecla(tecla);
            }
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            teclasPresionadas[tecla] = true;
        }
        // devuelve este resultado a quien llamo al metodo
        return true;
    }

    @Override
    // este metodo key up concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean keyUp(int tecla) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla >= 0 && tecla < teclasPresionadas.length) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            teclasPresionadas[tecla] = false;
        }
        // devuelve este resultado a quien llamo al metodo
        return true;
    }

    // este metodo leer esquema concentra una operacion de esta clase y recibe el control desde quien lo llama
    private int leerEsquema(int izquierda, int derecha, int salto, int defensa, int bomba, int golpe, int especial) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int acciones = 0;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (teclasPresionadas[izquierda]) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            acciones |= IZQUIERDA;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (teclasPresionadas[derecha]) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            acciones |= DERECHA;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (teclasPresionadas[salto]) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            acciones |= SALTAR;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (teclasPresionadas[defensa]) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            acciones |= DEFENDER;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (teclasPresionadas[bomba]) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            acciones |= BOMBA;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (teclasPresionadas[golpe]) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            acciones |= GOLPE;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (teclasPresionadas[especial]) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            acciones |= ESPECIAL;
        }
        // devuelve este resultado a quien llamo al metodo
        return acciones;
    }

    // este metodo esquema1 concentra una operacion de esta clase y recibe el control desde quien lo llama
    private int esquema1() {
        // devuelve este resultado a quien llamo al metodo
        return leerEsquema(Input.Keys.A, Input.Keys.D, Input.Keys.W, Input.Keys.S,
            Input.Keys.SPACE, Input.Keys.E, Input.Keys.R);
    }

    // este metodo esquema2 concentra una operacion de esta clase y recibe el control desde quien lo llama
    private int esquema2() {
        // devuelve este resultado a quien llamo al metodo
        return leerEsquema(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN,
            Input.Keys.M, Input.Keys.L, Input.Keys.P);
    }

    // este metodo mascara jugador1 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public int mascaraJugador1() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (enRed) {
            // devuelve este resultado a quien llamo al metodo
            return esquema1() | esquema2();
        }
        // devuelve este resultado a quien llamo al metodo
        return esquema1();
    }

    // este metodo mascara jugador2 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public int mascaraJugador2() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (enRed) {
            // devuelve este resultado a quien llamo al metodo
            return accionesRemotas;
        }
        // devuelve este resultado a quien llamo al metodo
        return esquema2();
    }

    // este metodo aplicar jugador2 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void aplicarJugador2(int mascara) {
        // actualiza acciones remotas con el valor que necesita el estado actual
        accionesRemotas = mascara & TODAS_LAS_ACCIONES;
    }

    // este metodo consumir pulsaciones concentra una operacion de esta clase y recibe el control desde quien lo llama
    public int consumirPulsaciones() {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int resultado = pulsaciones;
        // actualiza pulsaciones con el valor que necesita el estado actual
        pulsaciones = 0;
        // devuelve este resultado a quien llamo al metodo
        return resultado;
    }

    // este metodo accion de tecla concentra una operacion de esta clase y recibe el control desde quien lo llama
    private int accionDeTecla(int tecla) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla == Input.Keys.A || tecla == Input.Keys.LEFT) return IZQUIERDA;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla == Input.Keys.D || tecla == Input.Keys.RIGHT) return DERECHA;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla == Input.Keys.W || tecla == Input.Keys.UP) return SALTAR;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla == Input.Keys.S || tecla == Input.Keys.DOWN) return DEFENDER;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla == Input.Keys.SPACE || tecla == Input.Keys.M) return BOMBA;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla == Input.Keys.E || tecla == Input.Keys.L) return GOLPE;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla == Input.Keys.R || tecla == Input.Keys.P) return ESPECIAL;
        // devuelve este resultado a quien llamo al metodo
        return 0;
    }

    // este metodo limpiar concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void limpiar() {
        // realiza esta operacion auxiliar para completar el paso actual
        Arrays.fill(teclasPresionadas, false);
        // actualiza acciones remotas con el valor que necesita el estado actual
        accionesRemotas = 0;
        // actualiza pulsaciones con el valor que necesita el estado actual
        pulsaciones = 0;
    }

    // este metodo accion presionada concentra una operacion de esta clase y recibe el control desde quien lo llama
    private boolean accionPresionada(int acciones, int accion) {
        // devuelve este resultado a quien llamo al metodo
        return (acciones & accion) != 0;
    }

    // este metodo jugador1 izquierda concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador1Izquierda() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador1(), IZQUIERDA);
    }

    // este metodo jugador1 derecha concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador1Derecha() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador1(), DERECHA);
    }

    // este metodo jugador1 saltar concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador1Saltar() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador1(), SALTAR);
    }

    // este metodo jugador1 defender concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador1Defender() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador1(), DEFENDER);
    }

    // este metodo jugador1 ataque fuerte concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador1AtaqueFuerte() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador1(), BOMBA);
    }

    // este metodo jugador1 ataque suave concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador1AtaqueSuave() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador1(), GOLPE);
    }

    // este metodo jugador1 especial concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador1Especial() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador1(), ESPECIAL);
    }

    // este metodo jugador2 izquierda concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador2Izquierda() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador2(), IZQUIERDA);
    }

    // este metodo jugador2 derecha concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador2Derecha() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador2(), DERECHA);
    }

    // este metodo jugador2 saltar concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador2Saltar() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador2(), SALTAR);
    }

    // este metodo jugador2 defender concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador2Defender() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador2(), DEFENDER);
    }

    // este metodo jugador2 ataque fuerte concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador2AtaqueFuerte() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador2(), BOMBA);
    }

    // este metodo jugador2 ataque suave concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador2AtaqueSuave() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador2(), GOLPE);
    }

    // este metodo jugador2 especial concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean jugador2Especial() {
        // devuelve este resultado a quien llamo al metodo
        return accionPresionada(mascaraJugador2(), ESPECIAL);
    }
}
