package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proyecto.juego.visual.Archivos;
import com.proyecto.juego.visual.Tema;

// esta clase agrupa la logica y los datos principales de pantallaportada
public final class PantallaPortada extends PantallaBase {
    // este campo representa imagen y conserva ese dato entre llamadas
    private final Texture imagen;
    // este campo representa region y conserva ese dato entre llamadas
    private final TextureRegion region;

    // este metodo pantalla portada concentra una operacion de esta clase y recibe el control desde quien lo llama
    public PantallaPortada(Main juego) {
        // realiza esta operacion auxiliar para completar el paso actual
        super(juego);
        // actualiza imagen con el valor que necesita el estado actual
        imagen = new Texture(Archivos.recurso("ui/portada.png"));
        // realiza esta operacion auxiliar para completar el paso actual
        imagen.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        // actualiza region con el valor que necesita el estado actual
        region = new TextureRegion(imagen);
    }

    @Override
    // este metodo render concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void render(float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.justTouched()) {
            // realiza esta operacion auxiliar para completar el paso actual
            juego.mostrarPresentacion();
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        Gdx.gl.glClearColor(Tema.FONDO.r, Tema.FONDO.g, Tema.FONDO.b, 1);
        // realiza esta operacion auxiliar para completar el paso actual
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // realiza esta operacion auxiliar para completar el paso actual
        viewport.apply();
        // realiza esta operacion auxiliar para completar el paso actual
        camara.update();
        // realiza esta operacion auxiliar para completar el paso actual
        batch.setProjectionMatrix(camara.combined);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.begin();
        // realiza esta operacion auxiliar para completar el paso actual
        batch.draw(region, 0, 0, 1280, 720);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.end();
        }

    @Override
    // este metodo dispose concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dispose() {
        // realiza esta operacion auxiliar para completar el paso actual
        imagen.dispose();
        // realiza esta operacion auxiliar para completar el paso actual
        super.dispose();
    }
}
