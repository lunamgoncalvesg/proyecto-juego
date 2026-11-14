package com.proyecto.juego.server;

import com.proyecto.juego.combate.Combate;
import com.proyecto.juego.combate.ControladorCombate;
import com.proyecto.juego.red.ProtocoloRed;
import com.proyecto.juego.personajes.Personaje;
import com.proyecto.juego.personajes.TipoPersonaje;

import java.io.DataOutputStream;
import java.io.IOException;

// esta clase agrupa la logica y los datos principales de partida
final class Partida {
    // este campo representa cuenta regresiva y conserva ese dato entre llamadas
    static final int CUENTA_REGRESIVA = 0;
    // este campo representa combate y conserva ese dato entre llamadas
    static final int COMBATE = 1;
    // este campo representa resultado ronda y conserva ese dato entre llamadas
    static final int RESULTADO_RONDA = 2;
    // este campo representa finalizada y conserva ese dato entre llamadas
    static final int FINALIZADA = 3;
    // este campo representa tick y conserva ese dato entre llamadas
    private static final float TICK = 1f / 60f;

    // este campo representa tipo1 y conserva ese dato entre llamadas
    private final TipoPersonaje tipo1;
    // este campo representa tipo2 y conserva ese dato entre llamadas
    private final TipoPersonaje tipo2;
    // este campo representa jugador1 y conserva ese dato entre llamadas
    private final Personaje jugador1;
    // este campo representa jugador2 y conserva ese dato entre llamadas
    private final Personaje jugador2;
    // este campo representa combate y conserva ese dato entre llamadas
    private final Combate combate;
    // este campo representa controlador y conserva ese dato entre llamadas
    private final ControladorCombate controlador;
    // este campo representa poses y conserva ese dato entre llamadas
    private final int[] poses = new int[2];
    // este campo representa tiempospose y conserva ese dato entre llamadas
    private final float[] tiemposPose = new float[2];
    // este campo representa caminando y conserva ese dato entre llamadas
    private final boolean[] caminando = new boolean[2];
    // este campo representa fase y conserva ese dato entre llamadas
    private int fase = CUENTA_REGRESIVA;
    // este campo representa ganadorronda y conserva ese dato entre llamadas
    private int ganadorRonda;
    // este campo representa tiempofase y conserva ese dato entre llamadas
    private float tiempoFase = 3f;

    // realiza esta operacion auxiliar para completar el paso actual
    Partida(TipoPersonaje tipo1, TipoPersonaje tipo2) {
        // actualiza this.tipo1 con el valor que necesita el estado actual
        this.tipo1 = tipo1;
        // actualiza this.tipo2 con el valor que necesita el estado actual
        this.tipo2 = tipo2;
        // actualiza jugador1 con el valor que necesita el estado actual
        jugador1 = new Personaje("Jugador 1", tipo1);
        // actualiza jugador2 con el valor que necesita el estado actual
        jugador2 = new Personaje("Jugador 2", tipo2);
        // realiza esta operacion auxiliar para completar el paso actual
        jugador1.establecerLimites(20, 960);
        // realiza esta operacion auxiliar para completar el paso actual
        jugador2.establecerLimites(20, 960);
        // actualiza combate con el valor que necesita el estado actual
        combate = new Combate(jugador1, jugador2);
        // actualiza controlador con el valor que necesita el estado actual
        controlador = new ControladorCombate(combate);
        // realiza esta operacion auxiliar para completar el paso actual
        ubicarJugadores();
    }

    // este paso prepara o actualiza el estado que necesita el siguiente paso
    void actualizar(int mascara1, int mascara2, int pulsaciones1, int pulsaciones2) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (fase == FINALIZADA) return;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (fase == CUENTA_REGRESIVA) {
            // actualiza tiempo fase con el valor que necesita el estado actual
            tiempoFase = Math.max(0, tiempoFase - TICK);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (tiempoFase == 0) fase = COMBATE;
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (fase == RESULTADO_RONDA) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            tiempoFase -= TICK;
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (tiempoFase <= 0) {
                // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                if (combate.combateTerminado()) {
                    // actualiza fase con el valor que necesita el estado actual
                    fase = FINALIZADA;
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                } else {
                    // realiza esta operacion auxiliar para completar el paso actual
                    combate.iniciarRonda();
                    // realiza esta operacion auxiliar para completar el paso actual
                    ubicarJugadores();
                    // actualiza fase con el valor que necesita el estado actual
                    fase = CUENTA_REGRESIVA;
                    // actualiza tiempo fase con el valor que necesita el estado actual
                    tiempoFase = 2f;
                }
            }
            // devuelve este resultado a quien llamo al metodo
            return;
        }

        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float x1 = jugador1.getX();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float x2 = jugador2.getX();
        // realiza esta operacion auxiliar para completar el paso actual
        controlador.actualizar(TICK, mascara1, mascara2, pulsaciones1, pulsaciones2);
        // realiza esta operacion auxiliar para completar el paso actual
        controlador.resolverColision();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        caminando[0] = Math.abs(jugador1.getX() - x1) > 0.01f;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        caminando[1] = Math.abs(jugador2.getX() - x2) > 0.01f;
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int i = 0; i < 2; i++) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int ataque = i == 0 ? controlador.getAtaque1() : controlador.getAtaque2();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            tiemposPose[i] = Math.max(0, tiemposPose[i] - TICK);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (ataque != 0) {
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                poses[i] = ataque;
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                tiemposPose[i] = ataque == 3 ? 0.45f : ataque == 2 ? 0.3f : 0.18f;
            }
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (combate.terminoRonda()) {
            // actualiza ganador ronda con el valor que necesita el estado actual
            ganadorRonda = combate.obtenerGanadorRonda();
            // realiza esta operacion auxiliar para completar el paso actual
            combate.registrarGanadorRonda();
            // actualiza fase con el valor que necesita el estado actual
            fase = RESULTADO_RONDA;
            // actualiza tiempo fase con el valor que necesita el estado actual
            tiempoFase = 2f;
        }
    }

    // este paso prepara o actualiza el estado que necesita el siguiente paso
    byte[] crearEstado() throws IOException {
        // devuelve este resultado a quien llamo al metodo
        return ProtocoloRed.contenido(datos -> {
            datos.writeByte(tipo1.ordinal());
            datos.writeByte(tipo2.ordinal());
            datos.writeByte(fase);
            datos.writeFloat(tiempoFase);
            datos.writeInt(ganadorRonda);
            datos.writeByte(poses[0]);
            datos.writeByte(poses[1]);
            datos.writeFloat(tiemposPose[0]);
            datos.writeFloat(tiemposPose[1]);
            datos.writeBoolean(caminando[0]);
            datos.writeBoolean(caminando[1]);
            combate.escribir(datos);
        });
    }

    // este metodo coloca a los personajes y reinicia los datos visuales
    private void ubicarJugadores() {
        // realiza esta operacion auxiliar para completar el paso actual
        jugador1.establecerPosicion(70, 80);
        // realiza esta operacion auxiliar para completar el paso actual
        jugador2.establecerPosicion(910, 80);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        poses[0] = poses[1] = 0;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        tiemposPose[0] = tiemposPose[1] = 0;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        caminando[0] = caminando[1] = false;
    }

}
