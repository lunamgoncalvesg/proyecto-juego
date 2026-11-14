package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.proyecto.juego.visual.Tema;

// esta clase agrupa la logica y los datos principales de pantallainicio
public class PantallaInicio extends PantallaBase {
    // este campo representa opcion y conserva ese dato entre llamadas
    private int opcion;
    // este campo representa aviso y conserva ese dato entre llamadas
    private final String aviso;
    // este campo representa opciones y conserva ese dato entre llamadas
    private final String[] opciones = {"JUGAR", "GITHUB", "SALIR"};

    // este metodo pantalla inicio concentra una operacion de esta clase y recibe el control desde quien lo llama
    public PantallaInicio(Main juego) {
        // realiza esta operacion auxiliar para completar el paso actual
        this(juego, "");
    }

    // este metodo pantalla inicio concentra una operacion de esta clase y recibe el control desde quien lo llama
    public PantallaInicio(Main juego, String aviso) {
        // realiza esta operacion auxiliar para completar el paso actual
        super(juego);
        // actualiza this.aviso con el valor que necesita el estado actual
        this.aviso = aviso;
    }

    @Override
    // este metodo render concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void render(float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla(Input.Keys.DOWN, Input.Keys.S)) {
            // actualiza opcion con el valor que necesita el estado actual
            opcion = (opcion + 1) % opciones.length;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla(Input.Keys.UP, Input.Keys.W)) {
            // actualiza opcion con el valor que necesita el estado actual
            opcion = (opcion + opciones.length - 1) % opciones.length;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla(Input.Keys.ENTER)) {
            // realiza esta operacion auxiliar para completar el paso actual
            elegir();
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        fondo(delta);
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int i = 0; i < opciones.length; i++) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (boton(opciones[i], 430, 380 - i * 92, 420, 60, opcion == i)) {
                // actualiza opcion con el valor que necesita el estado actual
                opcion = i;
                // realiza esta operacion auxiliar para completar el paso actual
                elegir();
                // devuelve este resultado a quien llamo al metodo
                return;
            }
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (!aviso.isEmpty()) {
            // realiza esta operacion auxiliar para completar el paso actual
            centrado(aviso, 640, 99, 0.9f, Tema.AMARILLO);
        }
    }

    // este metodo elegir concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void elegir() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (opcion == 0) {
            // realiza esta operacion auxiliar para completar el paso actual
            juego.iniciarPartida();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else if (opcion == 1) {
            // realiza esta operacion auxiliar para completar el paso actual
            Gdx.net.openURI("https://github.com/lunamgoncalvesg/proyecto-juego");
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else {
            // realiza esta operacion auxiliar para completar el paso actual
            juego.salir();
        }
    }
}
