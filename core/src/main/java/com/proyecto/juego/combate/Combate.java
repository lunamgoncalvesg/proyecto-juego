package com.proyecto.juego.combate;

import com.proyecto.juego.personajes.EstadoPersonaje;
import com.proyecto.juego.personajes.Personaje;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Combate {

    private static final float DEMORA_REGENERACION = 1.25f;
    private static final float ESTAMINA_POR_SEGUNDO = 7f;

    private Personaje jugador1;
    private Personaje jugador2;
    private float tiempoRonda;
    private final float tiempoMaximo = 60;
    private final int cantRondas = 2;
    private int rondasJugador1;
    private int rondasJugador2;
    private float tiempoSinAccionJugador1;
    private float tiempoSinAccionJugador2;
    private float recargaBombaJugador1;
    private float recargaBombaJugador2;
    private float recargaEspecialJugador1;
    private float recargaEspecialJugador2;

    public Combate(Personaje jugador1, Personaje jugador2) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        rondasJugador1 = 0;
        rondasJugador2 = 0;
        iniciarRonda();
    }

    public void iniciarRonda() {
        tiempoRonda = tiempoMaximo;
        tiempoSinAccionJugador1 = 0;
        tiempoSinAccionJugador2 = 0;
        recargaBombaJugador1 = 0;
        recargaBombaJugador2 = 0;
        recargaEspecialJugador1 = 0;
        recargaEspecialJugador2 = 0;
        jugador1.reiniciarVida();
        jugador2.reiniciarVida();
    }

    public void actualizar(float delta) {
        if (terminoRonda()) {
            return;
        }
        tiempoRonda -= delta;
        if (tiempoRonda < 0) {
            tiempoRonda = 0;
        }
        tiempoSinAccionJugador1 = actualizarRegeneracion(jugador1, tiempoSinAccionJugador1, delta);
        tiempoSinAccionJugador2 = actualizarRegeneracion(jugador2, tiempoSinAccionJugador2, delta);
        actualizarRecargas(delta);
    }

    private float actualizarRegeneracion(Personaje jugador, float tiempoSinAccion, float delta) {
        if (jugador.getEstado() == EstadoPersonaje.BLOQUEANDO) {
            return 0;
        }
        float tiempoRestante = DEMORA_REGENERACION - tiempoSinAccion;
        if (delta > tiempoRestante) {
            jugador.recuperarEstamina((delta - tiempoRestante) * ESTAMINA_POR_SEGUNDO);
        }
        return Math.min(DEMORA_REGENERACION, tiempoSinAccion + delta);
    }

    private void actualizarRecargas(float delta) {
        recargaBombaJugador1 = reducirRecarga(recargaBombaJugador1, delta);
        recargaBombaJugador2 = reducirRecarga(recargaBombaJugador2, delta);
        recargaEspecialJugador1 = reducirRecarga(recargaEspecialJugador1, delta);
        recargaEspecialJugador2 = reducirRecarga(recargaEspecialJugador2, delta);
    }

    private float reducirRecarga(float segundosRestantes, float delta) {
        if (segundosRestantes > 0) {
            segundosRestantes -= delta;
            if (segundosRestantes < 0) {
                segundosRestantes = 0;
            }
        }
        return segundosRestantes;
    }

    public boolean atacarJugador1(TipoAtaque tipo) {
        return realizarAtaque(jugador1, jugador2, tipo, true);
    }

    public boolean atacarJugador2(TipoAtaque tipo) {
        return realizarAtaque(jugador2, jugador1, tipo, false);
    }

    private boolean realizarAtaque(Personaje atacante, Personaje defensor, TipoAtaque tipo, boolean esJugador1) {
        if (terminoRonda()) {
            return false;
        }
        Ataque ataque;
        if (tipo == TipoAtaque.SUAVE) {
            ataque = atacante.getAtaqueSuave();
        } else if (tipo == TipoAtaque.BOMBA) {
            ataque = atacante.getAtaqueBomba();
            if (esJugador1 && recargaBombaJugador1 > 0) {
                return false;
            }
            if (!esJugador1 && recargaBombaJugador2 > 0) {
                return false;
            }
        } else {
            ataque = atacante.getAtaqueEspecial();
            if (esJugador1 && recargaEspecialJugador1 > 0) {
                return false;
            }
            if (!esJugador1 && recargaEspecialJugador2 > 0) {
                return false;
            }
        }
        if (!atacante.tieneEstamina(ataque.getCosto())) {
            return false;
        }
        atacante.gastarEstamina(ataque.getCosto());
        if (defensor.getEstado() != EstadoPersonaje.BLOQUEANDO) {
            float alcance;
            switch (ataque.getTipo()) {
            case SUAVE:
                alcance = 195.0F;
                break;

            case BOMBA:
                alcance = 215.0F;
                break;

            case ESPECIAL:
                alcance = 245.0F;
                break;

            default:
                return false;

            }
            if (estaEnAlcance(atacante, defensor, alcance)) {
                defensor.recibirDanio(ataque.getDanio());
            }
        }
        if (esJugador1) {
            tiempoSinAccionJugador1 = 0;
        } else {
            tiempoSinAccionJugador2 = 0;
        }
        if (tipo == TipoAtaque.BOMBA) {
            if (esJugador1) {
                recargaBombaJugador1 = ataque.getTiempoRecarga();
            } else {
                recargaBombaJugador2 = ataque.getTiempoRecarga();
            }
        } else if (tipo == TipoAtaque.ESPECIAL) {
            if (esJugador1) {
                recargaEspecialJugador1 = ataque.getTiempoRecarga();
            } else {
                recargaEspecialJugador2 = ataque.getTiempoRecarga();
            }
        }
        return true;
    }

    public void bloquearJugador1() {
        tiempoSinAccionJugador1 = 0;
    }

    public void bloquearJugador2() {
        tiempoSinAccionJugador2 = 0;
    }

    public float getTiempoRonda() {
        return tiempoRonda;
    }

    public int getRondasJugador1() {
        return rondasJugador1;
    }

    public int getRondasJugador2() {
        return rondasJugador2;
    }

    public Personaje getJugador1() {
        return jugador1;
    }

    public Personaje getJugador2() {
        return jugador2;
    }

    public boolean terminoRonda() {
        return tiempoRonda <= 0 || !jugador1.estaVivo() || !jugador2.estaVivo();
    }

    public boolean rondaEstaTerminada() {
        return terminoRonda();
    }

    public int obtenerGanadorRonda() {
        if (jugador1.estaVivo() && jugador2.estaVivo()) {
            if (jugador1.getVida() > jugador2.getVida()) {
                return 1;
            } else if (jugador2.getVida() > jugador1.getVida()) {
                return 2;
            } else {
                return 0;
            }
        }
        if (jugador1.estaVivo()) {
            return 1;
        }
        if (jugador2.estaVivo()) {
            return 2;
        }
        return 0;
    }

    public void registrarGanadorRonda() {
        int ganador = obtenerGanadorRonda();
        if (ganador == 1) {
            rondasJugador1++;
        } else if (ganador == 2) {
            rondasJugador2++;
        }
    }

    public int getCantRondas() {
        return cantRondas;
    }

    public boolean combateTerminado() {
        // Al mejor de tres, gana quien llega a dos rondas.
        return rondasJugador1 >= cantRondas || rondasJugador2 >= cantRondas;
    }

    public float getRecargaBombaJugador1() {
        return recargaBombaJugador1;
    }

    public float getRecargaBombaJugador2() {
        return recargaBombaJugador2;
    }

    public float getRecargaEspecialJugador1() {
        return recargaEspecialJugador1;
    }

    public float getRecargaEspecialJugador2() {
        return recargaEspecialJugador2;
    }

    private boolean estaEnAlcance(Personaje atacante, Personaje defensor, float alcance) {
        float centroAtacante = atacante.getX() + 150;
        float centroDefensor = defensor.getX() + 150;
        float distancia = Math.abs(centroAtacante - centroDefensor);
        return distancia <= alcance + 0.01f && Math.abs(atacante.getY() - defensor.getY()) < 90;
    }

    public void escribir(DataOutputStream datos) throws IOException {
        jugador1.escribir(datos);
        jugador2.escribir(datos);
        datos.writeFloat(tiempoRonda);
        datos.writeInt(rondasJugador1);
        datos.writeInt(rondasJugador2);
        datos.writeFloat(recargaBombaJugador1);
        datos.writeFloat(recargaBombaJugador2);
        datos.writeFloat(recargaEspecialJugador1);
        datos.writeFloat(recargaEspecialJugador2);
    }

    public void leer(DataInputStream datos) throws IOException {
        jugador1.leer(datos);
        jugador2.leer(datos);
        tiempoRonda = datos.readFloat();
        rondasJugador1 = datos.readInt();
        rondasJugador2 = datos.readInt();
        recargaBombaJugador1 = datos.readFloat();
        recargaBombaJugador2 = datos.readFloat();
        recargaEspecialJugador1 = datos.readFloat();
        recargaEspecialJugador2 = datos.readFloat();
    }
}
