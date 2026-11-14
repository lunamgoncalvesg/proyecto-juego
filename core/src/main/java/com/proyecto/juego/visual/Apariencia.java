package com.proyecto.juego.visual;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.proyecto.juego.personajes.TipoPersonaje;
import java.util.HashMap;
import java.util.Map;

// esta clase agrupa la logica y los datos principales de apariencia
public final class Apariencia implements Disposable {
    // este campo representa tipo y conserva ese dato entre llamadas
    private final TipoPersonaje tipo;
    // este campo representa string y conserva ese dato entre llamadas
    private final Map<String, Texture> texturas = new HashMap<String, Texture>();
    // este campo representa string y conserva ese dato entre llamadas
    private final Map<String, TextureRegion> regiones = new HashMap<String, TextureRegion>();
    // este campo representa propia y conserva ese dato entre llamadas
    private final boolean propia;

    // este metodo apariencia concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Apariencia(TipoPersonaje tipo) {
        // actualiza this.tipo con el valor que necesita el estado actual
        this.tipo = tipo;
        // actualiza propia con el valor que necesita el estado actual
        propia = hoja().exists();
    }

    // este metodo hoja concentra una operacion de esta clase y recibe el control desde quien lo llama
    private FileHandle hoja() {
        // devuelve este resultado a quien llamo al metodo
        return Archivos.recurso("personajes/" + tipo.name().toLowerCase(java.util.Locale.ROOT) + "/arcade.png");
    }

    private Texture textura(String pose) {
        // Una sola textura compartida por todos los estados del personaje.
        if (!texturas.containsKey("hoja")) {
            Pixmap completa = new Pixmap(hoja());
            try {
                Texture textura = new Texture(completa);
                textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texturas.put("hoja", textura);
                String[] poses = {"idle", "walk", "jump", "block", "punch", "kick", "special", "win", "lose"};
                for (int i = 0; i < poses.length; i++) {
                    int x0 = (i % 3) * completa.getWidth() / 3;
                    int y0 = (i / 3) * completa.getHeight() / 3;
                    int x1 = (i % 3 + 1) * completa.getWidth() / 3;
                    int y1 = (i / 3 + 1) * completa.getHeight() / 3;
                    int izq = x1, arriba = y1, der = -1, abajo = -1;
                    for (int y = y0; y < y1; y++) {
                        for (int x = x0; x < x1; x++) {
                            if ((completa.getPixel(x, y) & 255) == 0) continue;
                            izq = Math.min(izq, x); der = Math.max(der, x);
                            arriba = Math.min(arriba, y); abajo = Math.max(abajo, y);
                        }
                    }
                    regiones.put(poses[i], der < izq
                        ? new TextureRegion(textura, x0, y0, x1 - x0, y1 - y0)
                        : new TextureRegion(textura, izq, arriba, der - izq + 1, abajo - arriba + 1));
                }
                regiones.put("portrait", regiones.get("idle"));
            } finally {
                completa.dispose();
            }
        }
        return texturas.get("hoja");
    }
    // este metodo region concentra una operacion de esta clase y recibe el control desde quien lo llama
    public TextureRegion region(String pose, boolean izquierda) {
        // realiza esta operacion auxiliar para completar el paso actual
        textura(pose);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        TextureRegion region = new TextureRegion(regiones.get(pose));
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (izquierda) {
            // realiza esta operacion auxiliar para completar el paso actual
            region.flip(true, false);
        }
        // devuelve este resultado a quien llamo al metodo
        return region;
    }

    // este metodo dibujar concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dibujar(SpriteBatch batch, String pose, float centro, float piso, float alto, boolean izquierda) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        TextureRegion imagen = region(pose, izquierda);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float proporcionAltura = pose.equals("block") ? 0.9f : 1f;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float altura = alto * proporcionAltura;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float ancho = altura * imagen.getRegionWidth() / imagen.getRegionHeight();
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (pose.equals("lose")) {
            // actualiza ancho con el valor que necesita el estado actual
            ancho = alto * 1.6f;
            // actualiza altura con el valor que necesita el estado actual
            altura = ancho * imagen.getRegionHeight() / imagen.getRegionWidth();
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (ancho > alto * 1.6f) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            altura *= alto * 1.6f / ancho;
            // actualiza ancho con el valor que necesita el estado actual
            ancho = alto * 1.6f;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        batch.setColor(color());
        // realiza esta operacion auxiliar para completar el paso actual
        batch.draw(imagen, centro - ancho / 2, piso, ancho, altura);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.setColor(Color.WHITE);
    }

    // este metodo retrato concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void retrato(SpriteBatch batch, float x, float y, float ancho, float alto) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        TextureRegion imagen = region("portrait", false);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float escala = Math.min(ancho / imagen.getRegionWidth(), alto / imagen.getRegionHeight());
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float w = imagen.getRegionWidth() * escala, h = imagen.getRegionHeight() * escala;
        // realiza esta operacion auxiliar para completar el paso actual
        batch.setColor(color());
        // realiza esta operacion auxiliar para completar el paso actual
        batch.draw(imagen, x + (ancho - w) / 2, y, w, h);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.setColor(Color.WHITE);
    }

    // este metodo color concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Color color() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (propia) {
            // devuelve este resultado a quien llamo al metodo
            return Color.WHITE;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tipo == TipoPersonaje.RAPIDO) {
            // devuelve este resultado a quien llamo al metodo
            return Tema.AMARILLO;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tipo == TipoPersonaje.FUERTE) {
            // devuelve este resultado a quien llamo al metodo
            return Tema.AMARILLO;
        }
        // devuelve este resultado a quien llamo al metodo
        return Tema.CREMA;
    }

    @Override
    // este metodo dispose concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dispose() {
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (Texture textura : texturas.values()) {
            // realiza esta operacion auxiliar para completar el paso actual
            textura.dispose();
        }
        // realiza esta operacion auxiliar para completar el paso actual
        texturas.clear();
        // realiza esta operacion auxiliar para completar el paso actual
        regiones.clear();
    }
}
