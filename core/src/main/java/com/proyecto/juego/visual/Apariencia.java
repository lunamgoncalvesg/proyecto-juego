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
            Pixmap poseCompleta = cargarPose(imagen, pose);
            Pixmap pixeles = recortarTransparencia(poseCompleta);
            if (pixeles != poseCompleta) {
                poseCompleta.dispose();
            }
            try {
                Texture textura = new Texture(pixeles);
                textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                texturas.put(pose, textura);
                // Cada pose tiene su propia textura recortada físicamente. Así las
                // coordenadas de UV no pueden tomar píxeles de otra fila del atlas.
                regiones.put(pose, new TextureRegion(textura));
            } finally {
                pixeles.dispose();
            }
        }
        return texturas.get(pose);
    }

    private Pixmap recortarTransparencia(Pixmap origen) {
        int izquierda = origen.getWidth(), derecha = -1;
        int arriba = origen.getHeight(), abajo = -1;
        for (int y = 0; y < origen.getHeight(); y++) {
            for (int x = 0; x < origen.getWidth(); x++) {
                if ((origen.getPixel(x, y) & 255) == 0) {
                    continue;
                }
                izquierda = Math.min(izquierda, x);
                derecha = Math.max(derecha, x);
                arriba = Math.min(arriba, y);
                abajo = Math.max(abajo, y);
            }
        }
        if (derecha < izquierda) {
            return origen;
        }
        Pixmap recortado = new Pixmap(derecha - izquierda + 1, abajo - arriba + 1, Pixmap.Format.RGBA8888);
        recortado.setBlending(Pixmap.Blending.None);
        recortado.drawPixmap(origen, 0, 0, izquierda, arriba, recortado.getWidth(), recortado.getHeight());
        return recortado;
    }

    private Pixmap cargarPose(FileHandle imagen, String pose) {
        // Si hay un archivo individual, siempre tiene prioridad sobre la hoja.
        // Así una pose nunca puede muestrear píxeles de la celda vecina.
        if (imagen.exists()) {
            return new Pixmap(imagen);
        } else if (!hoja().exists()) {
            return new Pixmap(archivo("idle"));
        } else {
            Pixmap completa = new Pixmap(hoja());
            String[] poses = {"idle", "walk", "jump", "block", "punch", "kick", "special", "win", "lose"};
            int indice = 0;
            for (int i = 0; i < poses.length; i++) {
                if (poses[i].equals(pose)) {
                    indice = i;
                }
            }
            int columna = indice % 3, fila = indice / 3;
            int x = columna * completa.getWidth() / 3;
            int y = fila * completa.getHeight() / 3;
            int ancho = (columna + 1) * completa.getWidth() / 3 - x;
            int alto = (fila + 1) * completa.getHeight() / 3 - y;
            Pixmap pixeles = new Pixmap(ancho, alto, Pixmap.Format.RGBA8888);
            pixeles.setBlending(Pixmap.Blending.None);
            pixeles.drawPixmap(completa, 0, 0, x, y, ancho, alto);
            completa.dispose();
            return pixeles;
        }
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
