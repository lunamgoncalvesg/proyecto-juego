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

public final class Apariencia implements Disposable {
    private final TipoPersonaje tipo;
    private final Map<String, Texture> texturas = new HashMap<String, Texture>();
    private final Map<String, TextureRegion> regiones = new HashMap<String, TextureRegion>();
    private final boolean propia;

    public Apariencia(TipoPersonaje tipo) {
        this.tipo = tipo;
        propia = hoja().exists();
    }

    private FileHandle hoja() {
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
    public TextureRegion region(String pose, boolean izquierda) {
        textura(pose);
        TextureRegion region = new TextureRegion(regiones.get(pose));
        if (izquierda) {
            region.flip(true, false);
        }
        return region;
    }

    public void dibujar(SpriteBatch batch, String pose, float centro, float piso, float alto, boolean izquierda) {
        TextureRegion imagen = region(pose, izquierda);
        float proporcionAltura = pose.equals("block") ? 0.9f : 1f;
        float altura = alto * proporcionAltura;
        float ancho = altura * imagen.getRegionWidth() / imagen.getRegionHeight();
        if (pose.equals("lose")) {
            ancho = alto * 1.6f;
            altura = ancho * imagen.getRegionHeight() / imagen.getRegionWidth();
        }
        if (ancho > alto * 1.6f) {
            altura *= alto * 1.6f / ancho;
            ancho = alto * 1.6f;
        }
        batch.setColor(color());
        batch.draw(imagen, centro - ancho / 2, piso, ancho, altura);
        batch.setColor(Color.WHITE);
    }

    public void retrato(SpriteBatch batch, float x, float y, float ancho, float alto) {
        TextureRegion imagen = region("portrait", false);
        float escala = Math.min(ancho / imagen.getRegionWidth(), alto / imagen.getRegionHeight());
        float w = imagen.getRegionWidth() * escala, h = imagen.getRegionHeight() * escala;
        batch.setColor(color());
        batch.draw(imagen, x + (ancho - w) / 2, y, w, h);
        batch.setColor(Color.WHITE);
    }

    public Color color() {
        if (propia) {
            return Color.WHITE;
        }
        if (tipo == TipoPersonaje.RAPIDO) {
            return Tema.AMARILLO;
        }
        if (tipo == TipoPersonaje.FUERTE) {
            return Tema.AMARILLO;
        }
        return Tema.CREMA;
    }

    @Override
    public void dispose() {
        for (Texture textura : texturas.values()) {
            textura.dispose();
        }
        texturas.clear();
        regiones.clear();
    }
}
