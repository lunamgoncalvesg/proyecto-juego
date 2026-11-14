package com.proyecto.juego.combate;

import com.proyecto.juego.controles.Acciones;
import com.proyecto.juego.personajes.EstadoPersonaje;
import com.proyecto.juego.personajes.Personaje;

// esta clase agrupa la logica y los datos principales de controladorcombate
public class ControladorCombate {
    // este campo representa ataque1 y conserva ese dato entre llamadas
    private int ataque1, ataque2;
    private final float[] recuperacion = new float[2];
    // este campo representa combate y conserva ese dato entre llamadas
    private final Combate combate;
    // este campo representa mascaraanterior1 y conserva ese dato entre llamadas
    private int mascaraAnterior1, mascaraAnterior2;

    // este metodo controlador combate concentra una operacion de esta clase y recibe el control desde quien lo llama
    public ControladorCombate(Combate combate) {
        // actualiza this.combate con el valor que necesita el estado actual
        this.combate = combate;
    }

    // este metodo get ataque1 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public int getAtaque1() { return ataque1; }
    // este metodo get ataque2 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public int getAtaque2() { return ataque2; }

    // este metodo avanza la simulacion segun el tiempo transcurrido
    public void actualizar(float delta, int mascara1, int mascara2) {
        // realiza esta operacion auxiliar para completar el paso actual
        actualizar(delta, mascara1, mascara2, 0, 0);
    }

    // este metodo avanza la simulacion segun el tiempo transcurrido
    public void actualizar(float delta, int mascara1, int mascara2, int pulsaciones1, int pulsaciones2) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        mascara1 &= Acciones.TODAS;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        mascara2 &= Acciones.TODAS;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        pulsaciones1 &= Acciones.TODAS;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        pulsaciones2 &= Acciones.TODAS;
        // actualiza ataque1 con el valor que necesita el estado actual
        ataque1 = 0;
        // actualiza ataque2 con el valor que necesita el estado actual
        ataque2 = 0;
        // realiza esta operacion auxiliar para completar el paso actual
        combate.actualizar(delta);
        for (int i = 0; i < 2; i++) {
            recuperacion[i] = Math.max(0, recuperacion[i] - delta);
            Personaje jugador = i == 0 ? combate.getJugador1() : combate.getJugador2();
            if (recuperacion[i] == 0 && jugador.getEstado() == EstadoPersonaje.ATACANDO) {
                jugador.establecerEstado(EstadoPersonaje.NORMAL);
            }
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (combate.terminoRonda()) {
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        actualizarMovimiento(combate.getJugador1(), mascara1, pulsaciones1, true, delta);
        // realiza esta operacion auxiliar para completar el paso actual
        actualizarMovimiento(combate.getJugador2(), mascara2, pulsaciones2, false, delta);
        // realiza esta operacion auxiliar para completar el paso actual
        combate.getJugador1().actualizarSalto(delta);
        // realiza esta operacion auxiliar para completar el paso actual
        combate.getJugador2().actualizarSalto(delta);
        // realiza esta operacion auxiliar para completar el paso actual
        actualizarAtaques(combate.getJugador1(), mascara1, pulsaciones1, mascaraAnterior1, true);
        // realiza esta operacion auxiliar para completar el paso actual
        actualizarAtaques(combate.getJugador2(), mascara2, pulsaciones2, mascaraAnterior2, false);
        // actualiza mascara anterior1 con el valor que necesita el estado actual
        mascaraAnterior1 = mascara1;
        // actualiza mascara anterior2 con el valor que necesita el estado actual
        mascaraAnterior2 = mascara2;
    }

    // este metodo separa los personajes cuando sus posiciones se superponen
    public void resolverColision() {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Personaje jugador1 = combate.getJugador1();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Personaje jugador2 = combate.getJugador2();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float superposicion = 195 - (jugador2.getX() - jugador1.getX());
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (superposicion <= 0) return;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float mover1 = Math.min(superposicion / 2, jugador1.getX() - 20);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float mover2 = Math.min(superposicion - mover1, 960 - jugador2.getX());
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        mover1 += Math.min(superposicion - mover1 - mover2, jugador1.getX() - 20 - mover1);
        // realiza esta operacion auxiliar para completar el paso actual
        jugador1.mover(-mover1);
        // realiza esta operacion auxiliar para completar el paso actual
        jugador2.mover(mover2);
    }

    // este metodo aplica desplazamiento, salto y defensa a partir de los controles
    private void actualizarMovimiento(Personaje jugador, int mascara, int pulsaciones, boolean jugador1, float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if ((mascara & Acciones.IZQUIERDA) != 0) jugador.mover(-jugador.getVelocidad() * delta);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if ((mascara & Acciones.DERECHA) != 0) jugador.mover(jugador.getVelocidad() * delta);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean salto = presionNueva(mascara, pulsaciones, jugador1 ? mascaraAnterior1 : mascaraAnterior2, Acciones.SALTAR);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (salto) jugador.saltar();
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if ((mascara & Acciones.DEFENDER) != 0) {
            // realiza esta operacion auxiliar para completar el paso actual
            jugador.establecerEstado(EstadoPersonaje.BLOQUEANDO);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else if (jugador.getEstado() == EstadoPersonaje.BLOQUEANDO) {
            // realiza esta operacion auxiliar para completar el paso actual
            jugador.establecerEstado(EstadoPersonaje.NORMAL);
        }
    }

    // este metodo detecta las nuevas pulsaciones y solicita el ataque correspondiente
    private void actualizarAtaques(Personaje jugador, int mascara, int pulsaciones, int anterior, boolean jugador1) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int indice = jugador1 ? 0 : 1;
        if (recuperacion[indice] > 0) return;
        int ataque = 0;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (((mascara | pulsaciones) & Acciones.GOLPE) != 0) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (ejecutar(jugador1, TipoAtaque.SUAVE)) ataque = 1;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (presionNueva(mascara, pulsaciones, anterior, Acciones.BOMBA)) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (ejecutar(jugador1, TipoAtaque.BOMBA)) ataque = 2;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (presionNueva(mascara, pulsaciones, anterior, Acciones.ESPECIAL)) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (ejecutar(jugador1, TipoAtaque.ESPECIAL)) ataque = 3;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (ataque != 0) {
            recuperacion[indice] = ataque == 3 ? 0.53f : ataque == 2 ? 0.38f : 0.26f;
            // realiza esta operacion auxiliar para completar el paso actual
            jugador.establecerEstado(EstadoPersonaje.ATACANDO);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (jugador1) ataque1 = ataque; else ataque2 = ataque;
        }
    }

    // este metodo ejecutar concentra una operacion de esta clase y recibe el control desde quien lo llama
    private boolean ejecutar(boolean jugador1, TipoAtaque tipo) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (jugador1) return combate.atacarJugador1(tipo);
        // devuelve este resultado a quien llamo al metodo
        return combate.atacarJugador2(tipo);
    }

    // este metodo presion nueva concentra una operacion de esta clase y recibe el control desde quien lo llama
    private boolean presionNueva(int actual, int pulsaciones, int anterior, int accion) {
        // devuelve este resultado a quien llamo al metodo
        return ((actual & accion) != 0 && (anterior & accion) == 0) || (pulsaciones & accion) != 0;
    }
}
