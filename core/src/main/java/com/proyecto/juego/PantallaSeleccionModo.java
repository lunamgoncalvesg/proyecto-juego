package com.proyecto.juego;

import com.badlogic.gdx.Input;
import com.proyecto.juego.visual.Tema;

// esta clase agrupa la logica y los datos principales de pantallaseleccionmodo
public class PantallaSeleccionModo extends PantallaBase {
    // este campo representa opcion y conserva ese dato entre llamadas
    private int opcion;
    // este campo representa titulos y conserva ese dato entre llamadas
    private final String[] titulos = {"EN ESTA COMPUTADORA", "EN DOS COMPUTADORAS", "VOLVER"};

    // este metodo pantalla seleccion modo concentra una operacion de esta clase y recibe el control desde quien lo llama
    public PantallaSeleccionModo(Main juego) {
        // realiza esta operacion auxiliar para completar el paso actual
        super(juego);
    }

    @Override
    // este metodo render concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void render(float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla(Input.Keys.DOWN, Input.Keys.S)) {
            // actualiza opcion con el valor que necesita el estado actual
            opcion = (opcion + 1) % 3;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla(Input.Keys.UP, Input.Keys.W)) {
            // actualiza opcion con el valor que necesita el estado actual
            opcion = (opcion + 2) % 3;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla(Input.Keys.ESCAPE)) {
            // realiza esta operacion auxiliar para completar el paso actual
            juego.setScreen(new PantallaInicio(juego));
            // devuelve este resultado a quien llamo al metodo
            return;
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
        for (int i = 0; i < 3; i++) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (boton(titulos[i], 300, 420 - i * 120, 680, 85, opcion == i)) {
                // actualiza opcion con el valor que necesita el estado actual
                opcion = i;
                // realiza esta operacion auxiliar para completar el paso actual
                elegir();
                // devuelve este resultado a quien llamo al metodo
                return;
            }
        }
    }

    // este metodo elegir concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void elegir() {
        // realiza esta operacion auxiliar para completar el paso actual
        juego.setScreen(opcion == 2 ? new PantallaInicio(juego) : new PantallaPreparacion(juego, opcion == 1));
    }
}
