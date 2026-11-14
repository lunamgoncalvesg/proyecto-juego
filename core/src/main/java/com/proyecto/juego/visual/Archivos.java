package com.proyecto.juego.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;

public final class Archivos {
    private static File directorio;

    static {
        try {
            File origen = new File(com.proyecto.juego.Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (origen.isFile()) {
                directorio = origen.getParentFile();
            }
        } catch (Exception ignored) {
            directorio = null;
        }
    }

    private Archivos() {
    }

    public static FileHandle recurso(String ruta) {
        if (directorio != null) {
            FileHandle externo = Gdx.files.absolute(new File(directorio, "assets/" + ruta).getAbsolutePath());
            if (externo.exists()) {
                return externo;
            }
        }
        FileHandle local = Gdx.files.local("assets/" + ruta);
        if (local.exists()) {
            return local;
        }
        return Gdx.files.internal(ruta);
    }
}
