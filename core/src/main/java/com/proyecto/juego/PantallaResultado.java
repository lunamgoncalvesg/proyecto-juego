package com.proyecto.juego;

import com.badlogic.gdx.Input;
import com.proyecto.juego.personajes.TipoPersonaje;
import com.proyecto.juego.visual.Apariencia;
import com.proyecto.juego.visual.Tema;

// esta clase agrupa la logica y los datos principales de pantallaresultado
public class PantallaResultado extends PantallaBase {
    // este campo representa ganador y conserva ese dato entre llamadas
    private final int ganador, jugadorLocal, rondas1, rondas2;
    // este campo representa personaje y conserva ese dato entre llamadas
    private final Apariencia personaje;

    // este metodo pantalla resultado concentra una operacion de esta clase y recibe el control desde quien lo llama
    public PantallaResultado(Main juego, int ganador, int jugadorLocal, TipoPersonaje tipo, int rondas1, int rondas2) {
        // realiza esta operacion auxiliar para completar el paso actual
        super(juego);
        // actualiza this.ganador con el valor que necesita el estado actual
        this.ganador = ganador;
        // actualiza this.jugador local con el valor que necesita el estado actual
        this.jugadorLocal = jugadorLocal;
        // actualiza this.rondas1 con el valor que necesita el estado actual
        this.rondas1 = rondas1;
        // actualiza this.rondas2 con el valor que necesita el estado actual
        this.rondas2 = rondas2;
        // actualiza personaje con el valor que necesita el estado actual
        personaje = new Apariencia(tipo);
    }

    @Override
    // este metodo render concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void render(float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (reloj >= 5 || tecla(Input.Keys.ENTER, Input.Keys.ESCAPE) || clic(140, 167, 480, 70)) {
            // realiza esta operacion auxiliar para completar el paso actual
            juego.setScreen(new PantallaInicio(juego));
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean victoria = jugadorLocal == 0 || jugadorLocal == ganador;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String titulo = "PERDISTE";
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String mensaje = "PERDISTE";
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (victoria) {
            // actualiza titulo con el valor que necesita el estado actual
            titulo = "GANASTE!";
            // actualiza mensaje con el valor que necesita el estado actual
            mensaje = "GANASTE!";
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (jugadorLocal == 0) {
            // actualiza titulo con el valor que necesita el estado actual
            titulo = "TENEMOS UN GANADOR";
            // actualiza mensaje con el valor que necesita el estado actual
            mensaje = "JUGADOR " + ganador + ": GANASTE!";
        }
        // realiza esta operacion auxiliar para completar el paso actual
        fondo(delta);
        // realiza esta operacion auxiliar para completar el paso actual
        panel(64, 140, 640, 390, victoria ? Tema.AMARILLO : Tema.AMARILLO);
        // realiza esta operacion auxiliar para completar el paso actual
        texto(mensaje, 96, 481, 2.2f, Tema.CREMA);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (jugadorLocal == 0) {
            // realiza esta operacion auxiliar para completar el paso actual
            texto("JUGADOR " + (3 - ganador) + ": PERDISTE", 96, 424, 1.8f, Tema.AMARILLO);
        }
        // realiza esta operacion auxiliar para completar el paso actual
        texto("MARCADOR FINAL", 96, 390, 1.2f, Tema.GRIS);
        // realiza esta operacion auxiliar para completar el paso actual
        texto(rondas1 + "  -  " + rondas2, 96, 321, 4, Tema.AMARILLO);
        // realiza esta operacion auxiliar para completar el paso actual
        boton("VOLVER AL MENU", 140, 167, 480, 70, true);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.begin();
        // realiza esta operacion auxiliar para completar el paso actual
        personaje.dibujar(batch, victoria ? "win" : "lose", 985, 149, 340, false);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.end();
        // realiza esta operacion auxiliar para completar el paso actual
        centrado("VOLVIENDO AL MENU EN " + Math.max(1, (int) Math.ceil(5 - reloj)) + "... ENTER: CONTINUAR", 640, 35, 1.1f, Tema.CREMA);
    }

    @Override
    // este metodo dispose concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dispose() {
        // realiza esta operacion auxiliar para completar el paso actual
        personaje.dispose();
        // realiza esta operacion auxiliar para completar el paso actual
        super.dispose();
    }
}
