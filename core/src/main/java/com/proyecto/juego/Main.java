package com.proyecto.juego;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.proyecto.juego.visual.FondoAnimado;

// esta clase agrupa la logica y los datos principales de main
public class Main extends Game {
    // este campo representa fondo y conserva ese dato entre llamadas
    public FondoAnimado fondo;
    // este campo representa cerrando y conserva ese dato entre llamadas
    private boolean cerrando;
    // este campo representa salidafinal y conserva ese dato entre llamadas
    private boolean salidaFinal;

    @Override
    // este metodo set screen concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void setScreen(Screen siguiente) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Screen anterior = getScreen();
        // realiza esta operacion auxiliar para completar el paso actual
        super.setScreen(siguiente);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (anterior != null && anterior != siguiente) {
            // realiza esta operacion auxiliar para completar el paso actual
            anterior.dispose();
        }
    }

    @Override
    // este metodo create concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void create() {
        // actualiza fondo con el valor que necesita el estado actual
        fondo = new FondoAnimado();
        // realiza esta operacion auxiliar para completar el paso actual
        setScreen(new PantallaPortada(this));
    }

    // este metodo iniciar partida concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void iniciarPartida() {
        // realiza esta operacion auxiliar para completar el paso actual
        setScreen(new PantallaSeleccionModo(this));
    }

    // este metodo mostrar presentacion concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void mostrarPresentacion() {
        // realiza esta operacion auxiliar para completar el paso actual
        setScreen(new PantallaCinematica(this));
    }

    // este metodo terminar presentacion concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void terminarPresentacion() {
        // realiza esta operacion auxiliar para completar el paso actual
        setScreen(new PantallaInicio(this));
    }

    @Override
    // este metodo render concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void render() {
        // realiza esta operacion auxiliar para completar el paso actual
        fondo.actualizar(Gdx.graphics.getDeltaTime());
        // realiza esta operacion auxiliar para completar el paso actual
        super.render();
    }

    // este metodo salir concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void salir() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (cerrando) {
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // actualiza cerrando con el valor que necesita el estado actual
        cerrando = true;
        // realiza esta operacion auxiliar para completar el paso actual
        setScreen(new PantallaCreditos(this));
    }

    // este metodo puede cerrar concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean puedeCerrar() {
        // devuelve este resultado a quien llamo al metodo
        return salidaFinal;
    }

    // este metodo terminar concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void terminar() {
        // actualiza salida final con el valor que necesita el estado actual
        salidaFinal = true;
        // realiza esta operacion auxiliar para completar el paso actual
        Gdx.app.exit();
    }

    @Override
    // este metodo dispose concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dispose() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (getScreen() != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            getScreen().dispose();
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (fondo != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            fondo.dispose();
        }
        // realiza esta operacion auxiliar para completar el paso actual
        super.dispose();
    }
}
