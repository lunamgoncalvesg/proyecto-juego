package com.proyecto.juego.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node;

public final class FondoAnimado implements Disposable {
    private final List<int[]> cuadros = new ArrayList<int[]>();
    private final List<Float> duraciones = new ArrayList<Float>();
    private Texture textura;
    private Pixmap pixmap;
    private byte[] rgba;
    private int ancho, alto, cuadro;
    private float tiempo;

    private boolean esDia;
    private float revision;

    public FondoAnimado() {
        cambiarFondo(java.time.LocalTime.now().getHour());
    }

    private void cambiarFondo(int hora) {
        esDia = hora >= 7 && hora < 19;
        dispose();
        cuadros.clear();
        duraciones.clear();
        tiempo = 0;
        cuadro = 0;
        FileHandle gif = Archivos.recurso(esDia ? "fondo_dia.gif" : "fondo_noche.gif");

        if (gif.exists()) {
            try {
                cargar(gif);
                return;
            } catch (Exception e) {
                Gdx.app.error("Fondo", "No se pudo leer " + gif.name(), e);
                dispose();
                cuadros.clear();
                duraciones.clear();
            }
        }
        pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Tema.FONDO);
        pixmap.fill();
        textura = new Texture(pixmap);
    }

    private int atributo(Node nodo, String nombre, int valor) {
        Node atributo = nodo == null || nodo.getAttributes() == null ? null : nodo.getAttributes().getNamedItem(nombre);
        return atributo == null ? valor : Integer.parseInt(atributo.getNodeValue());
    }

    private Node buscar(Node raiz, String nombre) {
        if (raiz == null) {
            return null;
        }
        if (raiz.getNodeName().equals(nombre)) {
            return raiz;
        }
        for (Node hijo = raiz.getFirstChild(); hijo != null; hijo = hijo.getNextSibling()) {
            Node encontrado = buscar(hijo, nombre);
            if (encontrado != null) {
                return encontrado;
            }
        }
        return null;
    }

    private void cargar(FileHandle archivo) throws Exception {
        ImageReader lector = ImageIO.getImageReadersByFormatName("gif").next();
        try (ImageInputStream entrada = ImageIO.createImageInputStream(archivo.read())) {
            lector.setInput(entrada);
            Node datos = lector.getStreamMetadata().getAsTree("javax_imageio_gif_stream_1.0");
            Node pantalla = buscar(datos, "LogicalScreenDescriptor");
            ancho = atributo(pantalla, "logicalScreenWidth", 0);
            alto = atributo(pantalla, "logicalScreenHeight", 0);
            int cantidad = lector.getNumImages(true);
            if (ancho <= 0 || alto <= 0 || cantidad > 300 || (long) ancho * alto * cantidad > 64000000L) {
                throw new IllegalArgumentException("GIF demasiado grande: usar hasta 300 cuadros y 64 millones de pixeles totales");
            }
            BufferedImage lienzo = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = lienzo.createGraphics();
            try {
                for (int i = 0; i < cantidad; i++) {
                    BufferedImage imagen = lector.read(i);
                    Node meta = lector.getImageMetadata(i).getAsTree("javax_imageio_gif_image_1.0");
                    Node descriptor = buscar(meta, "ImageDescriptor");
                    Node control = buscar(meta, "GraphicControlExtension");
                    int x = atributo(descriptor, "imageLeftPosition", 0);
                    int y = atributo(descriptor, "imageTopPosition", 0);
                    String descarte = control == null ? "none" : control.getAttributes().getNamedItem("disposalMethod").getNodeValue();
                    int[] anterior = descarte.equals("restoreToPrevious") ? lienzo.getRGB(0, 0, ancho, alto, null, 0, ancho) : null;
                    g.drawImage(imagen, x, y, null);
                    cuadros.add(lienzo.getRGB(0, 0, ancho, alto, null, 0, ancho));
                    duraciones.add(Math.max(0.02f, atributo(control, "delayTime", 10) / 100f));
                    if (anterior != null) {
                        lienzo.setRGB(0, 0, ancho, alto, anterior, 0, ancho);
                    } else if (descarte.equals("restoreToBackgroundColor")) {
                        g.setComposite(AlphaComposite.Clear);
                        g.fillRect(x, y, imagen.getWidth(), imagen.getHeight());
                        g.setComposite(AlphaComposite.SrcOver);
                    }
                }
            } finally {
                g.dispose();
            }
            pixmap = new Pixmap(ancho, alto, Pixmap.Format.RGBA8888);
            rgba = new byte[ancho * alto * 4];
            textura = new Texture(ancho, alto, Pixmap.Format.RGBA8888);
            textura.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            subir();
        } finally {
            lector.dispose();
        }
    }

    private void subir() {
        int[] pixeles = cuadros.get(cuadro);
        for (int i = 0; i < pixeles.length; i++) {
            int argb = pixeles[i], posicion = i * 4;
            rgba[posicion] = (byte) (argb >>> 16);
            rgba[posicion + 1] = (byte) (argb >>> 8);
            rgba[posicion + 2] = (byte) argb;
            rgba[posicion + 3] = (byte) (argb >>> 24);
        }
        pixmap.getPixels().clear();
        pixmap.getPixels().put(rgba);
        pixmap.getPixels().position(0);
        textura.draw(pixmap, 0, 0);
    }

    public void actualizar(float delta) {
        revision += delta;
        if (revision >= 30) {
            revision = 0;
            int hora = java.time.LocalTime.now().getHour();
            if (esDia != (hora >= 7 && hora < 19)) {
                cambiarFondo(hora);
            }
        }
        if (cuadros.isEmpty()) {
            return;
        }
        tiempo += Math.min(delta, 0.25f);
        int anterior = cuadro;
        while (tiempo >= duraciones.get(cuadro)) {
            tiempo -= duraciones.get(cuadro);
            cuadro = (cuadro + 1) % cuadros.size();
        }
        if (anterior != cuadro) {
            subir();
        }
    }

    public void dibujar(SpriteBatch batch, float x, float y, float ancho, float alto) {
        batch.draw(textura, x, y, ancho, alto);
    }

    @Override
    public void dispose() {
        if (textura != null) {
            textura.dispose();
            textura = null;
        }
        if (pixmap != null) {
            pixmap.dispose();
            pixmap = null;
        }
    }
}
