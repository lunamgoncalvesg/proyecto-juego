package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.proyecto.juego.visual.FuenteRetro;
import com.proyecto.juego.visual.Tema;

public abstract class PantallaBase extends ScreenAdapter {
    private static final float ANCHO_TEXTO_MAXIMO = 1220;
    protected final Main juego;
    protected final OrthographicCamera camara = new OrthographicCamera();
    protected final FitViewport viewport = new FitViewport(1280, 720, camara);
    protected final SpriteBatch batch = new SpriteBatch();
    protected final ShapeRenderer formas = new ShapeRenderer();
    protected final FuenteRetro fuente = new FuenteRetro();
    protected float reloj;
    private int cursorAnteriorX = -1, cursorAnteriorY = -1;
    private boolean mouseControlando;

    protected PantallaBase(Main juego) {
        this.juego = juego;
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    protected void fondo(float delta) {
        reloj += delta;
        actualizarFocoVisual();
        Gdx.gl.glClearColor(Tema.FONDO.r, Tema.FONDO.g, Tema.FONDO.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camara.update();
        batch.setProjectionMatrix(camara.combined);
        formas.setProjectionMatrix(camara.combined);
        batch.begin();
        juego.fondo.dibujar(batch, 0, 0, 1280, 720);
        batch.setColor(Color.WHITE);
        batch.end();
        formas.begin(ShapeRenderer.ShapeType.Filled);
        formas.setColor(Tema.FONDO);
        formas.rect(0, 0, 1280, 75);
        formas.rect(0, 590, 1280, 130);

        formas.end();


    }

    protected void panel(float x, float y, float ancho, float alto, Color acento) {
        formas.begin(ShapeRenderer.ShapeType.Filled);
        formas.setColor(acento);
        formas.rect(x - 2, y - 2, ancho + 4, alto + 4);
        formas.setColor(Tema.PANEL);
        formas.rect(x, y, ancho, alto);
        formas.end();
        formas.begin(ShapeRenderer.ShapeType.Line);
        formas.setColor(Tema.BORDE);
        formas.rect(x + 5, y + 5, ancho - 10, alto - 10);
        formas.end();
    }

    protected void texto(String texto, float x, float y, float escala, Color color) {
        escala = ajustarEscala(texto, escala);
        float ancho = fuente.ancho(texto, escala);
        x = Math.max(30, Math.min(x, 1250 - ancho));
        batch.begin();
        fuente.dibujar(batch, texto, x, y, escala, color);
        batch.end();
    }

    protected void centrado(String texto, float centro, float y, float escala, Color color) {
        escala = ajustarEscala(texto, escala);
        float ancho = fuente.ancho(texto, escala);
        texto(texto, Math.max(30, Math.min(centro - ancho / 2, 1250 - ancho)), y, escala, color);
    }

    private float ajustarEscala(String texto, float escala) {
        float ancho = fuente.ancho(texto, escala);
        return ancho > ANCHO_TEXTO_MAXIMO ? escala * ANCHO_TEXTO_MAXIMO / ancho : escala;
    }

    protected boolean sobre(float x, float y, float ancho, float alto) {
        com.badlogic.gdx.math.Vector2 punto = viewport.unproject(new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY()));
        return punto.x >= x && punto.x <= x + ancho && punto.y >= y && punto.y <= y + alto;
    }

    protected boolean clic(float x, float y, float ancho, float alto) {
        return Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT) && sobre(x, y, ancho, alto);
    }

    protected boolean boton(String titulo, float x, float y, float ancho, float alto, boolean seleccionado) {
        boolean hover = sobre(x, y, ancho, alto);
        boolean activo = mouseControlando ? hover : seleccionado;
        formas.begin(ShapeRenderer.ShapeType.Filled);
        formas.setColor(activo ? Tema.AMARILLO : Tema.BORDE);
        formas.rect(x, y, ancho, alto);
        formas.setColor(activo ? Tema.AMARILLO : Tema.PANEL);
        formas.rect(x + 2, y + 2, ancho - 4, alto - 4);
        formas.end();
        float escala = 1.2f;
        float altoTexto = 7 * escala * 1.8f;
        centrado(titulo, x + ancho / 2, y + (alto - altoTexto) / 2 + 1, escala,
            activo ? Tema.FONDO : Tema.CREMA);
        return clic(x, y, ancho, alto);
    }

    private void actualizarFocoVisual() {
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        boolean movimientoMouse = cursorAnteriorX >= 0 && (x != cursorAnteriorX || y != cursorAnteriorY);
        boolean tecla = Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)
            || Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)
            || Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.A)
            || Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.D)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
        if (tecla) {
            mouseControlando = false;
        } else if (movimientoMouse || Gdx.input.justTouched()) {
            mouseControlando = true;
        }
        cursorAnteriorX = x;
        cursorAnteriorY = y;
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
