package com.proyecto.juego;

import com.badlogic.gdx.Input;
import com.proyecto.juego.personajes.TipoPersonaje;
import com.proyecto.juego.red.SesionRed;
import com.proyecto.juego.visual.Tema;

public class PantallaInstrucciones extends PantallaBase {
    private final TipoPersonaje tipo1, tipo2;
    private final SesionRed red;
    private final String[] acciones = {"MOVERSE", "SALTAR", "DEFENDER", "GOLPE SUAVE", "ATAQUE BOMBA", "ESPECIAL"};
    private final String[] teclas1 = {"A / D", "W", "S", "E", "ESPACIO", "R"};
    private final String[] teclas2 = {"IZQ / DER", "ARRIBA", "ABAJO", "L", "M", "P"};

    public PantallaInstrucciones(Main juego, TipoPersonaje tipo1, TipoPersonaje tipo2) {
        this(juego, tipo1, tipo2, null);
    }

    public PantallaInstrucciones(Main juego, TipoPersonaje seleccionado, SesionRed red) {
        this(juego, seleccionado, seleccionado, red);
    }

    private PantallaInstrucciones(Main juego, TipoPersonaje tipo1, TipoPersonaje tipo2, SesionRed red) {
        super(juego);
        this.tipo1 = tipo1;
        this.tipo2 = tipo2;
        this.red = red;
    }

    @Override
    public void render(float delta) {
        if (red != null && red.haTerminado()) {
            juego.setScreen(new PantallaInicio(juego, red.getEstado()));
            return;
        }
        if (tecla(Input.Keys.ESCAPE)) {
            if (red != null) red.close();
            juego.setScreen(new PantallaSeleccionModo(juego));
            return;
        }
        if (tecla(Input.Keys.ENTER) || clic(440, 20, 400, 64)) {
            juego.setScreen(new PantallaCombate(juego, tipo1, tipo2, red));
            return;
        }
        fondo(delta);
        panel(64, 166, 1152, 390, Tema.AMARILLO);
        texto("ACCION", 95, 522, 1.3f, Tema.GRIS);
        texto(red != null ? "TU ESQUEMA A" : "JUGADOR 1 / IZQUIERDA", 424, 522, 1.3f, Tema.AMARILLO);
        texto(red != null ? "TU ESQUEMA B" : "JUGADOR 2 / DERECHA", 821, 522, 1.3f, Tema.AMARILLO);
        for (int i = 0; i < acciones.length; i++) {
            float y = 469 - i * 45;
            texto(acciones[i], 95, y, 1.5f, Tema.CREMA);
            texto(teclas1[i], 424, y, 1.5f, Tema.AMARILLO);
            texto(teclas2[i], 821, y, 1.5f, Tema.AMARILLO);
        }
        boton("JUGAR", 440, 20, 400, 64, true);
    }
}
