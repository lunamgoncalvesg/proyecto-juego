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
    private final String[] nombres = {
        TipoPersonaje.EQUILIBRADO.getNombreVisible(), TipoPersonaje.RAPIDO.getNombreVisible(),
        TipoPersonaje.FUERTE.getNombreVisible()
    };
    private final String[] descripciones = {
        "VEL. 300 / DAÑO x1,0 / " + TipoPersonaje.EQUILIBRADO.getHabilidadEspecial(),
        "VEL. 400 / DAÑO x0,8 / " + TipoPersonaje.RAPIDO.getHabilidadEspecial(),
        "VEL. 220 / DAÑO x1,4 / " + TipoPersonaje.FUERTE.getHabilidadEspecial()
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
                if (configurandoConexion && opcionConexion == 1) {
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
                if (configurandoConexion && opcionConexion == 1 && tecla == Input.Keys.V &&
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
        fondo(delta, enRed ? "ELEGI TU PERSONAJE" : "JUGADOR " + jugadorQueElige + ": ELEGI TU PERSONAJE");
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
        fondo(delta, "JUGAR EN RED");
        if (boton("CREAR UNA PARTIDA", 270, 452, 740, 75, opcionConexion == 0)) {
            opcionConexion = 0;
            error = "";
        }
        if (boton("UNIRME A UNA PARTIDA", 270, 352, 740, 75, opcionConexion == 1)) {
            opcionConexion = 1;
            error = "";
        }
        if (opcionConexion == 0) {
            centrado("PARTIDA LISTA", 640, 309, 1.8f, Tema.AMARILLO);
            centrado("Compartí la dirección de esta partida con tu rival.", 640, 272, 1.15f, Tema.CREMA);
            centrado("Esperando rival...", 640, 229, 1.1f, Tema.GRIS);
        } else {
            centrado("ESCRIBI LA DIRECCION DE TU RIVAL:", 640, 314, 1.35f, Tema.CREMA);
            panel(300, 238, 680, 51, Tema.AMARILLO);
            String visible = ip.length() > 52 ? "..." + ip.substring(ip.length() - 52) : ip;
            float escalaDireccion = visible.length() > 40 ? 0.95f : 1.25f;
            centrado(visible.isEmpty() ? "IP o dominio" : visible + ((int) (reloj * 2) % 2 == 0 ? "_" : ""),
                640, 256, escalaDireccion, ip.isEmpty() ? Tema.GRIS : Tema.CREMA);
        }
        centrado(error.isEmpty() ? "" : error, 640, 174, 1.05f, Tema.ROJO);
        if (boton("CONTINUAR", 440, 91, 400, 70, true)) {
            confirmarConexion();
            return;
        }
        centrado("Hace clic para elegir. ENTER para continuar. ESC para volver.", 640, 40, 1.2f, Tema.CREMA);
    }

    private void confirmarConexion() {
        if (opcionConexion == 1) {
            String destino = ip.trim();
            if (!esDestinoValido(destino)) {
                error = "La dirección no es válida.";
                return;
            }
            ip = destino;
        }
        configurandoConexion = false;
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
