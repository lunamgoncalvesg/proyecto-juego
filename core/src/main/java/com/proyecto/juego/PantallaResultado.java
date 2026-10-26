package com.proyecto.juego;

import com.badlogic.gdx.Input;
import com.proyecto.juego.personajes.TipoPersonaje;
import com.proyecto.juego.visual.Apariencia;
import com.proyecto.juego.visual.Tema;

public class PantallaResultado extends PantallaBase {
    private final int ganador, jugadorLocal, rondas1, rondas2;
    private final Apariencia personaje;

    public PantallaResultado(Main juego, int ganador, int jugadorLocal, TipoPersonaje tipo, int rondas1, int rondas2) {
        super(juego);
        this.ganador = ganador;
        this.jugadorLocal = jugadorLocal;
        this.rondas1 = rondas1;
        this.rondas2 = rondas2;
        personaje = new Apariencia(tipo);
    }

    @Override
    public void render(float delta) {
        if (reloj >= 5 || tecla(Input.Keys.ENTER, Input.Keys.ESCAPE) || clic(140, 167, 480, 70)) {
            juego.setScreen(new PantallaInicio(juego));
            return;
        }
        boolean victoria = jugadorLocal == 0 || jugadorLocal == ganador;
        String titulo = "PERDISTE";
        String mensaje = "PERDISTE";
        if (victoria) {
            titulo = "GANASTE!";
            mensaje = "GANASTE!";
        }
        if (jugadorLocal == 0) {
            titulo = "TENEMOS UN GANADOR";
            mensaje = "JUGADOR " + ganador + ": GANASTE!";
        }
        fondo(delta, "05 / EL RESULTADO", titulo);
        panel(64, 140, 640, 390, victoria ? Tema.AMARILLO : Tema.ROJO);
        texto(mensaje, 96, 481, 2.2f, Tema.CREMA);
        if (jugadorLocal == 0) {
            texto("JUGADOR " + (3 - ganador) + ": PERDISTE", 96, 424, 1.8f, Tema.ROJO);
        }
        texto("MARCADOR FINAL", 96, 365, 1.2f, Tema.GRIS);
        texto(rondas1 + "  -  " + rondas2, 96, 321, 4, Tema.AMARILLO);
        boton("VOLVER AL MENU", 140, 167, 480, 70, true);
        batch.begin();
        personaje.dibujar(batch, victoria ? "win" : "lose", 971, 149, 390, false);
        batch.end();
        texto("Volviendo al menu en " + Math.max(1, (int) Math.ceil(5 - reloj)) + "...    ENTER: continuar", 676, 35, 1.1f, Tema.CREMA);
    }

    @Override
    public void dispose() {
        personaje.dispose();
        super.dispose();
    }
}
