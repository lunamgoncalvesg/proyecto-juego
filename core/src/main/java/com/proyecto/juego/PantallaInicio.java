package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.proyecto.juego.visual.Tema;

public class PantallaInicio extends PantallaBase {
    private int opcion;
    private final String aviso;
    private final String[] opciones = {"JUGAR", "GITHUB", "SALIR"};

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
        fondo(delta);
        for (int i = 0; i < opciones.length; i++) {
            if (boton(opciones[i], 430, 380 - i * 92, 420, 60, opcion == i)) {
                opcion = i;
                elegir();
                return;
            }
        }
        if (!aviso.isEmpty()) {
            centrado(aviso, 640, 99, 0.9f, Tema.AMARILLO);
        }
    }

    private void elegir() {
        if (opcion == 0) {
            juego.iniciarPartida();
        } else if (opcion == 1) {
            Gdx.net.openURI("https://github.com/lunamgoncalvesg/proyecto-juego");
        } else {
            juego.salir();
        }
    }
}
