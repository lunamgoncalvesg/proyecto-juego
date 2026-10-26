package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.proyecto.juego.personajes.TipoPersonaje;
import com.proyecto.juego.visual.Apariencia;
import com.proyecto.juego.visual.Tema;

public class PantallaPreparacion extends PantallaBase {
    private final boolean enRed;
    private final Apariencia[] personajes = new Apariencia[3];
    private int indicePersonaje1, indicePersonaje2 = 1, opcionConexion, jugadorQueElige = 1;
    private boolean configurandoConexion;
    private String ip = "", error = "";
    private final String[] nombres = {"EQUILIBRADO", "RAPIDO", "FUERTE"};
    private final String[] descripciones = {"Balanceado. Recargas: 3 / 15 s", "Mas rapido. Recargas: 2 / 10 s", "Mas fuerte. Recargas: 4 / 20 s"};
    private final String misIps;

    public PantallaPreparacion(Main juego, boolean enRed) {
        super(juego);
        this.enRed = enRed;
        configurandoConexion = enRed;
        misIps = obtenerIps();
        for (int i = 0; i < 3; i++) {
            personajes[i] = new Apariencia(TipoPersonaje.values()[i]);
        }
    }

    private String obtenerIps() {
        java.util.List<String> direcciones = new java.util.ArrayList<String>();
        try {
            java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface interfaz = interfaces.nextElement();
                if (!interfaz.isUp() || interfaz.isLoopback() || interfaz.isVirtual()) {
                    continue;
                }
                java.util.Enumeration<java.net.InetAddress> ips = interfaz.getInetAddresses();
                while (ips.hasMoreElements()) {
                    java.net.InetAddress direccion = ips.nextElement();
                    if (direccion instanceof java.net.Inet4Address && !direccion.isLinkLocalAddress()) {
                        direcciones.add(direccion.getHostAddress());
                    }
                }
            }
        } catch (java.net.SocketException ignored) { }
        return direcciones.isEmpty() ? "Consulta tu IPv4 con ipconfig" : String.join(" / ", direcciones);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char c) {
                if (configurandoConexion && opcionConexion == 1) {
                    if (c == '\b' && !ip.isEmpty()) {
                        ip = ip.substring(0, ip.length() - 1);
                    } else if ((c >= '0' && c <= '9' || c == '.') && ip.length() < 15) {
                        ip += c;
                    }
                    error = "";
                }
                return true;
            }

            @Override
            public boolean keyDown(int tecla) {
                if (configurandoConexion && opcionConexion == 1 && tecla == Input.Keys.V &&
                    (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT))) {
                    String texto = Gdx.app.getClipboard().getContents();
                    if (texto != null) {
                        ip = texto.trim().replaceAll("[^0-9.]", "");
                    }
                    if (ip.length() > 15) {
                        ip = ip.substring(0, 15);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        if (tecla(Input.Keys.ESCAPE)) {
            if (!enRed && jugadorQueElige == 2) {
                jugadorQueElige = 1;
            } else if (enRed && !configurandoConexion) {
                configurandoConexion = true;
            } else {
                juego.setScreen(new PantallaSeleccionModo(juego));
                return;
            }
        }
        if (configurandoConexion) {
            dibujarConexion(delta);
            return;
        }
        int elegido = jugadorQueElige == 1 ? indicePersonaje1 : indicePersonaje2;
        if (seleccionHaciaIzquierda()) {
            elegido = (elegido + 2) % 3;
        }
        if (seleccionHaciaDerecha()) {
            elegido = (elegido + 1) % 3;
        }
        if (jugadorQueElige == 1) {
            indicePersonaje1 = elegido;
        } else {
            indicePersonaje2 = elegido;
        }
        if (tecla(Input.Keys.ENTER)) {
            confirmarPersonaje();
            return;
        }
        fondo(delta, "", enRed ? "ELEGI TU PERSONAJE" : "JUGADOR " + jugadorQueElige + ": ELEGI TU PERSONAJE");
        for (int i = 0; i < 3; i++) {
            float x = 64 + i * 391;
            panel(x, 237, 370, 330, elegido == i ? Tema.AMARILLO : Tema.BORDE);
            batch.begin();
            personajes[i].retrato(batch, x + 55, 332, 260, 210);
            batch.end();
            centrado(nombres[i], x + 185, 309, 1.9f, Tema.CREMA);
            centrado(descripciones[i], x + 185, 271, 1.1f, Tema.GRIS);
            if (clic(x, 237, 370, 330)) {
                if (jugadorQueElige == 1) {
                    indicePersonaje1 = i;
                } else {
                    indicePersonaje2 = i;
                }
            }
        }
        centrado(indicacionSeleccion(), 640, 199, 1.4f, Tema.CREMA);
        if (boton("CONTINUAR", 440, 91, 400, 70, true)) {
            confirmarPersonaje();
            return;
        }
        centrado("Hace clic en un personaje. ENTER para continuar. ESC para volver.", 640, 40, 1.2f, Tema.CREMA);
    }

    private boolean seleccionHaciaIzquierda() {
        if (jugadorQueElige == 2) {
            return tecla(Input.Keys.LEFT);
        }
        if (enRed) {
            return tecla(Input.Keys.A, Input.Keys.LEFT);
        }
        return tecla(Input.Keys.A);
    }

    private boolean seleccionHaciaDerecha() {
        if (jugadorQueElige == 2) {
            return tecla(Input.Keys.RIGHT);
        }
        if (enRed) {
            return tecla(Input.Keys.D, Input.Keys.RIGHT);
        }
        return tecla(Input.Keys.D);
    }

    private String indicacionSeleccion() {
        if (enRed) {
            if (opcionConexion == 0) {
                return "Sos el jugador 1, a la izquierda.";
            }
            return "Sos el jugador 2, a la derecha.";
        }
        if (jugadorQueElige == 1) {
            return "Jugador 1 usa A/D. Despues elige el jugador 2.";
        }
        return "Jugador 2 usa las flechas.";
    }

    private void dibujarConexion(float delta) {
        if (tecla(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.W, Input.Keys.S)) {
            opcionConexion = 1 - opcionConexion;
            error = "";
        }
        if (tecla(Input.Keys.ENTER)) {
            confirmarConexion();
            if (!configurandoConexion) {
                return;
            }
        }
        fondo(delta, "", "JUGAR EN RED");
        if (boton("CREAR UNA PARTIDA", 270, 452, 740, 75, opcionConexion == 0)) {
            opcionConexion = 0;
            error = "";
        }
        if (boton("UNIRME A UNA PARTIDA", 270, 352, 740, 75, opcionConexion == 1)) {
            opcionConexion = 1;
            error = "";
        }
        if (opcionConexion == 0) {
            centrado("La otra persona elige UNIRME y escribe tu IP:", 640, 300, 1.35f, Tema.CREMA);
            centrado(misIps, 640, 259, 1.7f, Tema.AMARILLO);
        } else {
            centrado("Escribi la IP de la computadora que creo la partida:", 640, 302, 1.35f, Tema.CREMA);
            panel(390, 230, 500, 51, Tema.AMARILLO);
            centrado(ip.isEmpty() ? "Ejemplo: 192.168.1.10" : ip + ((int) (reloj * 2) % 2 == 0 ? "_" : ""), 640, 267, 1.6f, ip.isEmpty() ? Tema.GRIS : Tema.CREMA);
        }
        centrado(error.isEmpty() ? "Las dos computadoras deben estar en la misma red." : error, 640, 198, 1.2f, error.isEmpty() ? Tema.GRIS : Tema.ROJO);
        if (boton("CONTINUAR", 440, 91, 400, 70, true)) {
            confirmarConexion();
            return;
        }
        centrado("Hace clic para elegir. ENTER para continuar. ESC para volver.", 640, 40, 1.2f, Tema.CREMA);
    }

    private void confirmarConexion() {
        if (opcionConexion == 1) {
            String[] partes = ip.split("\\.", -1);
            if (partes.length != 4) {
                error = "Escribi una IP valida, por ejemplo 192.168.1.10";
                return;
            }
            for (String parte : partes) {
                if (!parte.matches("[0-9]{1,3}") || Integer.parseInt(parte) > 255) {
                    error = "La IP no es valida. Revisala.";
                    return;
                }
            }
        }
        configurandoConexion = false;
    }

    private void confirmarPersonaje() {
        if (!enRed && jugadorQueElige == 1) {
            jugadorQueElige = 2;
            return;
        }
        juego.setScreen(new PantallaInstrucciones(juego, TipoPersonaje.values()[indicePersonaje1],
            TipoPersonaje.values()[indicePersonaje2], enRed, opcionConexion == 0, ip));
    }

    @Override
    public void dispose() {
        for (Apariencia personaje : personajes) {
            personaje.dispose();
        }
        super.dispose();
    }
}
