package com.proyecto.juego;

import com.badlogic.gdx.Input;
import com.proyecto.juego.personajes.TipoPersonaje;
import com.proyecto.juego.red.SesionRed;
import com.proyecto.juego.visual.Tema;

// esta clase agrupa la logica y los datos principales de pantallainstrucciones
public class PantallaInstrucciones extends PantallaBase {
    // este campo representa tipo1 y conserva ese dato entre llamadas
    private final TipoPersonaje tipo1, tipo2;
    // este campo representa red y conserva ese dato entre llamadas
    private final SesionRed red;
    // este campo representa confirmojugar y conserva ese dato entre llamadas
    private boolean confirmoJugar;
    // este campo representa acciones y conserva ese dato entre llamadas
    private final String[] acciones = {"MOVERSE", "SALTAR", "DEFENDER", "GOLPE SUAVE", "ATAQUE BOMBA", "ESPECIAL"};
    // este campo representa teclas1 y conserva ese dato entre llamadas
    private final String[] teclas1 = {"A / D", "W", "S", "E", "ESPACIO", "R"};
    // este campo representa teclas2 y conserva ese dato entre llamadas
    private final String[] teclas2 = {"IZQ / DER", "ARRIBA", "ABAJO", "L", "M", "P"};

    // este metodo pantalla instrucciones concentra una operacion de esta clase y recibe el control desde quien lo llama
    public PantallaInstrucciones(Main juego, TipoPersonaje tipo1, TipoPersonaje tipo2) {
        // realiza esta operacion auxiliar para completar el paso actual
        this(juego, tipo1, tipo2, null);
    }

    // este metodo pantalla instrucciones concentra una operacion de esta clase y recibe el control desde quien lo llama
    public PantallaInstrucciones(Main juego, TipoPersonaje seleccionado, SesionRed red) {
        // realiza esta operacion auxiliar para completar el paso actual
        this(juego, seleccionado, seleccionado, red);
    }

    // este metodo pantalla instrucciones concentra una operacion de esta clase y recibe el control desde quien lo llama
    private PantallaInstrucciones(Main juego, TipoPersonaje tipo1, TipoPersonaje tipo2, SesionRed red) {
        // realiza esta operacion auxiliar para completar el paso actual
        super(juego);
        // actualiza this.tipo1 con el valor que necesita el estado actual
        this.tipo1 = tipo1;
        // actualiza this.tipo2 con el valor que necesita el estado actual
        this.tipo2 = tipo2;
        // actualiza this.red con el valor que necesita el estado actual
        this.red = red;
    }

    @Override
    // este metodo render concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void render(float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (red != null && red.haTerminado()) {
            // realiza esta operacion auxiliar para completar el paso actual
            juego.setScreen(new PantallaInicio(juego, red.getEstado()));
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (red != null && red.hayEstadoCombate()) {
            // realiza esta operacion auxiliar para completar el paso actual
            juego.setScreen(new PantallaCombate(juego, tipo1, tipo2, red));
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla(Input.Keys.ESCAPE)) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (red != null) red.close();
            // realiza esta operacion auxiliar para completar el paso actual
            juego.setScreen(new PantallaSeleccionModo(juego));
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (!confirmoJugar && (tecla(Input.Keys.ENTER) || clic(440, 20, 400, 64))) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (red == null) {
                // realiza esta operacion auxiliar para completar el paso actual
                juego.setScreen(new PantallaCombate(juego, tipo1, tipo2, null));
                // devuelve este resultado a quien llamo al metodo
                return;
            }
            // realiza esta operacion auxiliar para completar el paso actual
            red.confirmarJugar();
            // actualiza confirmo jugar con el valor que necesita el estado actual
            confirmoJugar = true;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        fondo(delta);
        // realiza esta operacion auxiliar para completar el paso actual
        panel(64, 166, 1152, 390, Tema.AMARILLO);
        // realiza esta operacion auxiliar para completar el paso actual
        texto("ACCION", 95, 522, 1.3f, Tema.GRIS);
        // realiza esta operacion auxiliar para completar el paso actual
        texto(red != null ? "TU ESQUEMA A" : "JUGADOR 1 / IZQUIERDA", 424, 522, 1.3f, Tema.AMARILLO);
        // realiza esta operacion auxiliar para completar el paso actual
        texto(red != null ? "TU ESQUEMA B" : "JUGADOR 2 / DERECHA", 821, 522, 1.3f, Tema.AMARILLO);
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int i = 0; i < acciones.length; i++) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            float y = 469 - i * 45;
            // realiza esta operacion auxiliar para completar el paso actual
            texto(acciones[i], 95, y, 1.5f, Tema.CREMA);
            // realiza esta operacion auxiliar para completar el paso actual
            texto(teclas1[i], 424, y, 1.5f, Tema.AMARILLO);
            // realiza esta operacion auxiliar para completar el paso actual
            texto(teclas2[i], 821, y, 1.5f, Tema.AMARILLO);
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (confirmoJugar) {
            // realiza esta operacion auxiliar para completar el paso actual
            centrado("ESPERANDO AL RIVAL...", 640, 51, 1.15f, Tema.AMARILLO);
            // realiza esta operacion auxiliar para completar el paso actual
            centrado(red.getEstado(), 640, 22, 0.75f, Tema.GRIS);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else {
            // realiza esta operacion auxiliar para completar el paso actual
            boton("JUGAR", 440, 20, 400, 64, true);
        }
    }
}
