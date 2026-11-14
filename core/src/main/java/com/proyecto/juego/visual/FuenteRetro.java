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

// esta clase agrupa la logica y los datos principales de fuenteretro
public final class FuenteRetro implements Disposable {
    // este campo representa ancho celda y conserva ese dato entre llamadas
    private static final int ANCHO_CELDA = 6;
    // este campo representa ancho glyph y conserva ese dato entre llamadas
    private static final int ANCHO_GLYPH = 5;
    // este metodo compile concentra una operacion de esta clase y recibe el control desde quien lo llama
    private static final Pattern MARCAS_DIACRITICAS = Pattern.compile("\\p{M}+");
    // este campo representa caracteres y conserva ese dato entre llamadas
    private static final String[] CARACTERES = {
        "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
        "Q","R","S","T","U","V","W","X","Y","Z",
        "0","1","2","3","4","5","6","7","8","9",
        "!","?",".",",",":",";","-","+","/","=","(",")","'","\"","<",">","_","*","%"," "
    };
    // este campo representa pixeles y conserva ese dato entre llamadas
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

    // este campo representa character y conserva ese dato entre llamadas
    private final Map<Character, TextureRegion> letras = new HashMap<Character, TextureRegion>();
    // este campo representa textura y conserva ese dato entre llamadas
    private final Texture textura;

    // este metodo fuente retro concentra una operacion de esta clase y recibe el control desde quien lo llama
    public FuenteRetro() {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Pixmap pixmap = new Pixmap(CARACTERES.length * ANCHO_CELDA, 7, Pixmap.Format.RGBA8888);
        // realiza esta operacion auxiliar para completar el paso actual
        pixmap.setBlending(Pixmap.Blending.None);
        // realiza esta operacion auxiliar para completar el paso actual
        pixmap.setColor(1, 1, 1, 1);
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int i = 0; i < CARACTERES.length; i++) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            String[] filasGlyph = PIXELES[i].split("/");
            // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
            for (int y = 0; y < filasGlyph.length; y++) {
                // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
                for (int x = 0; x < filasGlyph[y].length(); x++) {
                    // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                    if (filasGlyph[y].charAt(x) == '1') {
                        // realiza esta operacion auxiliar para completar el paso actual
                        pixmap.drawPixel(i * ANCHO_CELDA + x, y);
                    }
                }
            }
        }
        // actualiza textura con el valor que necesita el estado actual
        textura = new Texture(pixmap);
        // realiza esta operacion auxiliar para completar el paso actual
        textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int i = 0; i < CARACTERES.length; i++) {
            // realiza esta operacion auxiliar para completar el paso actual
            letras.put(CARACTERES[i].charAt(0), new TextureRegion(textura, i * ANCHO_CELDA, 0, ANCHO_GLYPH, 7));
        }
        // realiza esta operacion auxiliar para completar el paso actual
        pixmap.dispose();
    }

    // este metodo ancho concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float ancho(String texto, float escala) {
        // devuelve este resultado a quien llamo al metodo
        return normalizar(texto).length() * ANCHO_CELDA * escala * 1.8f;
    }

    // este metodo dibujar concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dibujar(SpriteBatch batch, String texto, float x, float y, float escala, com.badlogic.gdx.graphics.Color color) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float pixel = escala * 1.8f;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float cursor = x;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String normalizado = normalizar(texto);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.setColor(color);
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int i = 0; i < normalizado.length(); i++) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            char caracter = normalizado.charAt(i);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            TextureRegion letra = letras.get(caracter);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (letra == null) {
                // actualiza letra con el valor que necesita el estado actual
                letra = letras.get('?');
            }
            // realiza esta operacion auxiliar para completar el paso actual
            batch.draw(letra, cursor, y, ANCHO_GLYPH * pixel, 7 * pixel);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            cursor += ANCHO_CELDA * pixel;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
    }

    // este metodo normalizar concentra una operacion de esta clase y recibe el control desde quien lo llama
    private String normalizar(String texto) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String descompuesto = Normalizer.normalize(texto.toUpperCase(Locale.ROOT), Normalizer.Form.NFD);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String plano = MARCAS_DIACRITICAS.matcher(descompuesto).replaceAll("");
        // devuelve este resultado a quien llamo al metodo
        return plano.replace('¿', '?').replace('¡', '!').replace('×', 'X').replace('·', '/');
    }

    @Override
    // este metodo dispose concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dispose() {
        // realiza esta operacion auxiliar para completar el paso actual
        textura.dispose();
        // realiza esta operacion auxiliar para completar el paso actual
        letras.clear();
    }
}
