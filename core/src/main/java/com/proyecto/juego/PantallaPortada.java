package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proyecto.juego.visual.Archivos;
import com.proyecto.juego.visual.Tema;

public final class PantallaPortada extends PantallaBase {
    private final Texture imagen;
    private final TextureRegion region;

    public PantallaPortada(Main juego) {
        super(juego);
        imagen = new Texture(Archivos.recurso("ui/portada.png"));
        imagen.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        region = new TextureRegion(imagen);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.justTouched()) {
            juego.mostrarPresentacion();
            return;
        }
        Gdx.gl.glClearColor(Tema.FONDO.r, Tema.FONDO.g, Tema.FONDO.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camara.update();
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        batch.draw(region, 0, 0, 1280, 720);
        batch.end();
        }

    @Override
    public void dispose() {
        imagen.dispose();
        super.dispose();
    }
}
