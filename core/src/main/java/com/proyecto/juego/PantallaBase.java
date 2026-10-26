package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.proyecto.juego.visual.Tema;

public abstract class PantallaBase extends ScreenAdapter {
    protected final Main juego;
    protected final OrthographicCamera camara = new OrthographicCamera();
    protected final FitViewport viewport = new FitViewport(1280, 720, camara);
    protected final SpriteBatch batch = new SpriteBatch();
    protected final ShapeRenderer formas = new ShapeRenderer();
    protected final BitmapFont fuente = new BitmapFont();
    protected float reloj;

    protected PantallaBase(Main juego) {
        this.juego = juego;
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        fuente.getRegion().getTexture().setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Linear,
            com.badlogic.gdx.graphics.Texture.TextureFilter.Linear);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    protected void fondo(float delta, String categoria, String titulo) {
        reloj += delta;
        Gdx.gl.glClearColor(Tema.FONDO.r, Tema.FONDO.g, Tema.FONDO.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camara.update();
        batch.setProjectionMatrix(camara.combined);
        formas.setProjectionMatrix(camara.combined);
        batch.begin();
        batch.setColor(0.62f, 0.62f, 0.62f, 1);
        juego.fondo.dibujar(batch, 0, 0, 1280, 720);
        batch.setColor(Color.WHITE);
        batch.end();
        formas.begin(ShapeRenderer.ShapeType.Filled);
        formas.setColor(Tema.FONDO);
        formas.rect(0, 0, 1280, 75);
        formas.rect(0, 590, 1280, 130);

        formas.end();

        centrado(titulo, 640, 662, 3.2f, Tema.CREMA);

    }

    protected void panel(float x, float y, float ancho, float alto, Color acento) {
        formas.begin(ShapeRenderer.ShapeType.Filled);
        formas.setColor(Tema.BORDE);
        formas.rect(x - 2, y - 2, ancho + 4, alto + 4);
        formas.setColor(Tema.PANEL);
        formas.rect(x, y, ancho, alto);
        formas.setColor(acento);
        formas.rect(x, y, 6, alto);
        formas.end();
    }

    protected void texto(String texto, float x, float y, float escala, Color color) {
        fuente.getData().setScale(escala);
        fuente.setColor(color);
        batch.begin();
        fuente.draw(batch, texto, x, y);
        batch.end();
    }

    protected void centrado(String texto, float centro, float y, float escala, Color color) {
        fuente.getData().setScale(escala);
        GlyphLayout layout = new GlyphLayout(fuente, texto);
        texto(texto, centro - layout.width / 2, y, escala, color);
    }

    protected boolean sobre(float x, float y, float ancho, float alto) {
        com.badlogic.gdx.math.Vector2 punto = viewport.unproject(new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY()));
        return punto.x >= x && punto.x <= x + ancho && punto.y >= y && punto.y <= y + alto;
    }

    protected boolean clic(float x, float y, float ancho, float alto) {
        return Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT) && sobre(x, y, ancho, alto);
    }

    protected boolean boton(String titulo, float x, float y, float ancho, float alto, boolean seleccionado) {
        boolean activo = seleccionado || sobre(x, y, ancho, alto);
        panel(x, y, ancho, alto, activo ? Tema.AMARILLO : Tema.BORDE);
        centrado(titulo, x + ancho / 2, y + alto / 2 + 13, 1.8f, activo ? Tema.AMARILLO : Tema.CREMA);
        return clic(x, y, ancho, alto);
    }
    protected boolean tecla(int... teclas) {
        for (int tecla : teclas) {
            if (Gdx.input.isKeyJustPressed(tecla)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void resize(int ancho, int alto) {
        viewport.update(ancho, alto, true);
    }

    @Override
    public void dispose() {
        fuente.dispose();
        formas.dispose();
        batch.dispose();
    }
}
