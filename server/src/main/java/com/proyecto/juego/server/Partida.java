package com.proyecto.juego.server;

import com.proyecto.juego.combate.Combate;
import com.proyecto.juego.combate.ControladorCombate;
import com.proyecto.juego.red.ProtocoloRed;
import com.proyecto.juego.personajes.Personaje;
import com.proyecto.juego.personajes.TipoPersonaje;

import java.io.DataOutputStream;
import java.io.IOException;

final class Partida {
    static final int CUENTA_REGRESIVA = 0;
    static final int COMBATE = 1;
    static final int RESULTADO_RONDA = 2;
    static final int FINALIZADA = 3;
    private static final float TICK = 1f / 60f;

    private final TipoPersonaje tipo1;
    private final TipoPersonaje tipo2;
    private final Personaje jugador1;
    private final Personaje jugador2;
    private final Combate combate;
    private final ControladorCombate controlador;
    private final int[] poses = new int[2];
    private final float[] tiemposPose = new float[2];
    private final boolean[] caminando = new boolean[2];
    private int fase = CUENTA_REGRESIVA;
    private int ganadorRonda;
    private float tiempoFase = 3f;

    Partida(TipoPersonaje tipo1, TipoPersonaje tipo2) {
        this.tipo1 = tipo1;
        this.tipo2 = tipo2;
        jugador1 = new Personaje("Jugador 1", tipo1);
        jugador2 = new Personaje("Jugador 2", tipo2);
        jugador1.establecerLimites(20, 960);
        jugador2.establecerLimites(20, 960);
        combate = new Combate(jugador1, jugador2);
        controlador = new ControladorCombate(combate);
        ubicarJugadores();
    }

    void actualizar(int mascara1, int mascara2, int pulsaciones1, int pulsaciones2) {
        if (fase == FINALIZADA) return;
        if (fase == CUENTA_REGRESIVA) {
            tiempoFase = Math.max(0, tiempoFase - TICK);
            if (tiempoFase == 0) fase = COMBATE;
            return;
        }
        if (fase == RESULTADO_RONDA) {
            tiempoFase -= TICK;
            if (tiempoFase <= 0) {
                if (combate.combateTerminado()) {
                    fase = FINALIZADA;
                } else {
                    combate.iniciarRonda();
                    ubicarJugadores();
                    fase = CUENTA_REGRESIVA;
                    tiempoFase = 2f;
                }
            }
            return;
        }

        float x1 = jugador1.getX();
        float x2 = jugador2.getX();
        controlador.actualizar(TICK, mascara1, mascara2, pulsaciones1, pulsaciones2);
        controlador.resolverColision();
        caminando[0] = Math.abs(jugador1.getX() - x1) > 0.01f;
        caminando[1] = Math.abs(jugador2.getX() - x2) > 0.01f;
        for (int i = 0; i < 2; i++) {
            int ataque = i == 0 ? controlador.getAtaque1() : controlador.getAtaque2();
            tiemposPose[i] = Math.max(0, tiemposPose[i] - TICK);
            if (ataque != 0) {
                poses[i] = ataque;
                tiemposPose[i] = ataque == 3 ? 0.45f : ataque == 2 ? 0.3f : 0.18f;
            }
        }
        if (combate.terminoRonda()) {
            ganadorRonda = combate.obtenerGanadorRonda();
            combate.registrarGanadorRonda();
            fase = RESULTADO_RONDA;
            tiempoFase = 2f;
        }
    }

    byte[] crearEstado() throws IOException {
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

    private void ubicarJugadores() {
        jugador1.establecerPosicion(70, 80);
        jugador2.establecerPosicion(910, 80);
        poses[0] = poses[1] = 0;
        tiemposPose[0] = tiemposPose[1] = 0;
        caminando[0] = caminando[1] = false;
    }

}
