package com.proyecto.juego;

import com.badlogic.gdx.Input;
import com.proyecto.juego.visual.Tema;

public class PantallaSeleccionModo extends PantallaBase {
    private int opcion;
    private final String[] titulos = {"EN ESTA COMPUTADORA", "EN DOS COMPUTADORAS", "VOLVER"};

    public PantallaSeleccionModo(Main juego) {
        super(juego);
    }

    @Override
    public void render(float delta) {
        if (tecla(Input.Keys.DOWN, Input.Keys.S)) {
            opcion = (opcion + 1) % 3;
        }
        if (tecla(Input.Keys.UP, Input.Keys.W)) {
            opcion = (opcion + 2) % 3;
        }
        if (tecla(Input.Keys.ESCAPE)) {
            juego.setScreen(new PantallaInicio(juego));
            return;
        }
        if (tecla(Input.Keys.ENTER)) {
            elegir();
            return;
        }
        fondo(delta, "", "DONDE VAN A JUGAR?");
        for (int i = 0; i < 3; i++) {
            if (boton(titulos[i], 300, 420 - i * 120, 680, 85, opcion == i)) {
                opcion = i;
                elegir();
                return;
            }
        }
        centrado("Hace clic o usa las flechas. ENTER para continuar. ESC para volver.", 640, 40, 1.2f, Tema.CREMA);
    }

    private void elegir() {
        juego.setScreen(opcion == 2 ? new PantallaInicio(juego) : new PantallaPreparacion(juego, opcion == 1));
    }
}
