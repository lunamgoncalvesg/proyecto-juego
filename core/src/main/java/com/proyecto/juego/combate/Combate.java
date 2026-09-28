package com.proyecto.juego.combate;

import com.proyecto.juego.personajes.EstadoPersonaje;
import com.proyecto.juego.personajes.Personaje;

public class Combate {

private Personaje jugador1;
private Personaje jugador2;

// Tiempo de la ronda
private float tiempoRonda;
private final float tiempoMaximo = 60;

// Rondas ganadas
private int rondasJugador1;
private int rondasJugador2;

// Tiempo desde la última acción de cada jugador
private float tiempoSinAccionJugador1;
private float tiempoSinAccionJugador2;

// Tiempo restante para volver a utilizar el ataque bomba
private float recargaBombaJugador1;
private float recargaBombaJugador2;

// Tiempo restante para volver a utilizar el ataque especial
private float recargaEspecialJugador1;
private float recargaEspecialJugador2;

public Combate(Personaje jugador1, Personaje jugador2) {

    this.jugador1 = jugador1;
    this.jugador2 = jugador2;

    rondasJugador1 = 0;
    rondasJugador2 = 0;

    iniciarRonda();
}

// Inicia o reinicia una ronda
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

// Actualiza el combate
public void actualizar(float delta) {

    if (terminoRonda()) {
        return;
    }

    // Reducir el tiempo de la ronda
    tiempoRonda -= delta;

    if (tiempoRonda < 0) {
        tiempoRonda = 0;
    }

    // Actualizar tiempos sin acción
    tiempoSinAccionJugador1 += delta;
    tiempoSinAccionJugador2 += delta;

    // Regeneración de vida
    actualizarRegeneracion();

    // Actualizar recargas
    actualizarRecargas(delta);
}

private void actualizarRegeneracion() {

    // Por cada segundo sin hacer nada, recupera 1 HP.

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

// Jugador 1 ataca al jugador 2
public boolean atacarJugador1(TipoAtaque tipo) {

    return realizarAtaque(
            jugador1,
            jugador2,
            tipo,
            true
    );
}

// Jugador 2 ataca al jugador 1
public boolean atacarJugador2(TipoAtaque tipo) {

    return realizarAtaque(
            jugador2,
            jugador1,
            tipo,
            false
    );
}

private boolean realizarAtaque(
        Personaje atacante,
        Personaje defensor,
        TipoAtaque tipo,
        boolean esJugador1) {

    Ataque ataque;

    // Elegir ataque
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

    // El atacante debe tener suficiente vida para pagar el ataque
    if (atacante.getVida() < ataque.getCosto()) {
        return false;
    }

    // Pagar el costo del ataque
    atacante.recibirDanio(ataque.getCosto());

    // =========================
    // BLOQUEO
    // =========================

    if (defensor.getEstado() == EstadoPersonaje.BLOQUEANDO) {

        // El ataque fue bloqueado.
        // No recibe daño.

    } else {

        // Aplicar daño al defensor
        defensor.recibirDanio(ataque.getDanio());
    }

    // Reiniciar tiempo sin acción del atacante
    if (esJugador1) {

        tiempoSinAccionJugador1 = 0;

    } else {

        tiempoSinAccionJugador2 = 0;
    }

    // Iniciar recarga del ataque bomba
    if (tipo == TipoAtaque.BOMBA) {

        if (esJugador1) {

            recargaBombaJugador1 = 3;

        } else {

            recargaBombaJugador2 = 3;
        }

    // Iniciar recarga del especial
    } else if (tipo == TipoAtaque.ESPECIAL) {

        if (esJugador1) {

            recargaEspecialJugador1 = 15;

        } else {

            recargaEspecialJugador2 = 15;
        }
    }

    return true;
}

// Bloqueo
public void bloquearJugador1() {

    tiempoSinAccionJugador1 = 0;
}

public void bloquearJugador2() {

    tiempoSinAccionJugador2 = 0;
}

// Obtener tiempo restante
public float getTiempoRonda() {

    return tiempoRonda;
}

// Obtener rondas ganadas
public int getRondasJugador1() {

    return rondasJugador1;
}

public int getRondasJugador2() {

    return rondasJugador2;
}

// Obtener personajes
public Personaje getJugador1() {

    return jugador1;
}

public Personaje getJugador2() {

    return jugador2;
}

// Comprobar si terminó la ronda
public boolean terminoRonda() {

    return tiempoRonda <= 0
            || !jugador1.estaVivo()
            || !jugador2.estaVivo();
}

// Alias para utilizar desde la pantalla
public boolean rondaEstaTerminada() {

    return terminoRonda();
}

// Determinar ganador de la ronda
public int obtenerGanadorRonda() {

    // Si ambos están vivos cuando termina el tiempo,
    // gana quien tenga más vida.

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

// Registrar el resultado de la ronda
public void registrarGanadorRonda() {

    int ganador = obtenerGanadorRonda();

    if (ganador == 1) {

        rondasJugador1++;

    } else if (ganador == 2) {

        rondasJugador2++;
    }
}

// Comprobar si alguien ganó las 3 rondas
public boolean combateTerminado() {

    return rondasJugador1 >= 2
            || rondasJugador2 >= 2;
}

}
