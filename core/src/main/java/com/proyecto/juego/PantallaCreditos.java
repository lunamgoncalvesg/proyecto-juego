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

// esta clase agrupa la logica y los datos principales de pantallacreditos
public class PantallaCreditos extends PantallaBase {
    // este campo representa lineas y conserva ese dato entre llamadas
    private final List<String> lineas = new ArrayList<String>();
    // este campo representa musica y conserva ese dato entre llamadas
    private Music musica;
    // este campo representa duracion y conserva ese dato entre llamadas
    private float duracion;

    // este metodo pantalla creditos concentra una operacion de esta clase y recibe el control desde quien lo llama
    public PantallaCreditos(Main juego) {
        // realiza esta operacion auxiliar para completar el paso actual
        super(juego);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        FileHandle archivo = Archivos.recurso("cinematicas/creditos.txt");
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String contenido = archivo.readString("UTF-8").replace("\r", "");
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (String linea : contenido.split("\n", -1)) {
            // realiza esta operacion auxiliar para completar el paso actual
            lineas.add(linea.trim());
        }
        // actualiza duracion con el valor que necesita el estado actual
        duracion = (lineas.size() * 65 + 780) / 48f + 3;
    }

    @Override
    // este metodo show concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void show() {
        // realiza esta operacion auxiliar para completar el paso actual
        super.show();
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (String extension : new String[]{"mp3", "ogg", "wav"}) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            FileHandle archivo = Archivos.recurso("cinematicas/musica_creditos." + extension);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (!archivo.exists()) {
                // pasa directamente a la siguiente vuelta del ciclo
                continue;
            }
            // agrupa una operacion que puede fallar para poder manejar su error
            try {
                // actualiza musica con el valor que necesita el estado actual
                musica = Gdx.audio.newMusic(archivo);
                // realiza esta operacion auxiliar para completar el paso actual
                musica.setVolume(0.7f);
                // realiza esta operacion auxiliar para completar el paso actual
                musica.play();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } catch (RuntimeException e) {
                // realiza esta operacion auxiliar para completar el paso actual
                Gdx.app.error("Creditos", "No se pudo reproducir la musica", e);
            }
            // sale de la seleccion actual para no continuar con los otros casos
            break;
        }
    }

    @Override
    // este metodo render concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void render(float delta) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        reloj += delta;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla(Input.Keys.ESCAPE, Input.Keys.ENTER, Input.Keys.SPACE) || reloj >= duracion) {
            // realiza esta operacion auxiliar para completar el paso actual
            juego.terminar();
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        Gdx.gl.glClearColor(0, 0, 0, 1);
        // realiza esta operacion auxiliar para completar el paso actual
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // realiza esta operacion auxiliar para completar el paso actual
        viewport.apply();
        // realiza esta operacion auxiliar para completar el paso actual
        camara.update();
        // realiza esta operacion auxiliar para completar el paso actual
        batch.setProjectionMatrix(camara.combined);
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int i = 0; i < lineas.size(); i++) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            float y = -60 + reloj * 48 - i * 65;
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (y < -40 || y > 760) {
                // pasa directamente a la siguiente vuelta del ciclo
                continue;
            }
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            String linea = lineas.get(i);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            boolean titulo = linea.startsWith("#");
            // realiza esta operacion auxiliar para completar el paso actual
            centrado(titulo ? linea.substring(1).trim() : linea, 640, y,
                titulo ? 2.2f : 1.65f, titulo ? Tema.AMARILLO : Color.WHITE);
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (musica != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            musica.setVolume(0.7f * Math.min(1, Math.min(reloj / 2, Math.max(0, (duracion - reloj) / 3))));
        }
    }

    @Override
    // este metodo pause concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void pause() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (musica != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            musica.pause();
        }
    }

    @Override
    // este metodo resume concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void resume() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (musica != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            musica.play();
        }
    }

    @Override
    // este metodo dispose concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dispose() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (musica != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            musica.dispose();
        }
        // realiza esta operacion auxiliar para completar el paso actual
        super.dispose();
    }
}
