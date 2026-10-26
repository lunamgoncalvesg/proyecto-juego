package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.proyecto.juego.visual.Archivos;
import com.proyecto.juego.visual.Tema;
import java.util.ArrayList;
import java.util.List;

public class PantallaCreditos extends PantallaBase {
    private final List<String> lineas = new ArrayList<String>();
    private Music musica;
    private float duracion;

    public PantallaCreditos(Main juego) {
        super(juego);
        FileHandle archivo = Archivos.recurso("cinematicas/creditos.txt");
        String contenido = archivo.readString("UTF-8").replace("\r", "");
        for (String linea : contenido.split("\n", -1)) {
            lineas.add(linea.trim());
        }
        duracion = (lineas.size() * 65 + 780) / 48f + 3;
    }

    @Override
    public void show() {
        super.show();
        for (String extension : new String[]{"mp3", "ogg", "wav"}) {
            FileHandle archivo = Archivos.recurso("cinematicas/musica_creditos." + extension);
            if (!archivo.exists()) {
                continue;
            }
            try {
                musica = Gdx.audio.newMusic(archivo);
                musica.setVolume(0.7f);
                musica.play();
            } catch (RuntimeException e) {
                Gdx.app.error("Creditos", "No se pudo reproducir la musica", e);
            }
            break;
        }
    }

    @Override
    public void render(float delta) {
        reloj += delta;
        if (tecla(Input.Keys.ESCAPE, Input.Keys.ENTER, Input.Keys.SPACE) || reloj >= duracion) {
            juego.terminar();
            return;
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camara.update();
        batch.setProjectionMatrix(camara.combined);
        for (int i = 0; i < lineas.size(); i++) {
            float y = -60 + reloj * 48 - i * 65;
            if (y < -40 || y > 760) {
                continue;
            }
            String linea = lineas.get(i);
            boolean titulo = linea.startsWith("#");
            centrado(titulo ? linea.substring(1).trim() : linea, 640, y,
                titulo ? 2.2f : 1.65f, titulo ? Tema.AMARILLO : Color.WHITE);
        }
        if (musica != null) {
            musica.setVolume(0.7f * Math.min(1, Math.min(reloj / 2, Math.max(0, (duracion - reloj) / 3))));
        }
    }

    @Override
    public void pause() {
        if (musica != null) {
            musica.pause();
        }
    }

    @Override
    public void resume() {
        if (musica != null) {
            musica.play();
        }
    }

    @Override
    public void dispose() {
        if (musica != null) {
            musica.dispose();
        }
        super.dispose();
    }
}
