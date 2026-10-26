package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.proyecto.juego.visual.Apariencia;
import com.proyecto.juego.visual.Tema;
import com.proyecto.juego.personajes.TipoPersonaje;

public class PantallaInicio extends PantallaBase {
    private int opcion;
    private final Apariencia personaje = new Apariencia(TipoPersonaje.EQUILIBRADO);
    private final String aviso;
    private final String[] opciones = {"JUGAR", "IR AL GITHUB", "SALIR"};

    public PantallaInicio(Main juego) {
        this(juego, "");
    }

    public PantallaInicio(Main juego, String aviso) {
        super(juego);
        this.aviso = aviso;
    }

    @Override
    public void render(float delta) {
        if (tecla(Input.Keys.DOWN, Input.Keys.S)) {
            opcion = (opcion + 1) % opciones.length;
        }
        if (tecla(Input.Keys.UP, Input.Keys.W)) {
            opcion = (opcion + opciones.length - 1) % opciones.length;
        }
        if (tecla(Input.Keys.ENTER)) {
            elegir();
            return;
        }
        fondo(delta, "", "STREET FIGHTER PONELE");
        for (int i = 0; i < opciones.length; i++) {
            if (boton(opciones[i], 160, 410 - i * 105, 480, 80, opcion == i)) {
                opcion = i;
                elegir();
                return;
            }
        }
        batch.begin();
        personaje.dibujar(batch, "idle", 935, 145, 390, false);
        batch.end();
        if (!aviso.isEmpty()) {
            centrado(aviso, 640, 113, 1.3f, Tema.ROJO);
        }
        centrado("Hace clic en una opcion o usa las flechas y ENTER.", 640, 40, 1.35f, Tema.CREMA);
    }

    private void elegir() {
        if (opcion == 0) {
            juego.setScreen(new PantallaSeleccionModo(juego));
        } else if (opcion == 1) {
            Gdx.net.openURI("https://github.com/lunamgoncalvesg/proyecto-juego");
        } else {
            juego.salir();
        }
    }

    @Override
    public void dispose() {
        personaje.dispose();
        super.dispose();
    }
}
