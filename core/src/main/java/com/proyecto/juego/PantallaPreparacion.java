package com.proyecto.juego;

import com.badlogic.gdx.Input;
import com.proyecto.juego.personajes.TipoPersonaje;
import com.proyecto.juego.visual.Apariencia;
import com.proyecto.juego.visual.Tema;

public class PantallaPreparacion extends PantallaBase {
    private final Apariencia[] personajes = new Apariencia[3];
    private int indicePersonaje1, indicePersonaje2 = 1, jugadorQueElige = 1;
    private final String[] nombres = {
        TipoPersonaje.EQUILIBRADO.getNombreVisible(), TipoPersonaje.RAPIDO.getNombreVisible(),
        TipoPersonaje.FUERTE.getNombreVisible()
    };
    private final String[] descripciones = {
        "EQUILIBRADO",
        "RÁPIDO, PERO DÉBIL",
        "FUERTE, PERO LENTO"
    };

    public PantallaPreparacion(Main juego) {
        super(juego);
        for (int i = 0; i < 3; i++) {
            personajes[i] = new Apariencia(TipoPersonaje.values()[i]);
        }
    }

    @Override
    public void render(float delta) {
        if (tecla(Input.Keys.ESCAPE)) {
            if (jugadorQueElige == 2) {
                jugadorQueElige = 1;
            } else {
                juego.setScreen(new PantallaSeleccionModo(juego));
                return;
            }
        }
        int elegido = jugadorQueElige == 1 ? indicePersonaje1 : indicePersonaje2;
        if (seleccionHaciaIzquierda()) {
            elegido = (elegido + 2) % 3;
        }
        if (seleccionHaciaDerecha()) {
            elegido = (elegido + 1) % 3;
        }
        if (jugadorQueElige == 1) {
            indicePersonaje1 = elegido;
        } else {
            indicePersonaje2 = elegido;
        }
        if (tecla(Input.Keys.ENTER)) {
            confirmarPersonaje();
            return;
        }
        fondo(delta);
        for (int i = 0; i < 3; i++) {
            float x = 64 + i * 391;
            panel(x, 237, 370, 330, elegido == i ? Tema.AMARILLO : Tema.BORDE);
            batch.begin();
            personajes[i].retrato(batch, x + 55, 332, 260, 210);
            batch.end();
            centrado(nombres[i], x + 185, 298, 1.75f, Tema.CREMA);
            centrado(descripciones[i], x + 185, 255, 0.75f, Tema.GRIS);
            if (clic(x, 237, 370, 330)) {
                if (jugadorQueElige == 1) {
                    indicePersonaje1 = i;
                } else {
                    indicePersonaje2 = i;
                }
            }
        }
        centrado(jugadorQueElige == 1 ? "Jugador 1" : "Jugador 2", 640, 199, 1.4f, Tema.CREMA);
        if (boton("CONTINUAR", 440, 91, 400, 70, true)) {
            confirmarPersonaje();
            return;
        }
    }

    private boolean seleccionHaciaIzquierda() {
        return jugadorQueElige == 2
            ? tecla(Input.Keys.LEFT)
            : tecla(Input.Keys.A);
    }

    private boolean seleccionHaciaDerecha() {
        return jugadorQueElige == 2
            ? tecla(Input.Keys.RIGHT)
            : tecla(Input.Keys.D);
    }

    private void confirmarPersonaje() {
        if (jugadorQueElige == 1) {
            jugadorQueElige = 2;
            return;
        }
        juego.setScreen(new PantallaInstrucciones(juego, TipoPersonaje.values()[indicePersonaje1],
            TipoPersonaje.values()[indicePersonaje2]));
    }

    @Override
    public void dispose() {
        for (Apariencia personaje : personajes) {
            personaje.dispose();
        }
        super.dispose();
    }
}
