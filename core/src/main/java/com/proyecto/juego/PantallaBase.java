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

// esta clase agrupa la logica y los datos principales de pantallabase
public abstract class PantallaBase extends ScreenAdapter {
    // este campo representa ancho texto maximo y conserva ese dato entre llamadas
    private static final float ANCHO_TEXTO_MAXIMO = 1220;
    // este campo representa juego y conserva ese dato entre llamadas
    protected final Main juego;
    // este campo crea y conserva camara para reutilizar este recurso
    protected final OrthographicCamera camara = new OrthographicCamera();
    // este campo crea y conserva viewport para reutilizar este recurso
    protected final FitViewport viewport = new FitViewport(1280, 720, camara);
    // este campo crea y conserva batch para reutilizar este recurso
    protected final SpriteBatch batch = new SpriteBatch();
    // este campo crea y conserva formas para reutilizar este recurso
    protected final ShapeRenderer formas = new ShapeRenderer();
    // este campo crea y conserva fuente para reutilizar este recurso
    protected final FuenteRetro fuente = new FuenteRetro();
    // este campo representa reloj y conserva ese dato entre llamadas
    protected float reloj;
    // este campo representa cursoranteriorx y conserva ese dato entre llamadas
    private int cursorAnteriorX = -1, cursorAnteriorY = -1;
    // este campo representa mousecontrolando y conserva ese dato entre llamadas
    private boolean mouseControlando;

    // este metodo pantalla base concentra una operacion de esta clase y recibe el control desde quien lo llama
    protected PantallaBase(Main juego) {
        // actualiza this.juego con el valor que necesita el estado actual
        this.juego = juego;
        // realiza esta operacion auxiliar para completar el paso actual
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    // este metodo show concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void show() {
        // realiza esta operacion auxiliar para completar el paso actual
        Gdx.input.setInputProcessor(null);
    }

    // este metodo fondo concentra una operacion de esta clase y recibe el control desde quien lo llama
    protected void fondo(float delta) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        reloj += delta;
        // realiza esta operacion auxiliar para completar el paso actual
        actualizarFocoVisual();
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
        formas.setProjectionMatrix(camara.combined);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.begin();
        // realiza esta operacion auxiliar para completar el paso actual
        juego.fondo.dibujar(batch, 0, 0, 1280, 720);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.setColor(Color.WHITE);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.end();
        // realiza esta operacion auxiliar para completar el paso actual
        formas.begin(ShapeRenderer.ShapeType.Filled);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(Tema.FONDO);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(0, 0, 1280, 75);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(0, 590, 1280, 130);

        // realiza esta operacion auxiliar para completar el paso actual
        formas.end();


    }

    // este metodo panel concentra una operacion de esta clase y recibe el control desde quien lo llama
    protected void panel(float x, float y, float ancho, float alto, Color acento) {
        // realiza esta operacion auxiliar para completar el paso actual
        formas.begin(ShapeRenderer.ShapeType.Filled);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(acento);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(x - 2, y - 2, ancho + 4, alto + 4);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(Tema.PANEL);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(x, y, ancho, alto);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.end();
        // realiza esta operacion auxiliar para completar el paso actual
        formas.begin(ShapeRenderer.ShapeType.Line);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(Tema.BORDE);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(x + 5, y + 5, ancho - 10, alto - 10);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.end();
    }

    // este metodo texto concentra una operacion de esta clase y recibe el control desde quien lo llama
    protected void texto(String texto, float x, float y, float escala, Color color) {
        // actualiza escala con el valor que necesita el estado actual
        escala = ajustarEscala(texto, escala);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float ancho = fuente.ancho(texto, escala);
        // actualiza x con el valor que necesita el estado actual
        x = Math.max(30, Math.min(x, 1250 - ancho));
        // realiza esta operacion auxiliar para completar el paso actual
        batch.begin();
        // realiza esta operacion auxiliar para completar el paso actual
        fuente.dibujar(batch, texto, x, y, escala, color);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.end();
    }

    // este metodo centrado concentra una operacion de esta clase y recibe el control desde quien lo llama
    protected void centrado(String texto, float centro, float y, float escala, Color color) {
        // actualiza escala con el valor que necesita el estado actual
        escala = ajustarEscala(texto, escala);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float ancho = fuente.ancho(texto, escala);
        // realiza esta operacion auxiliar para completar el paso actual
        texto(texto, Math.max(30, Math.min(centro - ancho / 2, 1250 - ancho)), y, escala, color);
    }

    // este metodo reduce el texto si excede el ancho disponible
    private float ajustarEscala(String texto, float escala) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float ancho = fuente.ancho(texto, escala);
        // devuelve este resultado a quien llamo al metodo
        return ancho > ANCHO_TEXTO_MAXIMO ? escala * ANCHO_TEXTO_MAXIMO / ancho : escala;
    }

    // este metodo sobre concentra una operacion de esta clase y recibe el control desde quien lo llama
    protected boolean sobre(float x, float y, float ancho, float alto) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        com.badlogic.gdx.math.Vector2 punto = viewport.unproject(new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY()));
        // devuelve este resultado a quien llamo al metodo
        return punto.x >= x && punto.x <= x + ancho && punto.y >= y && punto.y <= y + alto;
    }

    // este metodo clic concentra una operacion de esta clase y recibe el control desde quien lo llama
    protected boolean clic(float x, float y, float ancho, float alto) {
        // devuelve este resultado a quien llamo al metodo
        return Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT) && sobre(x, y, ancho, alto);
    }

    // este metodo boton concentra una operacion de esta clase y recibe el control desde quien lo llama
    protected boolean boton(String titulo, float x, float y, float ancho, float alto, boolean seleccionado) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean hover = sobre(x, y, ancho, alto);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean activo = mouseControlando ? hover : seleccionado;
        // realiza esta operacion auxiliar para completar el paso actual
        formas.begin(ShapeRenderer.ShapeType.Filled);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(activo ? Tema.AMARILLO : Tema.BORDE);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(x, y, ancho, alto);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(activo ? Tema.AMARILLO : Tema.PANEL);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(x + 2, y + 2, ancho - 4, alto - 4);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.end();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float escala = 1.2f;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float altoTexto = 7 * escala * 1.8f;
        // realiza esta operacion auxiliar para completar el paso actual
        centrado(titulo, x + ancho / 2, y + (alto - altoTexto) / 2 + 1, escala,
            activo ? Tema.FONDO : Tema.CREMA);
        // devuelve este resultado a quien llamo al metodo
        return clic(x, y, ancho, alto);
    }

    // este metodo decide si la navegacion actual usa teclado o mouse
    private void actualizarFocoVisual() {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int x = Gdx.input.getX();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int y = Gdx.input.getY();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean movimientoMouse = cursorAnteriorX >= 0 && (x != cursorAnteriorX || y != cursorAnteriorY);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean tecla = Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            || Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            || Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.A)
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            || Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.D)
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla) {
            // actualiza mouse controlando con el valor que necesita el estado actual
            mouseControlando = false;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else if (movimientoMouse || Gdx.input.justTouched()) {
            // actualiza mouse controlando con el valor que necesita el estado actual
            mouseControlando = true;
        }
        // actualiza cursor anterior x con el valor que necesita el estado actual
        cursorAnteriorX = x;
        // actualiza cursor anterior y con el valor que necesita el estado actual
        cursorAnteriorY = y;
    }
    // este metodo tecla concentra una operacion de esta clase y recibe el control desde quien lo llama
    protected boolean tecla(int... teclas) {
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int tecla : teclas) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (Gdx.input.isKeyJustPressed(tecla)) {
                // devuelve este resultado a quien llamo al metodo
                return true;
            }
        }
        // devuelve este resultado a quien llamo al metodo
        return false;
    }

    @Override
    // este metodo resize concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void resize(int ancho, int alto) {
        // realiza esta operacion auxiliar para completar el paso actual
        viewport.update(ancho, alto, true);
    }

    @Override
    // este metodo dispose concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dispose() {
        // realiza esta operacion auxiliar para completar el paso actual
        fuente.dispose();
        // realiza esta operacion auxiliar para completar el paso actual
        formas.dispose();
        // realiza esta operacion auxiliar para completar el paso actual
        batch.dispose();
    }
}
