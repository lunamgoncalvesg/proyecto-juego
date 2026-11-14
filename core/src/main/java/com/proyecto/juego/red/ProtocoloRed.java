package com.proyecto.juego.red;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ProtocoloRed {
    public static final int VERSION = 2;
    public static final int TAMANO_MAXIMO = 1024;

    public static final int HOLA = 1;
    public static final int ASIGNAR_JUGADOR = 2;
    public static final int ESTADO_ESPERA = 3;
    public static final int SELECCION = 4;
    public static final int ENTRADA = 5;
    public static final int ESTADO_COMBATE = 6;
    public static final int SALIR = 7;
    public static final int ERROR = 8;
    public static final int PING = 9;
    public static final int DESCONEXION = 10;
    public static final int JUGAR = 11;

    private ProtocoloRed() {
    }

    public static Mensaje leer(DataInputStream entrada) throws IOException {
        int longitud = entrada.readInt();
        if (longitud < 1 || longitud > TAMANO_MAXIMO) {
            throw new IOException("Longitud de mensaje invalida: " + longitud);
        }
        int tipo = entrada.readUnsignedByte();
        byte[] contenido = new byte[longitud - 1];
        entrada.readFully(contenido);
        return new Mensaje(tipo, contenido);
    }

    // La longitud de la trama cuenta el byte de tipo y el contenido.
    public static byte[] codificar(int tipo, byte[] contenido) throws IOException {
        if (tipo < 0 || tipo > 255 || contenido == null || contenido.length + 1 > TAMANO_MAXIMO) {
            throw new IOException("Mensaje invalido");
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(contenido.length + 5);
        DataOutputStream datos = new DataOutputStream(bytes);
        datos.writeInt(contenido.length + 1);
        datos.writeByte(tipo);
        datos.write(contenido);
        return bytes.toByteArray();
    }

    public static byte[] contenido(Vaciador vaciador) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream datos = new DataOutputStream(bytes);
        vaciador.escribir(datos);
        datos.flush();
        return bytes.toByteArray();
    }

    public static DataInputStream datos(Mensaje mensaje) {
        return new DataInputStream(new ByteArrayInputStream(mensaje.contenido));
    }

    public static final class Mensaje {
        private final int tipo;
        private final byte[] contenido;

        private Mensaje(int tipo, byte[] contenido) {
            this.tipo = tipo;
            this.contenido = contenido;
        }

        public int getTipo() {
            return tipo;
        }

        public byte[] getContenido() {
            return contenido;
        }
    }

    public interface Vaciador {
        void escribir(DataOutputStream datos) throws IOException;
    }
}
