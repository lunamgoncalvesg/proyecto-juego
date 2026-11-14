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

// esta clase agrupa la logica y los datos principales de fondoanimado
public final class FondoAnimado implements Disposable {
    // este campo representa cuadros y conserva ese dato entre llamadas
    private final List<int[]> cuadros = new ArrayList<int[]>();
    // este campo representa duraciones y conserva ese dato entre llamadas
    private final List<Float> duraciones = new ArrayList<Float>();
    // este campo representa textura y conserva ese dato entre llamadas
    private Texture textura;
    // este campo representa pixmap y conserva ese dato entre llamadas
    private Pixmap pixmap;
    // este campo representa rgba y conserva ese dato entre llamadas
    private byte[] rgba;
    // este campo representa ancho y conserva ese dato entre llamadas
    private int ancho, alto, cuadro;
    // este campo representa tiempo y conserva ese dato entre llamadas
    private float tiempo;

    // este campo representa esdia y conserva ese dato entre llamadas
    private boolean esDia;
    // este campo representa revision y conserva ese dato entre llamadas
    private float revision;

    // este metodo fondo animado concentra una operacion de esta clase y recibe el control desde quien lo llama
    public FondoAnimado() {
        // realiza esta operacion auxiliar para completar el paso actual
        cambiarFondo(java.time.LocalTime.now().getHour());
    }

    // este metodo elige el fondo segun la hora y reinicia su animacion
    private void cambiarFondo(int hora) {
        // actualiza es dia con el valor que necesita el estado actual
        esDia = hora >= 7 && hora < 19;
        // realiza esta operacion auxiliar para completar el paso actual
        dispose();
        // realiza esta operacion auxiliar para completar el paso actual
        cuadros.clear();
        // realiza esta operacion auxiliar para completar el paso actual
        duraciones.clear();
        // actualiza tiempo con el valor que necesita el estado actual
        tiempo = 0;
        // actualiza cuadro con el valor que necesita el estado actual
        cuadro = 0;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        FileHandle gif = Archivos.recurso(esDia ? "fondo_dia.gif" : "fondo_noche.gif");

        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (gif.exists()) {
            // agrupa una operacion que puede fallar para poder manejar su error
            try {
                // realiza esta operacion auxiliar para completar el paso actual
                cargar(gif);
                // devuelve este resultado a quien llamo al metodo
                return;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } catch (Exception e) {
                // realiza esta operacion auxiliar para completar el paso actual
                Gdx.app.error("Fondo", "No se pudo leer " + gif.name(), e);
                // realiza esta operacion auxiliar para completar el paso actual
                dispose();
                // realiza esta operacion auxiliar para completar el paso actual
                cuadros.clear();
                // realiza esta operacion auxiliar para completar el paso actual
                duraciones.clear();
            }
        }
        // actualiza pixmap con el valor que necesita el estado actual
        pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        // realiza esta operacion auxiliar para completar el paso actual
        pixmap.setColor(Tema.FONDO);
        // realiza esta operacion auxiliar para completar el paso actual
        pixmap.fill();
        // actualiza textura con el valor que necesita el estado actual
        textura = new Texture(pixmap);
    }

    // este metodo atributo concentra una operacion de esta clase y recibe el control desde quien lo llama
    private int atributo(Node nodo, String nombre, int valor) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Node atributo = nodo == null || nodo.getAttributes() == null ? null : nodo.getAttributes().getNamedItem(nombre);
        // devuelve este resultado a quien llamo al metodo
        return atributo == null ? valor : Integer.parseInt(atributo.getNodeValue());
    }

    // este metodo buscar concentra una operacion de esta clase y recibe el control desde quien lo llama
    private Node buscar(Node raiz, String nombre) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (raiz == null) {
            // devuelve este resultado a quien llamo al metodo
            return null;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (raiz.getNodeName().equals(nombre)) {
            // devuelve este resultado a quien llamo al metodo
            return raiz;
        }
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (Node hijo = raiz.getFirstChild(); hijo != null; hijo = hijo.getNextSibling()) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            Node encontrado = buscar(hijo, nombre);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (encontrado != null) {
                // devuelve este resultado a quien llamo al metodo
                return encontrado;
            }
        }
        // devuelve este resultado a quien llamo al metodo
        return null;
    }

    // este metodo lee los cuadros del gif y respeta su posicion y duracion
    private void cargar(FileHandle archivo) throws Exception {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        ImageReader lector = ImageIO.getImageReadersByFormatName("gif").next();
        // agrupa una operacion que puede fallar para poder manejar su error
        try (ImageInputStream entrada = ImageIO.createImageInputStream(archivo.read())) {
            // realiza esta operacion auxiliar para completar el paso actual
            lector.setInput(entrada);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            Node datos = lector.getStreamMetadata().getAsTree("javax_imageio_gif_stream_1.0");
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            Node pantalla = buscar(datos, "LogicalScreenDescriptor");
            // actualiza ancho con el valor que necesita el estado actual
            ancho = atributo(pantalla, "logicalScreenWidth", 0);
            // actualiza alto con el valor que necesita el estado actual
            alto = atributo(pantalla, "logicalScreenHeight", 0);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int cantidad = lector.getNumImages(true);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (ancho <= 0 || alto <= 0 || cantidad > 300 || (long) ancho * alto * cantidad > 64000000L) {
                // detiene este flujo e informa que los datos o la operacion no son validos
                throw new IllegalArgumentException("GIF demasiado grande: usar hasta 300 cuadros y 64 millones de pixeles totales");
            }
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            BufferedImage lienzo = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            Graphics2D g = lienzo.createGraphics();
            // agrupa una operacion que puede fallar para poder manejar su error
            try {
                // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
                for (int i = 0; i < cantidad; i++) {
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    BufferedImage imagen = lector.read(i);
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    Node meta = lector.getImageMetadata(i).getAsTree("javax_imageio_gif_image_1.0");
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    Node descriptor = buscar(meta, "ImageDescriptor");
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    Node control = buscar(meta, "GraphicControlExtension");
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    int x = atributo(descriptor, "imageLeftPosition", 0);
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    int y = atributo(descriptor, "imageTopPosition", 0);
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    String descarte = control == null ? "none" : control.getAttributes().getNamedItem("disposalMethod").getNodeValue();
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    int[] anterior = descarte.equals("restoreToPrevious") ? lienzo.getRGB(0, 0, ancho, alto, null, 0, ancho) : null;
                    // realiza esta operacion auxiliar para completar el paso actual
                    g.drawImage(imagen, x, y, null);
                    // realiza esta operacion auxiliar para completar el paso actual
                    cuadros.add(lienzo.getRGB(0, 0, ancho, alto, null, 0, ancho));
                    // realiza esta operacion auxiliar para completar el paso actual
                    duraciones.add(Math.max(0.02f, atributo(control, "delayTime", 10) / 100f));
                    // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                    if (anterior != null) {
                        // realiza esta operacion auxiliar para completar el paso actual
                        lienzo.setRGB(0, 0, ancho, alto, anterior, 0, ancho);
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    } else if (descarte.equals("restoreToBackgroundColor")) {
                        // realiza esta operacion auxiliar para completar el paso actual
                        g.setComposite(AlphaComposite.Clear);
                        // realiza esta operacion auxiliar para completar el paso actual
                        g.fillRect(x, y, imagen.getWidth(), imagen.getHeight());
                        // realiza esta operacion auxiliar para completar el paso actual
                        g.setComposite(AlphaComposite.SrcOver);
                    }
                }
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } finally {
                // realiza esta operacion auxiliar para completar el paso actual
                g.dispose();
            }
            // actualiza pixmap con el valor que necesita el estado actual
            pixmap = new Pixmap(ancho, alto, Pixmap.Format.RGBA8888);
            // actualiza rgba con el valor que necesita el estado actual
            rgba = new byte[ancho * alto * 4];
            // actualiza textura con el valor que necesita el estado actual
            textura = new Texture(ancho, alto, Pixmap.Format.RGBA8888);
            // realiza esta operacion auxiliar para completar el paso actual
            textura.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            // realiza esta operacion auxiliar para completar el paso actual
            subir();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } finally {
            // realiza esta operacion auxiliar para completar el paso actual
            lector.dispose();
        }
    }

    // este metodo subir concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void subir() {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int[] pixeles = cuadros.get(cuadro);
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int i = 0; i < pixeles.length; i++) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int argb = pixeles[i], posicion = i * 4;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            rgba[posicion] = (byte) (argb >>> 16);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            rgba[posicion + 1] = (byte) (argb >>> 8);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            rgba[posicion + 2] = (byte) argb;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            rgba[posicion + 3] = (byte) (argb >>> 24);
        }
        // realiza esta operacion auxiliar para completar el paso actual
        pixmap.getPixels().clear();
        // realiza esta operacion auxiliar para completar el paso actual
        pixmap.getPixels().put(rgba);
        // realiza esta operacion auxiliar para completar el paso actual
        pixmap.getPixels().position(0);
        // realiza esta operacion auxiliar para completar el paso actual
        textura.draw(pixmap, 0, 0);
    }

    // este metodo avanza la simulacion segun el tiempo transcurrido
    public void actualizar(float delta) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        revision += delta;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (revision >= 30) {
            // actualiza revision con el valor que necesita el estado actual
            revision = 0;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int hora = java.time.LocalTime.now().getHour();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (esDia != (hora >= 7 && hora < 19)) {
                // realiza esta operacion auxiliar para completar el paso actual
                cambiarFondo(hora);
            }
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (cuadros.isEmpty()) {
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        tiempo += Math.min(delta, 0.25f);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int anterior = cuadro;
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        while (tiempo >= duraciones.get(cuadro)) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            tiempo -= duraciones.get(cuadro);
            // actualiza cuadro con el valor que necesita el estado actual
            cuadro = (cuadro + 1) % cuadros.size();
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (anterior != cuadro) {
            // realiza esta operacion auxiliar para completar el paso actual
            subir();
        }
    }

    // este metodo dibujar concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dibujar(SpriteBatch batch, float x, float y, float ancho, float alto) {
        // realiza esta operacion auxiliar para completar el paso actual
        batch.draw(textura, x, y, ancho, alto);
    }

    @Override
    // este metodo dispose concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dispose() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (textura != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            textura.dispose();
            // actualiza textura con el valor que necesita el estado actual
            textura = null;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (pixmap != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            pixmap.dispose();
            // actualiza pixmap con el valor que necesita el estado actual
            pixmap = null;
        }
    }
}
