package com.proyecto.juego.red;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// esta clase agrupa la logica y los datos principales de protocolored
public final class ProtocoloRed {
    // este campo representa version y conserva ese dato entre llamadas
    public static final int VERSION = 2;
    // este campo representa tamano maximo y conserva ese dato entre llamadas
    public static final int TAMANO_MAXIMO = 1024;

    // este campo representa hola y conserva ese dato entre llamadas
    public static final int HOLA = 1;
    // este campo representa asignar jugador y conserva ese dato entre llamadas
    public static final int ASIGNAR_JUGADOR = 2;
    // este campo representa estado espera y conserva ese dato entre llamadas
    public static final int ESTADO_ESPERA = 3;
    // este campo representa seleccion y conserva ese dato entre llamadas
    public static final int SELECCION = 4;
    // este campo representa entrada y conserva ese dato entre llamadas
    public static final int ENTRADA = 5;
    // este campo representa estado combate y conserva ese dato entre llamadas
    public static final int ESTADO_COMBATE = 6;
    // este campo representa salir y conserva ese dato entre llamadas
    public static final int SALIR = 7;
    // este campo representa error y conserva ese dato entre llamadas
    public static final int ERROR = 8;
    // este campo representa ping y conserva ese dato entre llamadas
    public static final int PING = 9;
    // este campo representa desconexion y conserva ese dato entre llamadas
    public static final int DESCONEXION = 10;
    // este campo representa jugar y conserva ese dato entre llamadas
    public static final int JUGAR = 11;

    // este metodo protocolo red concentra una operacion de esta clase y recibe el control desde quien lo llama
    private ProtocoloRed() {
    }

    // este metodo leer concentra una operacion de esta clase y recibe el control desde quien lo llama
    public static Mensaje leer(DataInputStream entrada) throws IOException {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int longitud = entrada.readInt();
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (longitud < 1 || longitud > TAMANO_MAXIMO) {
            // detiene este flujo e informa que los datos o la operacion no son validos
            throw new IOException("Longitud de mensaje invalida: " + longitud);
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int tipo = entrada.readUnsignedByte();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        byte[] contenido = new byte[longitud - 1];
        // realiza esta operacion auxiliar para completar el paso actual
        entrada.readFully(contenido);
        // devuelve este resultado a quien llamo al metodo
        return new Mensaje(tipo, contenido);
    }

    // este metodo arma el mensaje de red con longitud, tipo y contenido
    public static byte[] codificar(int tipo, byte[] contenido) throws IOException {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tipo < 0 || tipo > 255 || contenido == null || contenido.length + 1 > TAMANO_MAXIMO) {
            // detiene este flujo e informa que los datos o la operacion no son validos
            throw new IOException("Mensaje invalido");
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(contenido.length + 5);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        DataOutputStream datos = new DataOutputStream(bytes);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeInt(contenido.length + 1);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeByte(tipo);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.write(contenido);
        // devuelve este resultado a quien llamo al metodo
        return bytes.toByteArray();
    }

    // este metodo contenido concentra una operacion de esta clase y recibe el control desde quien lo llama
    public static byte[] contenido(Vaciador vaciador) throws IOException {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        DataOutputStream datos = new DataOutputStream(bytes);
        // realiza esta operacion auxiliar para completar el paso actual
        vaciador.escribir(datos);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.flush();
        // devuelve este resultado a quien llamo al metodo
        return bytes.toByteArray();
    }

    // este metodo datos concentra una operacion de esta clase y recibe el control desde quien lo llama
    public static DataInputStream datos(Mensaje mensaje) {
        // devuelve este resultado a quien llamo al metodo
        return new DataInputStream(new ByteArrayInputStream(mensaje.contenido));
    }

    // esta clase agrupa la logica y los datos principales de mensaje
    public static final class Mensaje {
        // este campo representa tipo y conserva ese dato entre llamadas
        private final int tipo;
        // este campo representa contenido y conserva ese dato entre llamadas
        private final byte[] contenido;

        // este metodo mensaje concentra una operacion de esta clase y recibe el control desde quien lo llama
        private Mensaje(int tipo, byte[] contenido) {
            // actualiza this.tipo con el valor que necesita el estado actual
            this.tipo = tipo;
            // actualiza this.contenido con el valor que necesita el estado actual
            this.contenido = contenido;
        }

        // este metodo get tipo concentra una operacion de esta clase y recibe el control desde quien lo llama
        public int getTipo() {
            // devuelve este resultado a quien llamo al metodo
            return tipo;
        }

        // este metodo get contenido concentra una operacion de esta clase y recibe el control desde quien lo llama
        public byte[] getContenido() {
            // devuelve este resultado a quien llamo al metodo
            return contenido;
        }
    }

    // esta clase agrupa la logica y los datos principales de vaciador
    public interface Vaciador {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        void escribir(DataOutputStream datos) throws IOException;
    }
}
