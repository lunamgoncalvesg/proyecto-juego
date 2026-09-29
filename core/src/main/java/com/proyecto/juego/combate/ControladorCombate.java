package com.proyecto.juego.combate;

import com.proyecto.juego.controles.Controles;
import com.proyecto.juego.personajes.EstadoPersonaje;
import com.proyecto.juego.personajes.Personaje;

public class ControladorCombate {

private Combate combate;
private Controles controles;

private boolean jugador1SaltarAnterior;
private boolean jugador1AtaqueFuerteAnterior;
private boolean jugador1AtaqueSuaveAnterior;
private boolean jugador1EspecialAnterior;

private boolean jugador2SaltarAnterior;
private boolean jugador2AtaqueFuerteAnterior;
private boolean jugador2AtaqueSuaveAnterior;
private boolean jugador2EspecialAnterior;

public ControladorCombate(
        Combate combate,
        Controles controles
) {

    this.combate = combate;
    this.controles = controles;
}

public void actualizar(float delta) {

    combate.actualizar(delta);

    if (combate.terminoRonda()) {
        return;
    }

    Personaje jugador1 = combate.getJugador1();
    Personaje jugador2 = combate.getJugador2();

    if (controles.jugador1Izquierda()) {

        jugador1.mover(
                -jugador1.getVelocidad() * delta
        );
    }

    if (controles.jugador1Derecha()) {

        jugador1.mover(
                jugador1.getVelocidad() * delta
        );
    }

    boolean salto1 =
            controles.jugador1Saltar();

    if (salto1 && !jugador1SaltarAnterior) {

        jugador1.saltar();
    }

    jugador1SaltarAnterior = salto1;

    if (controles.jugador1Defender()) {

        jugador1.establecerEstado(
                EstadoPersonaje.BLOQUEANDO
        );

    } else if (
            jugador1.getEstado()
                    == EstadoPersonaje.BLOQUEANDO
    ) {

        jugador1.establecerEstado(
                EstadoPersonaje.NORMAL
        );
    }

    if (controles.jugador2Izquierda()) {

        jugador2.mover(
                -jugador2.getVelocidad() * delta
        );
    }

    if (controles.jugador2Derecha()) {

        jugador2.mover(
                jugador2.getVelocidad() * delta
        );
    }

    boolean salto2 =
            controles.jugador2Saltar();

    if (salto2 && !jugador2SaltarAnterior) {

        jugador2.saltar();
    }

    jugador2SaltarAnterior = salto2;

    if (controles.jugador2Defender()) {

        jugador2.establecerEstado(
                EstadoPersonaje.BLOQUEANDO
        );

    } else if (
            jugador2.getEstado()
                    == EstadoPersonaje.BLOQUEANDO
    ) {

        jugador2.establecerEstado(
                EstadoPersonaje.NORMAL
        );
    }

    jugador1.actualizarSalto(delta);
    jugador2.actualizarSalto(delta);

    boolean ataqueSuave1 =
            controles.jugador1AtaqueSuave();

    if (
            ataqueSuave1
            && !jugador1AtaqueSuaveAnterior
    ) {

        if (
                combate.atacarJugador1(
                        TipoAtaque.SUAVE
                )
        ) {

            jugador1.establecerEstado(
                    EstadoPersonaje.ATACANDO
            );
        }
    }

    jugador1AtaqueSuaveAnterior =
            ataqueSuave1;

    boolean ataqueFuerte1 =
            controles.jugador1AtaqueFuerte();

    if (
            ataqueFuerte1
            && !jugador1AtaqueFuerteAnterior
    ) {

        if (
                combate.atacarJugador1(
                        TipoAtaque.BOMBA
                )
        ) {

            jugador1.establecerEstado(
                    EstadoPersonaje.ATACANDO
            );
        }
    }

    jugador1AtaqueFuerteAnterior =
            ataqueFuerte1;

    boolean especial1 =
            controles.jugador1Especial();

    if (
            especial1
            && !jugador1EspecialAnterior
    ) {

        if (
                combate.atacarJugador1(
                        TipoAtaque.ESPECIAL
                )
        ) {

            jugador1.establecerEstado(
                    EstadoPersonaje.ATACANDO
            );
        }
    }

    jugador1EspecialAnterior =
            especial1;

    boolean ataqueSuave2 =
            controles.jugador2AtaqueSuave();

    if (
            ataqueSuave2
            && !jugador2AtaqueSuaveAnterior
    ) {

        if (
                combate.atacarJugador2(
                        TipoAtaque.SUAVE
                )
        ) {

            jugador2.establecerEstado(
                    EstadoPersonaje.ATACANDO
            );
        }
    }

    jugador2AtaqueSuaveAnterior =
            ataqueSuave2;

    boolean ataqueFuerte2 =
            controles.jugador2AtaqueFuerte();

    if (
            ataqueFuerte2
            && !jugador2AtaqueFuerteAnterior
    ) {

        if (
                combate.atacarJugador2(
                        TipoAtaque.BOMBA
                )
        ) {

            jugador2.establecerEstado(
                    EstadoPersonaje.ATACANDO
            );
        }
    }

    jugador2AtaqueFuerteAnterior =
            ataqueFuerte2;

    boolean especial2 =
            controles.jugador2Especial();

    if (
            especial2
            && !jugador2EspecialAnterior
    ) {

        if (
                combate.atacarJugador2(
                        TipoAtaque.ESPECIAL
                )
        ) {

            jugador2.establecerEstado(
                    EstadoPersonaje.ATACANDO
            );
        }
    }

    jugador2EspecialAnterior =
            especial2;
}

}