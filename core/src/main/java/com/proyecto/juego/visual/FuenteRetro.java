package com.proyecto.juego.visual;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public final class FuenteRetro implements Disposable {
    private static final int ANCHO_CELDA = 6;
    private static final int ANCHO_GLYPH = 5;
    private static final Pattern MARCAS_DIACRITICAS = Pattern.compile("\\p{M}+");
    private static final String[] CARACTERES = {
        "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
        "Q","R","S","T","U","V","W","X","Y","Z",
        "0","1","2","3","4","5","6","7","8","9",
        "!","?",".",",",":",";","-","+","/","=","(",")","'","\"","<",">","_","*","%"," "
    };
    private static final String[] PIXELES = {
        "01110/10001/10001/11111/10001/10001/10001",
        "11110/10001/10001/11110/10001/10001/11110",
        "01111/10000/10000/10000/10000/10000/01111",
        "11110/10001/10001/10001/10001/10001/11110",
        "11111/10000/10000/11110/10000/10000/11111",
        "11111/10000/10000/11110/10000/10000/10000",
        "01111/10000/10000/10111/10001/10001/01111",
        "10001/10001/10001/11111/10001/10001/10001",
        "11111/00100/00100/00100/00100/00100/11111",
        "00111/00010/00010/00010/10010/10010/01100",
        "10001/10010/10100/11000/10100/10010/10001",
        "10000/10000/10000/10000/10000/10000/11111",
        "10001/11011/10101/10101/10001/10001/10001",
        "10001/11001/10101/10011/10001/10001/10001",
        "01110/10001/10001/10001/10001/10001/01110",
        "11110/10001/10001/11110/10000/10000/10000",
        "01110/10001/10001/10001/10101/10010/01101",
        "11110/10001/10001/11110/10100/10010/10001",
        "01111/10000/10000/01110/00001/00001/11110",
        "11111/00100/00100/00100/00100/00100/00100",
        "10001/10001/10001/10001/10001/10001/01110",
        "10001/10001/10001/10001/10001/01010/00100",
        "10001/10001/10001/10101/10101/10101/01010",
        "10001/10001/01010/00100/01010/10001/10001",
        "10001/10001/01010/00100/00100/00100/00100",
        "11111/00001/00010/00100/01000/10000/11111",
        "01110/10001/10011/10101/11001/10001/01110",
        "00100/01100/00100/00100/00100/00100/01110",
        "01110/10001/00001/00010/00100/01000/11111",
        "11110/00001/00001/01110/00001/00001/11110",
        "00010/00110/01010/10010/11111/00010/00010",
        "11111/10000/10000/11110/00001/00001/11110",
        "01110/10000/10000/11110/10001/10001/01110",
        "11111/00001/00010/00100/01000/01000/01000",
        "01110/10001/10001/01110/10001/10001/01110",
        "01110/10001/10001/01111/00001/00001/01110",
        "00100/01110/01110/01110/00100/00000/00100",
        "01110/10001/00001/00010/00100/00000/00100",
        "00000/00000/00000/00000/00000/00110/00110",
        "00000/00000/00000/00000/00110/00110/00100",
        "00000/00110/00110/00000/00110/00110/00000",
        "00000/00110/00110/00000/00110/00110/00100",
        "00000/00100/00100/00000/00100/00100/00000",
        "00100/00010/00001/00010/00100/00000/00100",
        "00000/00000/00000/11111/00000/00000/00000",
        "00000/00100/00100/11111/00100/00100/00000",
        "00001/00010/00010/00100/01000/01000/10000",
        "00000/00000/11111/00000/11111/00000/00000",
        "00010/00100/01000/01000/01000/00100/00010",
        "01000/00100/00010/00010/00010/00100/01000",
        "00100/00100/00100/00100/00100/00000/00100",
        "01010/01010/11111/01010/11111/01010/01010",
        "00000/00000/01010/00100/01010/00000/00000",
        "11001/11010/00100/01000/10110/00110/00000",
        "00000/00000/00000/00000/00000/00000/00000",
        "00000/00000/00000/00000/00000/00000/00000"
    };

    private final Map<Character, TextureRegion> letras = new HashMap<Character, TextureRegion>();
    private final Texture textura;

    public FuenteRetro() {
        Pixmap pixmap = new Pixmap(CARACTERES.length * ANCHO_CELDA, 7, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(1, 1, 1, 1);
        for (int i = 0; i < CARACTERES.length; i++) {
            String[] filasGlyph = PIXELES[i].split("/");
            for (int y = 0; y < filasGlyph.length; y++) {
                for (int x = 0; x < filasGlyph[y].length(); x++) {
                    if (filasGlyph[y].charAt(x) == '1') {
                        pixmap.drawPixel(i * ANCHO_CELDA + x, y);
                    }
                }
            }
        }
        textura = new Texture(pixmap);
        textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        for (int i = 0; i < CARACTERES.length; i++) {
            letras.put(CARACTERES[i].charAt(0), new TextureRegion(textura, i * ANCHO_CELDA, 0, ANCHO_GLYPH, 7));
        }
        pixmap.dispose();
    }

    public float ancho(String texto, float escala) {
        return normalizar(texto).length() * ANCHO_CELDA * escala * 1.8f;
    }

    public void dibujar(SpriteBatch batch, String texto, float x, float y, float escala, com.badlogic.gdx.graphics.Color color) {
        float pixel = escala * 1.8f;
        float cursor = x;
        String normalizado = normalizar(texto);
        batch.setColor(color);
        for (int i = 0; i < normalizado.length(); i++) {
            char caracter = normalizado.charAt(i);
            TextureRegion letra = letras.get(caracter);
            if (letra == null) {
                letra = letras.get('?');
            }
            batch.draw(letra, cursor, y, ANCHO_GLYPH * pixel, 7 * pixel);
            cursor += ANCHO_CELDA * pixel;
        }
        batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
    }

    private String normalizar(String texto) {
        String descompuesto = Normalizer.normalize(texto.toUpperCase(Locale.ROOT), Normalizer.Form.NFD);
        String plano = MARCAS_DIACRITICAS.matcher(descompuesto).replaceAll("");
        return plano.replace('¿', '?').replace('¡', '!').replace('×', 'X').replace('·', '/');
    }

    @Override
    public void dispose() {
        textura.dispose();
        letras.clear();
    }
}
