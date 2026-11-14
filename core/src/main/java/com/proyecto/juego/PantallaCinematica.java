package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.proyecto.juego.visual.Archivos;
import com.proyecto.juego.visual.Tema;

// esta clase agrupa la logica y los datos principales de pantallacinematica
public class PantallaCinematica extends PantallaBase {
    // este campo representa video y conserva ese dato entre llamadas
    private VideoPlayer video;
    // este campo representa terminada y conserva ese dato entre llamadas
    private volatile boolean terminada;
    // este campo representa fallo y conserva ese dato entre llamadas
    private boolean fallo;
    // este campo representa sincuadro y conserva ese dato entre llamadas
    private float sinCuadro;

    // este metodo pantalla cinematica concentra una operacion de esta clase y recibe el control desde quien lo llama
    public PantallaCinematica(Main juego) {
        // realiza esta operacion auxiliar para completar el paso actual
        super(juego);
    }

    @Override
    // este metodo show concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void show() {
        // realiza esta operacion auxiliar para completar el paso actual
        super.show();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        FileHandle archivo = Archivos.recurso("cinematicas/presentacion.webm");
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // actualiza video con el valor que necesita el estado actual
            video = VideoPlayerCreator.createVideoPlayer();
            // realiza esta operacion auxiliar para completar el paso actual
            video.setOnCompletionListener(fin -> terminada = true);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (!video.load(archivo)) {
                // detiene este flujo e informa que los datos o la operacion no son validos
                throw new IllegalStateException("No se pudo abrir la presentación");
            }
            // realiza esta operacion auxiliar para completar el paso actual
            video.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            // realiza esta operacion auxiliar para completar el paso actual
            video.play();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (Exception | LinkageError e) {
            // realiza esta operacion auxiliar para completar el paso actual
            Gdx.app.error("Cinematica", "No se pudo reproducir " + archivo.path(), e);
            // actualiza fallo con el valor que necesita el estado actual
            fallo = true;
        }
    }

    @Override
    // este metodo render concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void render(float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (terminada || tecla(Input.Keys.ENTER, Input.Keys.ESCAPE, Input.Keys.SPACE) || fallo) {
            // realiza esta operacion auxiliar para completar el paso actual
            juego.terminarPresentacion();
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        Gdx.gl.glClearColor(Tema.FONDO.r, Tema.FONDO.g, Tema.FONDO.b, 1);
        // realiza esta operacion auxiliar para completar el paso actual
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);
        // realiza esta operacion auxiliar para completar el paso actual
        viewport.apply();
        // realiza esta operacion auxiliar para completar el paso actual
        camara.update();
        // realiza esta operacion auxiliar para completar el paso actual
        batch.setProjectionMatrix(camara.combined);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (video == null) {
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // realiza esta operacion auxiliar para completar el paso actual
            video.update();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            Texture cuadro = video.getTexture();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (cuadro == null) {
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                sinCuadro += delta;
                // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                if (sinCuadro > 8) {
                    // actualiza fallo con el valor que necesita el estado actual
                    fallo = true;
                }
                // devuelve este resultado a quien llamo al metodo
                return;
            }
            // actualiza sin cuadro con el valor que necesita el estado actual
            sinCuadro = 0;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            float ancho = video.getVideoWidth(), alto = video.getVideoHeight();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (ancho > 0 && alto > 0) {
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                float escala = Math.min(1280 / ancho, 720 / alto);
                // realiza esta operacion auxiliar para completar el paso actual
                batch.begin();
                // realiza esta operacion auxiliar para completar el paso actual
                batch.draw(new TextureRegion(cuadro, 0, 0, (int) ancho, (int) alto),
                    (1280 - ancho * escala) / 2, (720 - alto * escala) / 2,
                    ancho * escala, alto * escala);
                // realiza esta operacion auxiliar para completar el paso actual
                batch.end();
            }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (Exception e) {
            // realiza esta operacion auxiliar para completar el paso actual
            Gdx.app.error("Cinematica", "Se interrumpió la presentación", e);
            // actualiza fallo con el valor que necesita el estado actual
            fallo = true;
        }
    }

    @Override
    // este metodo pause concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void pause() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (video != null && !fallo) {
            // realiza esta operacion auxiliar para completar el paso actual
            video.pause();
        }
    }

    @Override
    // este metodo resume concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void resume() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (video != null && !fallo) {
            // realiza esta operacion auxiliar para completar el paso actual
            video.play();
        }
    }

    @Override
    // este metodo dispose concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dispose() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (video != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            video.dispose();
        }
        // realiza esta operacion auxiliar para completar el paso actual
        super.dispose();
    }
}
