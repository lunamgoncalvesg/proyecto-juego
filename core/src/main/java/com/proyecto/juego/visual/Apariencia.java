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
        propia = archivo("idle").exists() || hoja().exists();
    }

    private FileHandle hoja() {
        return Archivos.recurso("personajes/" + tipo.name().toLowerCase(java.util.Locale.ROOT) + "/arcade.png");
    }

    private FileHandle archivo(String pose) {
        String carpeta = "personajes/" + tipo.name().toLowerCase(java.util.Locale.ROOT) + "/";
        FileHandle simple = Archivos.recurso(carpeta + pose + ".png");
        if (simple.exists()) {
            return simple;
        }
        return Archivos.recurso(carpeta + "jugador" + (tipo.ordinal() + 1) + "_" + pose + ".png");
    }

    private Texture textura(String pose) {
        if (!texturas.containsKey(pose)) {
            FileHandle imagen = archivo(pose);
            if (!imagen.exists()) {
                imagen = archivo("idle");
            }
            Pixmap pixeles = cargarPose(imagen, pose);
            try {
                int izquierda = pixeles.getWidth(), derecha = -1, arriba = pixeles.getHeight(), abajo = -1;
                for (int y = 0; y < pixeles.getHeight(); y++) {
                    for (int x = 0; x < pixeles.getWidth(); x++) {
                        if ((pixeles.getPixel(x, y) & 255) < 128) {
                            continue;
                        }
                        izquierda = Math.min(izquierda, x);
                        derecha = Math.max(derecha, x);
                        arriba = Math.min(arriba, y);
                        abajo = Math.max(abajo, y);
                    }
                }
                Texture textura = new Texture(pixeles);
                textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texturas.put(pose, textura);
                regiones.put(pose, derecha < izquierda ? new TextureRegion(textura) :
                    new TextureRegion(textura, izquierda, arriba, derecha - izquierda + 1, abajo - arriba + 1));
            } finally {
                pixeles.dispose();
            }
        }
        return texturas.get(pose);
    }

    private Pixmap cargarPose(FileHandle imagen, String pose) {
        if (hoja().exists()) {
            Pixmap completa = new Pixmap(hoja());
            String[] poses = {"idle", "walk", "jump", "block", "punch", "kick", "special", "win", "lose"};
            int indice = 0;
            for (int i = 0; i < poses.length; i++) {
                if (poses[i].equals(pose)) {
                    indice = i;
                }
            }
            int[] xs = cortes(completa, true, 0, completa.getHeight());
            int[] ys = cortes(completa, false, xs[indice % 3], xs[indice % 3 + 1]);
            int x = xs[indice % 3], y = ys[indice / 3];
            int ancho = xs[indice % 3 + 1] - x, alto = ys[indice / 3 + 1] - y;
            Pixmap pixeles = new Pixmap(ancho, alto, Pixmap.Format.RGBA8888);
            pixeles.setBlending(Pixmap.Blending.None);
            pixeles.drawPixmap(completa, 0, 0, x, y, ancho, alto);
            completa.dispose();
            return pixeles;
        } else {
            return new Pixmap(imagen);
        }
    }

    private int[] cortes(Pixmap imagen, boolean vertical, int desde, int hasta) {
        int longitud = vertical ? imagen.getWidth() : imagen.getHeight();

        int[] cortes = {0, longitud / 3, longitud * 2 / 3, longitud};
        for (int i = 1; i <= 2; i++) {
            int ideal = cortes[i], mejor = Integer.MAX_VALUE, distancia = Integer.MAX_VALUE;
            for (int posicion = ideal - longitud / 12; posicion <= ideal + longitud / 12; posicion++) {
                int ocupados = 0;
                for (int j = desde; j < hasta; j++) {
                    int pixel = vertical ? imagen.getPixel(posicion, j) : imagen.getPixel(j, posicion);
                    if ((pixel & 255) >= 128) {
                        ocupados++;
                    }
                }
                int diferencia = Math.abs(posicion - ideal);
                if (ocupados < mejor || ocupados == mejor && diferencia < distancia) {
                    mejor = ocupados;
                    distancia = diferencia;
                    cortes[i] = posicion;
                }
            }
        }
        return cortes;
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
        float proporcionAltura = 1;
        if (pose.equals("lose")) {
            proporcionAltura = 0.35f;
        } else if (pose.equals("block")) {
            proporcionAltura = 0.9f;
        }
        float altura = alto * proporcionAltura;
        float ancho = altura * imagen.getRegionWidth() / imagen.getRegionHeight();
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
            return Tema.ROJO;
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
