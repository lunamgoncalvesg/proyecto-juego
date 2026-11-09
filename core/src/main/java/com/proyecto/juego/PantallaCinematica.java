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

public class PantallaCinematica extends PantallaBase {
    private VideoPlayer video;
    private volatile boolean terminada;
    private boolean fallo;
    private float sinCuadro;

    public PantallaCinematica(Main juego) {
        super(juego);
    }

    @Override
    public void show() {
        super.show();
        FileHandle archivo = Archivos.recurso("cinematicas/presentacion.webm");
        try {
            video = VideoPlayerCreator.createVideoPlayer();
            video.setOnCompletionListener(fin -> terminada = true);
            if (!video.load(archivo)) {
                throw new IllegalStateException("No se pudo abrir la presentación");
            }
            video.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            video.play();
        } catch (Exception | LinkageError e) {
            Gdx.app.error("Cinematica", "No se pudo reproducir " + archivo.path(), e);
            fallo = true;
        }
    }

    @Override
    public void render(float delta) {
        if (terminada || tecla(Input.Keys.ENTER, Input.Keys.ESCAPE, Input.Keys.SPACE) || fallo) {
            juego.terminarPresentacion();
            return;
        }
        Gdx.gl.glClearColor(Tema.FONDO.r, Tema.FONDO.g, Tema.FONDO.b, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camara.update();
        batch.setProjectionMatrix(camara.combined);
        if (video == null) {
            return;
        }
        try {
            video.update();
            Texture cuadro = video.getTexture();
            if (cuadro == null) {
                sinCuadro += delta;
                if (sinCuadro > 8) {
                    fallo = true;
                }
                return;
            }
            sinCuadro = 0;
            float ancho = video.getVideoWidth(), alto = video.getVideoHeight();
            if (ancho > 0 && alto > 0) {
                float escala = Math.min(1280 / ancho, 720 / alto);
                batch.begin();
                batch.draw(new TextureRegion(cuadro, 0, 0, (int) ancho, (int) alto),
                    (1280 - ancho * escala) / 2, (720 - alto * escala) / 2,
                    ancho * escala, alto * escala);
                batch.end();
            }
        } catch (Exception e) {
            Gdx.app.error("Cinematica", "Se interrumpió la presentación", e);
            fallo = true;
        }
    }

    @Override
    public void pause() {
        if (video != null && !fallo) {
            video.pause();
        }
    }

    @Override
    public void resume() {
        if (video != null && !fallo) {
            video.play();
        }
    }

    @Override
    public void dispose() {
        if (video != null) {
            video.dispose();
        }
        super.dispose();
    }
}
