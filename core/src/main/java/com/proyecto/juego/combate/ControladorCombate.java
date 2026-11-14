package com.proyecto.juego.combate;

import com.proyecto.juego.controles.Acciones;
import com.proyecto.juego.personajes.EstadoPersonaje;
import com.proyecto.juego.personajes.Personaje;

public class ControladorCombate {
    private int ataque1, ataque2;
    private final float[] recuperacion = new float[2];
    private final Combate combate;
    private int mascaraAnterior1, mascaraAnterior2;

    public ControladorCombate(Combate combate) {
        this.combate = combate;
    }

    public int getAtaque1() { return ataque1; }
    public int getAtaque2() { return ataque2; }

    public void actualizar(float delta, int mascara1, int mascara2) {
        actualizar(delta, mascara1, mascara2, 0, 0);
    }

    public void actualizar(float delta, int mascara1, int mascara2, int pulsaciones1, int pulsaciones2) {
        mascara1 &= Acciones.TODAS;
        mascara2 &= Acciones.TODAS;
        pulsaciones1 &= Acciones.TODAS;
        pulsaciones2 &= Acciones.TODAS;
        ataque1 = 0;
        ataque2 = 0;
        combate.actualizar(delta);
        for (int i = 0; i < 2; i++) {
            recuperacion[i] = Math.max(0, recuperacion[i] - delta);
            Personaje jugador = i == 0 ? combate.getJugador1() : combate.getJugador2();
            if (recuperacion[i] == 0 && jugador.getEstado() == EstadoPersonaje.ATACANDO) {
                jugador.establecerEstado(EstadoPersonaje.NORMAL);
            }
        }
        if (combate.terminoRonda()) {
            return;
        }
        actualizarMovimiento(combate.getJugador1(), mascara1, pulsaciones1, true, delta);
        actualizarMovimiento(combate.getJugador2(), mascara2, pulsaciones2, false, delta);
        combate.getJugador1().actualizarSalto(delta);
        combate.getJugador2().actualizarSalto(delta);
        actualizarAtaques(combate.getJugador1(), mascara1, pulsaciones1, mascaraAnterior1, true);
        actualizarAtaques(combate.getJugador2(), mascara2, pulsaciones2, mascaraAnterior2, false);
        mascaraAnterior1 = mascara1;
        mascaraAnterior2 = mascara2;
    }

    public void resolverColision() {
        Personaje jugador1 = combate.getJugador1();
        Personaje jugador2 = combate.getJugador2();
        float superposicion = 195 - (jugador2.getX() - jugador1.getX());
        if (superposicion <= 0) return;
        float mover1 = Math.min(superposicion / 2, jugador1.getX() - 20);
        float mover2 = Math.min(superposicion - mover1, 960 - jugador2.getX());
        mover1 += Math.min(superposicion - mover1 - mover2, jugador1.getX() - 20 - mover1);
        jugador1.mover(-mover1);
        jugador2.mover(mover2);
    }

    private void actualizarMovimiento(Personaje jugador, int mascara, int pulsaciones, boolean jugador1, float delta) {
        if ((mascara & Acciones.IZQUIERDA) != 0) jugador.mover(-jugador.getVelocidad() * delta);
        if ((mascara & Acciones.DERECHA) != 0) jugador.mover(jugador.getVelocidad() * delta);
        boolean salto = presionNueva(mascara, pulsaciones, jugador1 ? mascaraAnterior1 : mascaraAnterior2, Acciones.SALTAR);
        if (salto) jugador.saltar();
        if ((mascara & Acciones.DEFENDER) != 0) {
            jugador.establecerEstado(EstadoPersonaje.BLOQUEANDO);
        } else if (jugador.getEstado() == EstadoPersonaje.BLOQUEANDO) {
            jugador.establecerEstado(EstadoPersonaje.NORMAL);
        }
    }

    private void actualizarAtaques(Personaje jugador, int mascara, int pulsaciones, int anterior, boolean jugador1) {
        int indice = jugador1 ? 0 : 1;
        if (recuperacion[indice] > 0) return;
        int ataque = 0;
        if (presionNueva(mascara, pulsaciones, anterior, Acciones.GOLPE)) {
            if (ejecutar(jugador1, TipoAtaque.SUAVE)) ataque = 1;
        }
        if (presionNueva(mascara, pulsaciones, anterior, Acciones.BOMBA)) {
            if (ejecutar(jugador1, TipoAtaque.BOMBA)) ataque = 2;
        }
        if (presionNueva(mascara, pulsaciones, anterior, Acciones.ESPECIAL)) {
            if (ejecutar(jugador1, TipoAtaque.ESPECIAL)) ataque = 3;
        }
        if (ataque != 0) {
            recuperacion[indice] = ataque == 3 ? 0.53f : ataque == 2 ? 0.38f : 0.26f;
            jugador.establecerEstado(EstadoPersonaje.ATACANDO);
            if (jugador1) ataque1 = ataque; else ataque2 = ataque;
        }
    }

    private boolean ejecutar(boolean jugador1, TipoAtaque tipo) {
        if (jugador1) return combate.atacarJugador1(tipo);
        return combate.atacarJugador2(tipo);
    }

    private boolean presionNueva(int actual, int pulsaciones, int anterior, int accion) {
        return ((actual & accion) != 0 && (anterior & accion) == 0) || (pulsaciones & accion) != 0;
    }
}
