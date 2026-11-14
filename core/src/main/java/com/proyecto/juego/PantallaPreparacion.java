package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.proyecto.juego.personajes.TipoPersonaje;
import com.proyecto.juego.red.SesionRed;
import com.proyecto.juego.visual.Apariencia;
import com.proyecto.juego.visual.Tema;

// esta clase agrupa la logica y los datos principales de pantallapreparacion
public class PantallaPreparacion extends PantallaBase {
    // este campo representa enred y conserva ese dato entre llamadas
    private final boolean enRed;
    // este campo representa personajes y conserva ese dato entre llamadas
    private final Apariencia[] personajes = new Apariencia[3];
    // este campo representa indicepersonaje1 y conserva ese dato entre llamadas
    private int indicePersonaje1, indicePersonaje2 = 1, jugadorQueElige = 1;
    // este campo representa configurandoconexion y conserva ese dato entre llamadas
    private boolean configurandoConexion;
    // este campo representa ip y conserva ese dato entre llamadas
    private String ip = "181.46.47.81", error = "";
    // este campo representa red y conserva ese dato entre llamadas
    private SesionRed red;
    // este campo representa nombres y conserva ese dato entre llamadas
    private final String[] nombres = {
        TipoPersonaje.EQUILIBRADO.getNombreVisible(), TipoPersonaje.RAPIDO.getNombreVisible(),
        TipoPersonaje.FUERTE.getNombreVisible()
    };
    // este campo representa descripciones y conserva ese dato entre llamadas
    private final String[] descripciones = {
        "EQUILIBRADO",
        "RÁPIDO, PERO DÉBIL",
        "FUERTE, PERO LENTO"
    };

    // este metodo pantalla preparacion concentra una operacion de esta clase y recibe el control desde quien lo llama
    public PantallaPreparacion(Main juego, boolean enRed) {
        // realiza esta operacion auxiliar para completar el paso actual
        super(juego);
        // actualiza this.en red con el valor que necesita el estado actual
        this.enRed = enRed;
        // actualiza configurando conexion con el valor que necesita el estado actual
        configurandoConexion = enRed;
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int i = 0; i < 3; i++) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            personajes[i] = new Apariencia(TipoPersonaje.values()[i]);
        }
    }

    @Override
    // este metodo show concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void show() {
        // realiza esta operacion auxiliar para completar el paso actual
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
    // este metodo render concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void render(float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (enRed && red != null && red.haTerminado() && !configurandoConexion) {
            // actualiza error con el valor que necesita el estado actual
            error = red.getEstado();
            // actualiza red con el valor que necesita el estado actual
            red = null;
            // actualiza configurando conexion con el valor que necesita el estado actual
            configurandoConexion = true;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla(Input.Keys.ESCAPE)) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (!enRed && jugadorQueElige == 2) {
                // actualiza jugador que elige con el valor que necesita el estado actual
                jugadorQueElige = 1;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } else {
                // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                if (red != null) red.close();
                // realiza esta operacion auxiliar para completar el paso actual
                juego.setScreen(new PantallaSeleccionModo(juego));
                // devuelve este resultado a quien llamo al metodo
                return;
            }
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (configurandoConexion) {
            // realiza esta operacion auxiliar para completar el paso actual
            dibujarConexion(delta);
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int elegido = jugadorQueElige == 1 ? indicePersonaje1 : indicePersonaje2;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (seleccionHaciaIzquierda()) {
            // actualiza elegido con el valor que necesita el estado actual
            elegido = (elegido + 2) % 3;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (seleccionHaciaDerecha()) {
            // actualiza elegido con el valor que necesita el estado actual
            elegido = (elegido + 1) % 3;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (jugadorQueElige == 1) {
            // actualiza indice personaje1 con el valor que necesita el estado actual
            indicePersonaje1 = elegido;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else {
            // actualiza indice personaje2 con el valor que necesita el estado actual
            indicePersonaje2 = elegido;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla(Input.Keys.ENTER)) {
            // realiza esta operacion auxiliar para completar el paso actual
            confirmarPersonaje();
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        fondo(delta);
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int i = 0; i < 3; i++) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            float x = 64 + i * 391;
            // realiza esta operacion auxiliar para completar el paso actual
            panel(x, 237, 370, 330, elegido == i ? Tema.AMARILLO : Tema.BORDE);
            // realiza esta operacion auxiliar para completar el paso actual
            batch.begin();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            personajes[i].retrato(batch, x + 55, 332, 260, 210);
            // realiza esta operacion auxiliar para completar el paso actual
            batch.end();
            // realiza esta operacion auxiliar para completar el paso actual
            centrado(nombres[i], x + 185, 298, 1.75f, Tema.CREMA);
            // realiza esta operacion auxiliar para completar el paso actual
            centrado(descripciones[i], x + 185, 255, 0.75f, Tema.GRIS);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (clic(x, 237, 370, 330)) {
                // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                if (jugadorQueElige == 1) {
                    // actualiza indice personaje1 con el valor que necesita el estado actual
                    indicePersonaje1 = i;
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                } else {
                    // actualiza indice personaje2 con el valor que necesita el estado actual
                    indicePersonaje2 = i;
                }
            }
        }
        // realiza esta operacion auxiliar para completar el paso actual
        centrado(indicacionSeleccion(), 640, 199, 1.4f, Tema.CREMA);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (boton("CONTINUAR", 440, 91, 400, 70, true)) {
            // realiza esta operacion auxiliar para completar el paso actual
            confirmarPersonaje();
            // devuelve este resultado a quien llamo al metodo
            return;
        }
    }

    // este metodo seleccion hacia izquierda concentra una operacion de esta clase y recibe el control desde quien lo llama
    private boolean seleccionHaciaIzquierda() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (jugadorQueElige == 2) {
            // devuelve este resultado a quien llamo al metodo
            return tecla(Input.Keys.LEFT);
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (enRed) {
            // devuelve este resultado a quien llamo al metodo
            return tecla(Input.Keys.A, Input.Keys.LEFT);
        }
        // devuelve este resultado a quien llamo al metodo
        return tecla(Input.Keys.A);
    }

    // este metodo seleccion hacia derecha concentra una operacion de esta clase y recibe el control desde quien lo llama
    private boolean seleccionHaciaDerecha() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (jugadorQueElige == 2) {
            // devuelve este resultado a quien llamo al metodo
            return tecla(Input.Keys.RIGHT);
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (enRed) {
            // devuelve este resultado a quien llamo al metodo
            return tecla(Input.Keys.D, Input.Keys.RIGHT);
        }
        // devuelve este resultado a quien llamo al metodo
        return tecla(Input.Keys.D);
    }

    // este metodo indicacion seleccion concentra una operacion de esta clase y recibe el control desde quien lo llama
    private String indicacionSeleccion() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (enRed) {
            // devuelve este resultado a quien llamo al metodo
            return red != null && red.getPuesto() == 2
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                ? "Sos el jugador 2, a la derecha." : "Sos el jugador 1, a la izquierda.";
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (jugadorQueElige == 1) {
            // devuelve este resultado a quien llamo al metodo
            return "Jugador 1";
        }
        // devuelve este resultado a quien llamo al metodo
        return "Jugador 2";
    }

    // este metodo dibujar conexion concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void dibujarConexion(float delta) {
        // realiza esta operacion auxiliar para completar el paso actual
        fondo(delta);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla(Input.Keys.ENTER)) {
            // realiza esta operacion auxiliar para completar el paso actual
            confirmarConexion();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (!configurandoConexion) {
                // devuelve este resultado a quien llamo al metodo
                return;
            }
        }
        // realiza esta operacion auxiliar para completar el paso actual
        panel(300, 296, 680, 55, Tema.AMARILLO);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String visible = ip.length() > 52 ? "..." + ip.substring(ip.length() - 52) : ip;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float escalaDireccion = visible.length() > 40 ? 0.95f : 1.25f;
        // realiza esta operacion auxiliar para completar el paso actual
        centrado(visible.isEmpty() ? "IP del servidor" : visible + ((int) (reloj * 2) % 2 == 0 ? "_" : ""),
            640, 314, escalaDireccion, ip.isEmpty() ? Tema.GRIS : Tema.CREMA);
        // realiza esta operacion auxiliar para completar el paso actual
        centrado(error.isEmpty() ? "" : error, 640, 174, 1.05f, Tema.AMARILLO);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean rivalConectado = red != null && red.getEstado().startsWith("Rival conectado");
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String textoBoton = red != null && red.getPuesto() > 0
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            ? rivalConectado ? "CONTINUAR" : "ESPERANDO RIVAL" : "CONECTAR";
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean habilitado = red == null || rivalConectado || red.haTerminado();
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (boton(textoBoton, 440, 91, 400, 70, habilitado)) {
            // realiza esta operacion auxiliar para completar el paso actual
            confirmarConexion();
            // devuelve este resultado a quien llamo al metodo
            return;
        }
    }

    // este metodo inicia la conexion o continua cuando ambos jugadores estan listos
    private void confirmarConexion() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (red != null && red.getPuesto() > 0) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (red.getEstado().startsWith("Rival conectado")) {
                // actualiza configurando conexion con el valor que necesita el estado actual
                configurandoConexion = false;
            }
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (red != null && !red.haTerminado()) {
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (red != null && red.haTerminado()) {
            // actualiza red con el valor que necesita el estado actual
            red = null;
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String destino = ip.trim();

        // actualiza ip con el valor que necesita el estado actual
        ip = destino;
        // actualiza error con el valor que necesita el estado actual
        error = "";
        // actualiza red con el valor que necesita el estado actual
        red = new SesionRed(ip);
    }

    // este metodo es destino valido concentra una operacion de esta clase y recibe el control desde quien lo llama
    private boolean esDestinoValido(String destino) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (destino.isEmpty() || destino.length() > 253 || destino.startsWith(".") || destino.endsWith(".")) {
            // devuelve este resultado a quien llamo al metodo
            return false;
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String[] partes = destino.split("\\.", -1);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean ipv4 = partes.length == 4;
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (String parte : partes) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (parte.isEmpty() || parte.length() > 63 || !parte.matches("[A-Za-z0-9-]+") ||
                parte.startsWith("-") || parte.endsWith("-")) {
                // devuelve este resultado a quien llamo al metodo
                return false;
            }
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (!parte.matches("[0-9]{1,3}") || Integer.parseInt(parte) > 255) {
                // actualiza ipv4 con el valor que necesita el estado actual
                ipv4 = false;
            }
        }
        // devuelve este resultado a quien llamo al metodo
        return ipv4 || destino.matches("(?i).*[a-z].*");
    }

    // este metodo guarda la seleccion y abre las instrucciones del combate
    private void confirmarPersonaje() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (!enRed && jugadorQueElige == 1) {
            // actualiza jugador que elige con el valor que necesita el estado actual
            jugadorQueElige = 2;
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (enRed) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            TipoPersonaje seleccionado = TipoPersonaje.values()[indicePersonaje1];
            // realiza esta operacion auxiliar para completar el paso actual
            red.enviarSeleccion(seleccionado);
            // realiza esta operacion auxiliar para completar el paso actual
            juego.setScreen(new PantallaInstrucciones(juego, seleccionado, red));
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        juego.setScreen(new PantallaInstrucciones(juego, TipoPersonaje.values()[indicePersonaje1],
            TipoPersonaje.values()[indicePersonaje2]));
    }

    @Override
    // este metodo dispose concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dispose() {
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (Apariencia personaje : personajes) {
            // realiza esta operacion auxiliar para completar el paso actual
            personaje.dispose();
        }
        // realiza esta operacion auxiliar para completar el paso actual
        super.dispose();
    }
}
