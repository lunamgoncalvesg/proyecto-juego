package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proyecto.juego.visual.Archivos;
import com.proyecto.juego.visual.Tema;

/** Portada estática importada como imagen, seguida por la pantalla de menú. */
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
        centrado("1UP 000", 105, 629, 1.0f, Tema.CREMA);
        centrado("HI-SCORE 12345", 640, 629, 1.0f, Tema.AMARILLO);
        centrado("2UP 000", 1175, 629, 1.0f, Tema.CREMA);
        centrado("STREET FIGHTER", 640, 435, 2.6f, Tema.CREMA);
        centrado("PONELE", 640, 354, 4.2f, Tema.AMARILLO);
        centrado("PRESS START", 640, 225, 1.55f, Tema.ROJO);
        centrado("ENTER / CLIC PARA CONTINUAR", 640, 130, 0.82f, Tema.CREMA);
    }

    @Override
    public void dispose() {
        imagen.dispose();
        super.dispose();
    }
}
