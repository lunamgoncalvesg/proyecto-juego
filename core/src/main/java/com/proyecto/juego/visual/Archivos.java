package com.proyecto.juego.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;

// esta clase agrupa la logica y los datos principales de archivos
public final class Archivos {
    // este campo representa directorio y conserva ese dato entre llamadas
    private static File directorio;

    // este campo conserva un dato que esta clase necesita entre sus operaciones
    static {
        // agrupa una operacion que puede fallar para poder manejar su error
        try {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            File origen = new File(com.proyecto.juego.Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (origen.isFile()) {
                // actualiza directorio con el valor que necesita el estado actual
                directorio = origen.getParentFile();
            }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (Exception ignored) {
            // actualiza directorio con el valor que necesita el estado actual
            directorio = null;
        }
    }

    // este metodo archivos concentra una operacion de esta clase y recibe el control desde quien lo llama
    private Archivos() {
    }

    // este metodo recurso concentra una operacion de esta clase y recibe el control desde quien lo llama
    public static FileHandle recurso(String ruta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (directorio != null) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            FileHandle externo = Gdx.files.absolute(new File(directorio, "assets/" + ruta).getAbsolutePath());
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (externo.exists()) {
                // devuelve este resultado a quien llamo al metodo
                return externo;
            }
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        FileHandle local = Gdx.files.local("assets/" + ruta);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (local.exists()) {
            // devuelve este resultado a quien llamo al metodo
            return local;
        }
        // devuelve este resultado a quien llamo al metodo
        return Gdx.files.internal(ruta);
    }
}
