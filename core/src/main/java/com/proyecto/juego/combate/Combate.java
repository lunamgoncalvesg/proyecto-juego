package com.proyecto.juego.combate;

import com.proyecto.juego.personajes.EstadoPersonaje;
import com.proyecto.juego.personajes.Personaje;

public class Combate {

    private Personaje jugador1;
    private Personaje jugador2;

    private float tiempoRonda;
    private final float tiempoMaximo = 60;

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

        tiempoSinAccionJugador1 += delta;
        tiempoSinAccionJugador2 += delta;

        actualizarRegeneracion();

        actualizarRecargas(delta);
    }

    private void actualizarRegeneracion() {

        while (tiempoSinAccionJugador1 >= 1) {

            jugador1.recuperarVida(1);
            tiempoSinAccionJugador1 -= 1;
        }

        while (tiempoSinAccionJugador2 >= 1) {

            jugador2.recuperarVida(1);
            tiempoSinAccionJugador2 -= 1;
        }
    }

    private void actualizarRecargas(float delta) {

        if (recargaBombaJugador1 > 0) {

            recargaBombaJugador1 -= delta;

            if (recargaBombaJugador1 < 0) {
                recargaBombaJugador1 = 0;
            }
        }

        if (recargaBombaJugador2 > 0) {

            recargaBombaJugador2 -= delta;

            if (recargaBombaJugador2 < 0) {
                recargaBombaJugador2 = 0;
            }
        }

        if (recargaEspecialJugador1 > 0) {

            recargaEspecialJugador1 -= delta;

            if (recargaEspecialJugador1 < 0) {
                recargaEspecialJugador1 = 0;
            }
        }

        if (recargaEspecialJugador2 > 0) {

            recargaEspecialJugador2 -= delta;

            if (recargaEspecialJugador2 < 0) {
                recargaEspecialJugador2 = 0;
            }
        }
    }

    public boolean atacarJugador1(TipoAtaque tipo) {

        return realizarAtaque(
                jugador1,
                jugador2,
                tipo,
                true);
    }

    public boolean atacarJugador2(TipoAtaque tipo) {

        return realizarAtaque(
                jugador2,
                jugador1,
                tipo,
                false);
    }

    private boolean realizarAtaque(
            Personaje atacante,
            Personaje defensor,
            TipoAtaque tipo,
            boolean esJugador1) {

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

        if (atacante.getVida() < ataque.getCosto()) {
            return false;
        }

        atacante.recibirDanio(ataque.getCosto());

        if (defensor.getEstado() == EstadoPersonaje.BLOQUEANDO) {

        } else {

            defensor.recibirDanio(ataque.getDanio());
        }

        if (esJugador1) {

            tiempoSinAccionJugador1 = 0;

        } else {

            tiempoSinAccionJugador2 = 0;
        }

        if (tipo == TipoAtaque.BOMBA) {

            if (esJugador1) {

                recargaBombaJugador1 = 3;

            } else {

                recargaBombaJugador2 = 3;
            }

        } else if (tipo == TipoAtaque.ESPECIAL) {

            if (esJugador1) {

                recargaEspecialJugador1 = 15;

            } else {

                recargaEspecialJugador2 = 15;
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

        return tiempoRonda <= 0
                || !jugador1.estaVivo()
                || !jugador2.estaVivo();
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

    public boolean combateTerminado() {

        return rondasJugador1 >= 2
                || rondasJugador2 >= 2;
    }

}
