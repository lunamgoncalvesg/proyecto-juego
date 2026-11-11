package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.proyecto.juego.personajes.TipoPersonaje;
import com.proyecto.juego.red.SesionRed;
import com.proyecto.juego.visual.Apariencia;
import com.proyecto.juego.visual.Tema;

public class PantallaPreparacion extends PantallaBase {
    private final boolean enRed;
    private final Apariencia[] personajes = new Apariencia[3];
    private int indicePersonaje1, indicePersonaje2 = 1, jugadorQueElige = 1;
    private boolean configurandoConexion;
    private String ip = "181.46.47.81", error = "";
    private SesionRed red;
    private final String[] nombres = {
        TipoPersonaje.EQUILIBRADO.getNombreVisible(), TipoPersonaje.RAPIDO.getNombreVisible(),
        TipoPersonaje.FUERTE.getNombreVisible()
    };
    private final String[] descripciones = {
        "EQUILIBRADO",
        "RÁPIDO, PERO DÉBIL",
        "FUERTE, PERO LENTO"
    };

    public PantallaPreparacion(Main juego, boolean enRed) {
        super(juego);
        this.enRed = enRed;
        configurandoConexion = enRed;
        for (int i = 0; i < 3; i++) {
            personajes[i] = new Apariencia(TipoPersonaje.values()[i]);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char c) {
                if (configurandoConexion && enRed) {
                    if (c == '\b' && !ip.isEmpty()) {
                        ip = ip.substring(0, ip.length() - 1);
                    } else if ((c >= '0' && c <= '9' || c >= 'a' && c <= 'z' ||
                        c >= 'A' && c <= 'Z' || c == '.' || c == '-') && ip.length() < 253) {
                        ip += c;
                    }
                    error = "";
                }
                return true;
            }

            @Override
            public boolean keyDown(int tecla) {
                if (configurandoConexion && enRed && tecla == Input.Keys.V &&
                    (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT))) {
                    String texto = Gdx.app.getClipboard().getContents();
                    if (texto != null) {
                        ip = texto.trim().replaceAll("[^A-Za-z0-9.-]", "");
                    }
                    if (ip.length() > 253) {
                        ip = ip.substring(0, 253);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        if (enRed && red != null && red.haTerminado() && !configurandoConexion) {
            error = red.getEstado();
            red = null;
            configurandoConexion = true;
        }
        if (tecla(Input.Keys.ESCAPE)) {
            if (!enRed && jugadorQueElige == 2) {
                jugadorQueElige = 1;
            } else {
                if (red != null) red.close();
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
        fondo(delta);
        for (int i = 0; i < 3; i++) {
            float x = 64 + i * 391;
            panel(x, 237, 370, 330, elegido == i ? Tema.AMARILLO : Tema.BORDE);
            batch.begin();
            personajes[i].retrato(batch, x + 55, 332, 260, 210);
            batch.end();
            centrado(nombres[i], x + 185, 298, 1.75f, Tema.CREMA);
            centrado(descripciones[i], x + 185, 255, 0.75f, Tema.GRIS);
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
            return red != null && red.getPuesto() == 2
                ? "Sos el jugador 2, a la derecha." : "Sos el jugador 1, a la izquierda.";
        }
        if (jugadorQueElige == 1) {
            return "Jugador 1";
        }
        return "Jugador 2";
    }

    private void dibujarConexion(float delta) {
        fondo(delta);
        if (tecla(Input.Keys.ENTER)) {
            confirmarConexion();
            if (!configurandoConexion) {
                return;
            }
        }
        panel(300, 296, 680, 55, Tema.AMARILLO);
        String visible = ip.length() > 52 ? "..." + ip.substring(ip.length() - 52) : ip;
        float escalaDireccion = visible.length() > 40 ? 0.95f : 1.25f;
        centrado(visible.isEmpty() ? "IP del servidor" : visible + ((int) (reloj * 2) % 2 == 0 ? "_" : ""),
            640, 314, escalaDireccion, ip.isEmpty() ? Tema.GRIS : Tema.CREMA);
        centrado(error.isEmpty() ? "" : error, 640, 174, 1.05f, Tema.AMARILLO);
        boolean rivalConectado = red != null && red.getEstado().startsWith("Rival conectado");
        String textoBoton = red != null && red.getPuesto() > 0
            ? rivalConectado ? "CONTINUAR" : "ESPERANDO RIVAL" : "CONECTAR";
        boolean habilitado = red == null || rivalConectado || red.haTerminado();
        if (boton(textoBoton, 440, 91, 400, 70, habilitado)) {
            confirmarConexion();
            return;
        }
    }

    private void confirmarConexion() {
        if (red != null && red.getPuesto() > 0) {
            if (red.getEstado().startsWith("Rival conectado")) {
                configurandoConexion = false;
            }
            return;
        }
        if (red != null && !red.haTerminado()) {
            return;
        }
        if (red != null && red.haTerminado()) {
            red = null;
        }
        String destino = ip.trim();

        ip = destino;
        error = "";
        red = new SesionRed(ip);
    }

    private boolean esDestinoValido(String destino) {
        if (destino.isEmpty() || destino.length() > 253 || destino.startsWith(".") || destino.endsWith(".")) {
            return false;
        }
        String[] partes = destino.split("\\.", -1);
        boolean ipv4 = partes.length == 4;
        for (String parte : partes) {
            if (parte.isEmpty() || parte.length() > 63 || !parte.matches("[A-Za-z0-9-]+") ||
                parte.startsWith("-") || parte.endsWith("-")) {
                return false;
            }
            if (!parte.matches("[0-9]{1,3}") || Integer.parseInt(parte) > 255) {
                ipv4 = false;
            }
        }
        return ipv4 || destino.matches("(?i).*[a-z].*");
    }

    private void confirmarPersonaje() {
        if (!enRed && jugadorQueElige == 1) {
            jugadorQueElige = 2;
            return;
        }
        if (enRed) {
            TipoPersonaje seleccionado = TipoPersonaje.values()[indicePersonaje1];
            red.enviarSeleccion(seleccionado, true);
            juego.setScreen(new PantallaInstrucciones(juego, seleccionado, red));
            return;
        }
        juego.setScreen(new PantallaInstrucciones(juego, TipoPersonaje.values()[indicePersonaje1],
            TipoPersonaje.values()[indicePersonaje2]));
    }

    @Override
    public void dispose() {
        for (Apariencia personaje : personajes) {
            personaje.dispose();
        }
        super.dispose();
    }
}
