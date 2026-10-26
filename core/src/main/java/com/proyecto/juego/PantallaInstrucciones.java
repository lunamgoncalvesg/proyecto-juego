package com.proyecto.juego;

import com.badlogic.gdx.Input;
import com.proyecto.juego.personajes.TipoPersonaje;
import com.proyecto.juego.red.SesionRed;
import com.proyecto.juego.visual.Tema;

public class PantallaInstrucciones extends PantallaBase {
    private final TipoPersonaje tipo1, tipo2;
    private final boolean enRed, anfitrion;
    private final String ip;
    private final String[] acciones = {"MOVERSE", "SALTAR", "DEFENDER", "GOLPE SUAVE", "ATAQUE BOMBA", "ESPECIAL"};
    private final String[] teclas1 = {"A / D", "W", "S", "E", "ESPACIO", "R"};
    private final String[] teclas2 = {"IZQ / DER", "ARRIBA", "ABAJO", "L", "M", "P"};

    public PantallaInstrucciones(Main juego, TipoPersonaje tipo1, TipoPersonaje tipo2, boolean enRed, boolean anfitrion, String ip) {
        super(juego);
        this.tipo1 = tipo1;
        this.tipo2 = tipo2;
        this.enRed = enRed;
        this.anfitrion = anfitrion;
        this.ip = ip;
    }

    @Override
    public void render(float delta) {
        if (tecla(Input.Keys.ESCAPE)) {
            juego.setScreen(new PantallaPreparacion(juego, enRed));
            return;
        }
        if (tecla(Input.Keys.ENTER) || clic(440, 20, 400, 64)) {
            SesionRed red = enRed ? new SesionRed(anfitrion, ip) : null;
            juego.setScreen(new PantallaCombate(juego, tipo1, enRed ? tipo1 : tipo2, red));
            return;
        }
        fondo(delta, "03 / ANTES DEL PRIMER GOLPE", "CONOCE TUS CONTROLES");
        panel(64, 166, 1152, 390, Tema.AMARILLO);
        texto("ACCION", 95, 522, 1.3f, Tema.GRIS);
        texto(enRed ? "ESQUEMA A" : "JUGADOR 1 / IZQUIERDA", 424, 522, 1.3f, Tema.AMARILLO);
        texto(enRed ? "ESQUEMA B" : "JUGADOR 2 / DERECHA", 821, 522, 1.3f, Tema.ROJO);
        for (int i = 0; i < acciones.length; i++) {
            float y = 469 - i * 45;
            texto(acciones[i], 95, y, 1.5f, Tema.CREMA);
            texto(teclas1[i], 424, y, 1.5f, Tema.AMARILLO);
            texto(teclas2[i], 821, y, 1.5f, Tema.ROJO);
        }
        centrado(enRed ? "AMBOS ESQUEMAS MUEVEN SOLO A TU PERSONAJE. PODES COMBINARLOS." :
            "EN ESTA COMPUTADORA, CADA JUGADOR USA SU PROPIO ESQUEMA.", 640, 132, 1.2f, Tema.CREMA);
        centrado("Recargas: rapido 2 / 10 s, equilibrado 3 / 15 s, fuerte 4 / 20 s. Gana quien llega a 2 rondas.", 640, 102, 1.1f, Tema.GRIS);
        boton("JUGAR", 440, 20, 400, 64, true);
    }
}
