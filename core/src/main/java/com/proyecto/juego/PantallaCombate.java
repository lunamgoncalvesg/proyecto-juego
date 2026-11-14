package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.proyecto.juego.combate.Combate;
import com.proyecto.juego.combate.ControladorCombate;
import com.proyecto.juego.controles.Controles;
import com.proyecto.juego.personajes.EstadoPersonaje;
import com.proyecto.juego.personajes.Personaje;
import com.proyecto.juego.personajes.TipoPersonaje;
import com.proyecto.juego.red.SesionRed;
import com.proyecto.juego.visual.Apariencia;
import com.proyecto.juego.visual.Tema;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// esta clase agrupa la logica y los datos principales de pantallacombate
public class PantallaCombate extends PantallaBase {
    // este campo representa tipo1 y conserva ese dato entre llamadas
    private TipoPersonaje tipo1, tipo2;
    // este campo representa red y conserva ese dato entre llamadas
    private final SesionRed red;
    // este campo representa controles y conserva ese dato entre llamadas
    private final Controles controles;
    // este campo representa combate y conserva ese dato entre llamadas
    private Combate combate;
    // este campo representa controlador y conserva ese dato entre llamadas
    private ControladorCombate controlador;
    // este campo representa apariencia1 y conserva ese dato entre llamadas
    private Apariencia apariencia1, apariencia2;
    // este campo representa jugador1 y conserva ese dato entre llamadas
    private Personaje jugador1, jugador2;
    // este campo representa rivallisto y conserva ese dato entre llamadas
    private boolean rivalListo;
    // este campo representa resultadoronda y conserva ese dato entre llamadas
    private boolean resultadoRonda;
    // este campo representa finalizado y conserva ese dato entre llamadas
    private boolean finalizado;
    // este campo representa ganadorronda y conserva ese dato entre llamadas
    private int ganadorRonda;
    // este campo representa cuentaregresiva y conserva ese dato entre llamadas
    private float cuentaRegresiva = 3, esperaEntreRondas, tiempoDesdeElFinal;
    // este campo representa poses y conserva ese dato entre llamadas
    private final int[] poses = new int[2];
    // este campo representa tiempospose y conserva ese dato entre llamadas
    private final float[] tiemposPose = new float[2];
    // este campo representa caminando y conserva ese dato entre llamadas
    private final boolean[] caminando = new boolean[2];
    // este campo representa visualdesdex y conserva ese dato entre llamadas
    private final float[] visualDesdeX = new float[2], visualDesdeY = new float[2];
    // este campo representa visualhastax y conserva ese dato entre llamadas
    private final float[] visualHastaX = new float[2], visualHastaY = new float[2];
    // este campo representa progresovisual y conserva ese dato entre llamadas
    private float progresoVisual = 1;
    // este campo representa ultimoestado y conserva ese dato entre llamadas
    private byte[] ultimoEstado;
    // este campo representa mascaraanterior y conserva ese dato entre llamadas
    private int mascaraAnterior;
    // este campo representa tiempodesdeelenvio y conserva ese dato entre llamadas
    private float tiempoDesdeElEnvio;
    // este campo representa pulsacionespendientes y conserva ese dato entre llamadas
    private int pulsacionesPendientes;

    // este metodo pantalla combate concentra una operacion de esta clase y recibe el control desde quien lo llama
    public PantallaCombate(Main juego, TipoPersonaje tipo1, TipoPersonaje tipo2, SesionRed red) {
        // realiza esta operacion auxiliar para completar el paso actual
        super(juego);
        // actualiza this.red con el valor que necesita el estado actual
        this.red = red;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (red != null && red.getPuesto() == 2) {
            // actualiza this.tipo1 con el valor que necesita el estado actual
            this.tipo1 = TipoPersonaje.EQUILIBRADO;
            // actualiza this.tipo2 con el valor que necesita el estado actual
            this.tipo2 = tipo1;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else {
            // actualiza this.tipo1 con el valor que necesita el estado actual
            this.tipo1 = tipo1;
            // actualiza this.tipo2 con el valor que necesita el estado actual
            this.tipo2 = tipo2;
        }
        // actualiza controles con el valor que necesita el estado actual
        controles = new Controles(red != null);
        // actualiza rival listo con el valor que necesita el estado actual
        rivalListo = red == null;
        // realiza esta operacion auxiliar para completar el paso actual
        crearCombate();
    }

    // este metodo construye los personajes, sus apariencias y las reglas de la ronda
    private void crearCombate() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (apariencia1 != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            apariencia1.dispose();
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (apariencia2 != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            apariencia2.dispose();
        }
        // actualiza apariencia1 con el valor que necesita el estado actual
        apariencia1 = new Apariencia(tipo1);
        // actualiza apariencia2 con el valor que necesita el estado actual
        apariencia2 = new Apariencia(tipo2);
        // actualiza jugador1 con el valor que necesita el estado actual
        jugador1 = new Personaje("Jugador 1", tipo1);
        // actualiza jugador2 con el valor que necesita el estado actual
        jugador2 = new Personaje("Jugador 2", tipo2);
        // realiza esta operacion auxiliar para completar el paso actual
        jugador1.establecerLimites(20, 960);
        // realiza esta operacion auxiliar para completar el paso actual
        jugador2.establecerLimites(20, 960);
        // actualiza combate con el valor que necesita el estado actual
        combate = new Combate(jugador1, jugador2);
        // actualiza controlador con el valor que necesita el estado actual
        controlador = new ControladorCombate(combate);
        // realiza esta operacion auxiliar para completar el paso actual
        ubicarJugadores();
    }

    // este metodo coloca a los personajes y reinicia los datos visuales
    private void ubicarJugadores() {
        // realiza esta operacion auxiliar para completar el paso actual
        jugador1.establecerPosicion(70, 80);
        // realiza esta operacion auxiliar para completar el paso actual
        jugador2.establecerPosicion(910, 80);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        poses[0] = poses[1] = 0;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        tiemposPose[0] = tiemposPose[1] = 0;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        caminando[0] = caminando[1] = false;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        visualDesdeX[0] = visualHastaX[0] = jugador1.getX();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        visualDesdeX[1] = visualHastaX[1] = jugador2.getX();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        visualDesdeY[0] = visualHastaY[0] = jugador1.getY();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        visualDesdeY[1] = visualHastaY[1] = jugador2.getY();
        // actualiza progreso visual con el valor que necesita el estado actual
        progresoVisual = 1;
    }

    @Override
    // este metodo show concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void show() {
        // realiza esta operacion auxiliar para completar el paso actual
        Gdx.input.setInputProcessor(controles);
    }

    @Override
    // este metodo render concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void render(float delta) {
        // actualiza delta con el valor que necesita el estado actual
        delta = Math.min(delta, 0.05f);
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tecla(Input.Keys.ESCAPE)) {
            // realiza esta operacion auxiliar para completar el paso actual
            juego.setScreen(new PantallaInicio(juego));
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (red != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            actualizarRed(delta);
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (red != null && red.haTerminado() && !finalizado) {
            // realiza esta operacion auxiliar para completar el paso actual
            juego.setScreen(new PantallaInicio(juego, red.getEstado()));
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (red == null) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (rivalListo) actualizarCombate(delta);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else {
            // realiza esta operacion auxiliar para completar el paso actual
            recibirEstadoServidor();
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (finalizado) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            tiempoDesdeElFinal += delta;
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (tiempoDesdeElFinal >= 2.5f) {
                // realiza esta operacion auxiliar para completar el paso actual
                mostrarResultado();
                // devuelve este resultado a quien llamo al metodo
                return;
            }
        }
        // realiza esta operacion auxiliar para completar el paso actual
        actualizarInterpolacion(delta);
        // realiza esta operacion auxiliar para completar el paso actual
        dibujarCombate(delta);
    }

    // este metodo abre el resumen final con el ganador y las rondas
    private void mostrarResultado() {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int ganador = combate.getRondasJugador1() >= 2 ? 1 : 2;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int jugadorLocal = red == null ? 0 : red.getPuesto();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        TipoPersonaje personajeResultado = ganador == 1 ? tipo1 : tipo2;
        // realiza esta operacion auxiliar para completar el paso actual
        juego.setScreen(new PantallaResultado(juego, ganador, jugadorLocal, personajeResultado,
            combate.getRondasJugador1(), combate.getRondasJugador2()));
    }

    // este metodo actualizar combate concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void actualizarCombate(float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (finalizado) {
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (cuentaRegresiva > 0) {
            // actualiza cuenta regresiva con el valor que necesita el estado actual
            cuentaRegresiva = Math.max(0, cuentaRegresiva - delta);
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (resultadoRonda) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            esperaEntreRondas -= delta;
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (esperaEntreRondas <= 0) {
                // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                if (combate.combateTerminado()) {
                    // actualiza finalizado con el valor que necesita el estado actual
                    finalizado = true;
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                } else {
                    // realiza esta operacion auxiliar para completar el paso actual
                    combate.iniciarRonda();
                    // realiza esta operacion auxiliar para completar el paso actual
                    ubicarJugadores();
                    // actualiza resultado ronda con el valor que necesita el estado actual
                    resultadoRonda = false;
                    // actualiza cuenta regresiva con el valor que necesita el estado actual
                    cuentaRegresiva = 2;
                }
            }
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float x1 = jugador1.getX(), x2 = jugador2.getX();
        // realiza esta operacion auxiliar para completar el paso actual
        controlador.actualizar(delta, controles.mascaraJugador1(), controles.mascaraJugador2());
        // realiza esta operacion auxiliar para completar el paso actual
        controlador.resolverColision();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        caminando[0] = Math.abs(jugador1.getX() - x1) > 0.01f;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        caminando[1] = Math.abs(jugador2.getX() - x2) > 0.01f;
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int i = 0; i < 2; i++) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int ataque = i == 0 ? controlador.getAtaque1() : controlador.getAtaque2();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            tiemposPose[i] = Math.max(0, tiemposPose[i] - delta);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (ataque != 0) {
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                poses[i] = ataque;
                // comprueba esta condicion para decidir si ejecuta el bloque que sigue
                if (ataque == 3) {
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    tiemposPose[i] = 0.45f;
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                } else if (ataque == 2) {
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    tiemposPose[i] = 0.3f;
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                } else {
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    tiemposPose[i] = 0.18f;
                }
            }
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (combate.terminoRonda()) {
            // actualiza ganador ronda con el valor que necesita el estado actual
            ganadorRonda = combate.obtenerGanadorRonda();
            // realiza esta operacion auxiliar para completar el paso actual
            combate.registrarGanadorRonda();
            // actualiza resultado ronda con el valor que necesita el estado actual
            resultadoRonda = true;
            // actualiza espera entre rondas con el valor que necesita el estado actual
            esperaEntreRondas = 2;
        }
    }

    // este metodo envia al servidor las teclas sostenidas y las pulsaciones breves
    private void actualizarRed(float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (!red.conectado()) return;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        tiempoDesdeElEnvio += delta;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int mascara = controles.mascaraJugador1();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        pulsacionesPendientes |= controles.consumirPulsaciones();
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tiempoDesdeElEnvio >= 1f / 60f || mascara != mascaraAnterior || pulsacionesPendientes != 0) {
            // realiza esta operacion auxiliar para completar el paso actual
            red.enviarControles(mascara, pulsacionesPendientes);
            // actualiza tiempo desde el envio con el valor que necesita el estado actual
            tiempoDesdeElEnvio = 0;
            // actualiza pulsaciones pendientes con el valor que necesita el estado actual
            pulsacionesPendientes = 0;
        }
        // actualiza mascara anterior con el valor que necesita el estado actual
        mascaraAnterior = mascara;
    }

    // este metodo lee y valida el estado autoritativo que envio el servidor
    private void recibirEstadoServidor() {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        byte[] estado = red.ultimoEstado();
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (estado == null || estado == ultimoEstado) {
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        boolean habiaEstado = ultimoEstado != null;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float[] posicionAnteriorX = {posicionVisualX(0), posicionVisualX(1)};
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float[] posicionAnteriorY = {posicionVisualY(0), posicionVisualY(1)};
        // actualiza ultimo estado con el valor que necesita el estado actual
        ultimoEstado = estado;
        // agrupa una operacion que puede fallar para poder manejar su error
        try (DataInputStream datos = new DataInputStream(new ByteArrayInputStream(estado))) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int ordinalPrimero = datos.readUnsignedByte();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int ordinalSegundo = datos.readUnsignedByte();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (ordinalPrimero >= TipoPersonaje.values().length || ordinalSegundo >= TipoPersonaje.values().length) {
                // detiene este flujo e informa que los datos o la operacion no son validos
                throw new IOException("Personaje desconocido en el estado");
            }
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            TipoPersonaje primero = TipoPersonaje.values()[ordinalPrimero];
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            TipoPersonaje segundo = TipoPersonaje.values()[ordinalSegundo];
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int fase = datos.readUnsignedByte();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            float tiempoFase = datos.readFloat();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int ganador = datos.readInt();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            int[] posesRecibidas = {datos.readUnsignedByte(), datos.readUnsignedByte()};
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            float[] tiemposRecibidos = {datos.readFloat(), datos.readFloat()};
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            boolean[] caminandoRecibido = {datos.readBoolean(), datos.readBoolean()};
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (fase > 3 || !Float.isFinite(tiempoFase) || tiempoFase < 0 || tiempoFase > 60 ||
                ganador < 0 || ganador > 2 || posesRecibidas[0] > 3 || posesRecibidas[1] > 3 ||
                !Float.isFinite(tiemposRecibidos[0]) || !Float.isFinite(tiemposRecibidos[1]) ||
                tiemposRecibidos[0] < 0 || tiemposRecibidos[0] > 0.5f ||
                tiemposRecibidos[1] < 0 || tiemposRecibidos[1] > 0.5f) {
                // detiene este flujo e informa que los datos o la operacion no son validos
                throw new IOException("Estado de combate fuera de rango");
            }
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (primero != tipo1 || segundo != tipo2) {
                // actualiza tipo1 con el valor que necesita el estado actual
                tipo1 = primero;
                // actualiza tipo2 con el valor que necesita el estado actual
                tipo2 = segundo;
                // realiza esta operacion auxiliar para completar el paso actual
                crearCombate();
            }
            // realiza esta operacion auxiliar para completar el paso actual
            combate.leer(datos);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (datos.available() != 0) throw new IOException("Datos sobrantes en estado de combate");
            // realiza esta operacion auxiliar para completar el paso actual
            validarPersonaje(jugador1);
            // realiza esta operacion auxiliar para completar el paso actual
            validarPersonaje(jugador2);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (!Float.isFinite(combate.getTiempoRonda()) || combate.getTiempoRonda() < 0 || combate.getTiempoRonda() > 60 ||
                combate.getRondasJugador1() < 0 || combate.getRondasJugador1() > 2 ||
                combate.getRondasJugador2() < 0 || combate.getRondasJugador2() > 2 ||
                !Float.isFinite(combate.getRecargaBombaJugador1()) || combate.getRecargaBombaJugador1() < 0 || combate.getRecargaBombaJugador1() > 20 ||
                !Float.isFinite(combate.getRecargaBombaJugador2()) || combate.getRecargaBombaJugador2() < 0 || combate.getRecargaBombaJugador2() > 20 ||
                !Float.isFinite(combate.getRecargaEspecialJugador1()) || combate.getRecargaEspecialJugador1() < 0 || combate.getRecargaEspecialJugador1() > 20 ||
                !Float.isFinite(combate.getRecargaEspecialJugador2()) || combate.getRecargaEspecialJugador2() < 0 || combate.getRecargaEspecialJugador2() > 20) {
                // detiene este flujo e informa que los datos o la operacion no son validos
                throw new IOException("Valores de combate fuera de rango");
            }
            // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
            for (int i = 0; i < 2; i++) {
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                Personaje personaje = i == 0 ? jugador1 : jugador2;
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                visualDesdeX[i] = habiaEstado ? posicionAnteriorX[i] : personaje.getX();
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                visualDesdeY[i] = habiaEstado ? posicionAnteriorY[i] : personaje.getY();
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                visualHastaX[i] = personaje.getX();
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                visualHastaY[i] = personaje.getY();
            }
            // actualiza progreso visual con el valor que necesita el estado actual
            progresoVisual = habiaEstado ? 0 : 1;
            // actualiza rival listo con el valor que necesita el estado actual
            rivalListo = true;
            // actualiza cuenta regresiva con el valor que necesita el estado actual
            cuentaRegresiva = fase == 0 ? tiempoFase : 0;
            // actualiza resultado ronda con el valor que necesita el estado actual
            resultadoRonda = fase == 2;
            // actualiza finalizado con el valor que necesita el estado actual
            finalizado = fase == 3;
            // actualiza ganador ronda con el valor que necesita el estado actual
            ganadorRonda = ganador;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            poses[0] = posesRecibidas[0];
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            poses[1] = posesRecibidas[1];
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            tiemposPose[0] = tiemposRecibidos[0];
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            tiemposPose[1] = tiemposRecibidos[1];
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            caminando[0] = caminandoRecibido[0];
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            caminando[1] = caminandoRecibido[1];
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } catch (IOException e) {
            // realiza esta operacion auxiliar para completar el paso actual
            Gdx.app.error("Red", "Estado de combate invalido", e);
            // realiza esta operacion auxiliar para completar el paso actual
            red.close();
        }
    }

    // este metodo rechaza posiciones y recursos fuera de los limites esperados
    private void validarPersonaje(Personaje personaje) throws IOException {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (!Float.isFinite(personaje.getX()) || personaje.getX() < 0 || personaje.getX() > 1200 ||
            !Float.isFinite(personaje.getY()) || personaje.getY() < 80 || personaje.getY() > 400 ||
            !Float.isFinite(personaje.getVida()) || personaje.getVida() < 0 || personaje.getVida() > 100 ||
            !Float.isFinite(personaje.getEstamina()) || personaje.getEstamina() < 0 || personaje.getEstamina() > 100) {
            // detiene este flujo e informa que los datos o la operacion no son validos
            throw new IOException("Valores de personaje fuera de rango");
        }
    }

    // este metodo suaviza el movimiento entre dos estados recibidos por red
    private void actualizarInterpolacion(float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (red != null && progresoVisual < 1) {
            // actualiza progreso visual con el valor que necesita el estado actual
            progresoVisual = Math.min(1, progresoVisual + delta * 30f);
        }
    }

    // este metodo posicion visual x concentra una operacion de esta clase y recibe el control desde quien lo llama
    private float posicionVisualX(int indice) {
        // devuelve este resultado a quien llamo al metodo
        return visualDesdeX[indice] + (visualHastaX[indice] - visualDesdeX[indice]) * progresoVisual;
    }

    // este metodo posicion visual y concentra una operacion de esta clase y recibe el control desde quien lo llama
    private float posicionVisualY(int indice) {
        // devuelve este resultado a quien llamo al metodo
        return visualDesdeY[indice] + (visualHastaY[indice] - visualDesdeY[indice]) * progresoVisual;
    }

    // este metodo prioriza resultado, defensa, ataque, salto, caminata y reposo
    private String elegirPose(Personaje personaje, int indice) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (resultadoRonda || finalizado) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (ganadorRonda == 0) {
                // devuelve este resultado a quien llamo al metodo
                return "idle";
            }
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (ganadorRonda == indice + 1) {
                // devuelve este resultado a quien llamo al metodo
                return "win";
            }
            // devuelve este resultado a quien llamo al metodo
            return "lose";
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (personaje.getEstado() == EstadoPersonaje.BLOQUEANDO) {
            // devuelve este resultado a quien llamo al metodo
            return "block";
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tiemposPose[indice] > 0) {
            // Preparacion y recuperacion: cada golpe vuelve a guardia aunque se mantenga la tecla.
            float duracion = poses[indice] == 3 ? 0.45f : poses[indice] == 2 ? 0.3f : 0.18f;
            float progreso = 1 - tiemposPose[indice] / duracion;
            if (progreso < 0.18f || progreso >= 0.75f) return "idle";
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (poses[indice] == 3) {
                // devuelve este resultado a quien llamo al metodo
                return "special";
            }
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (poses[indice] == 2) {
                // devuelve este resultado a quien llamo al metodo
                return "kick";
            }
            // devuelve este resultado a quien llamo al metodo
            return "punch";
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (personaje.estaEnElAire() || personaje.getY() > 81) {
            // devuelve este resultado a quien llamo al metodo
            return "jump";
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (caminando[indice]) {
            // devuelve este resultado a quien llamo al metodo
            return "walk";
        }
        // devuelve este resultado a quien llamo al metodo
        return "idle";
    }

    // este metodo pinta el escenario, los luchadores, las barras y los avisos
    private void dibujarCombate(float delta) {
        // realiza esta operacion auxiliar para completar el paso actual
        fondo(delta);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.begin();
        // realiza esta operacion auxiliar para completar el paso actual
        juego.fondo.dibujar(batch, 0, 75, 1280, 540);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.end();
        // realiza esta operacion auxiliar para completar el paso actual
        formas.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(Tema.FONDO);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(0, 75, 1280, 18);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(0, 0, 1280, 75);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(Tema.AMARILLO);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(0, 91, 1280, 2);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(Tema.BORDE);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.end();
        // realiza esta operacion auxiliar para completar el paso actual
        batch.begin();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float x1 = red == null ? jugador1.getX() : posicionVisualX(0);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float x2 = red == null ? jugador2.getX() : posicionVisualX(1);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float y1 = red == null ? jugador1.getY() : posicionVisualY(0);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float y2 = red == null ? jugador2.getY() : posicionVisualY(1);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float centro1 = resultadoRonda || finalizado ? 225 : x1 + 150;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float centro2 = resultadoRonda || finalizado ? 1055 : x2 + 150;
        // realiza esta operacion auxiliar para completar el paso actual
        apariencia1.dibujar(batch, elegirPose(jugador1, 0), centro1, y1, 350, false);
        // realiza esta operacion auxiliar para completar el paso actual
        apariencia2.dibujar(batch, elegirPose(jugador2, 1), centro2, y2, 350, true);
        // realiza esta operacion auxiliar para completar el paso actual
        batch.end();
        // realiza esta operacion auxiliar para completar el paso actual
        dibujarBarras(64, jugador1, Tema.AMARILLO, false);
        // realiza esta operacion auxiliar para completar el paso actual
        dibujarBarras(736, jugador2, Tema.AMARILLO, true);
        // realiza esta operacion auxiliar para completar el paso actual
        texto("J1 / " + tipo1.getNombreVisible(), 64, 687, 1.15f, Tema.AMARILLO);
        // realiza esta operacion auxiliar para completar el paso actual
        texto("J2 / " + tipo2.getNombreVisible(), 830, 687, 1.15f, Tema.AMARILLO);
        // realiza esta operacion auxiliar para completar el paso actual
        centrado(String.format("%02d", (int) Math.ceil(combate.getTiempoRonda())), 640, 677, 3.1f, Tema.CREMA);
        // realiza esta operacion auxiliar para completar el paso actual
        texto("RONDAS  " + combate.getRondasJugador1() + " / 2", 64, 594, 1.25f, Tema.CREMA);
        // realiza esta operacion auxiliar para completar el paso actual
        texto("RONDAS  " + combate.getRondasJugador2() + " / 2", 1032, 594, 1.25f, Tema.CREMA);
        // realiza esta operacion auxiliar para completar el paso actual
        dibujarIndicadoresAtaque(115, jugador1, combate.getRecargaBombaJugador1(), combate.getRecargaEspecialJugador1(), false);
        // realiza esta operacion auxiliar para completar el paso actual
        dibujarIndicadoresAtaque(945, jugador2, combate.getRecargaBombaJugador2(), combate.getRecargaEspecialJugador2(), true);
        // realiza esta operacion auxiliar para completar el paso actual
        dibujarAvisos();
    }

    // este metodo dibujar avisos concentra una operacion de esta clase y recibe el control desde quien lo llama
    private void dibujarAvisos() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (!rivalListo) {
            // realiza esta operacion auxiliar para completar el paso actual
            panel(240, 260, 800, 140, Tema.AMARILLO);
            // realiza esta operacion auxiliar para completar el paso actual
            centrado("ESPERANDO AL RIVAL", 640, 360, 2.4f, Tema.CREMA);
            // realiza esta operacion auxiliar para completar el paso actual
            centrado(red == null ? "" : red.getEstado(), 640, 310, 1.2f, Tema.GRIS);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else if (cuentaRegresiva > 0 && !finalizado) {
            // realiza esta operacion auxiliar para completar el paso actual
            centrado("PREPARADOS", 640, 500, 1.5f, Tema.CREMA);
            // realiza esta operacion auxiliar para completar el paso actual
            centrado("" + (int) Math.ceil(cuentaRegresiva), 640, 418, 5, Tema.AMARILLO);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else if (resultadoRonda || finalizado) {
            // realiza esta operacion auxiliar para completar el paso actual
            panel(300, 468, 680, 110, Tema.AMARILLO);
            // realiza esta operacion auxiliar para completar el paso actual
            centrado(ganadorRonda == 0 ? "EMPATE" : "GANÓ EL JUGADOR " + ganadorRonda, 640, 535, 2.2f, Tema.CREMA);
            // realiza esta operacion auxiliar para completar el paso actual
            centrado(finalizado ? "COMBATE TERMINADO" : "PREPARATE PARA LA SIGUIENTE", 640, 490, 1.2f, Tema.AMARILLO);
        }
    }

    // este metodo muestra la recarga de cada ataque y las teclas asignadas
    private void dibujarIndicadoresAtaque(float x, Personaje personaje, float bomba, float especial, boolean segundo) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float[] tiempos = {0, bomba, especial};
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float[] maximos = {1, personaje.getAtaqueBomba().getTiempoRecarga(), personaje.getAtaqueEspecial().getTiempoRecarga()};
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String[] nombres = {"GOLPE", "BOMBA", "ESPECIAL"};
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        String[] teclas;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (red != null) {
            // actualiza teclas con el valor que necesita el estado actual
            teclas = new String[]{"E / L", "ESP / M", "R / P"};
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else if (segundo) {
            // actualiza teclas con el valor que necesita el estado actual
            teclas = new String[]{"L", "M", "P"};
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else {
            // actualiza teclas con el valor que necesita el estado actual
            teclas = new String[]{"E", "ESP", "R"};
        }
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        for (int i = 0; i < 3; i++) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            float centro = x + i * 110;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            float carga = Math.max(0, Math.min(1, 1 - tiempos[i] / maximos[i]));
            // realiza esta operacion auxiliar para completar el paso actual
            formas.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
            // realiza esta operacion auxiliar para completar el paso actual
            formas.setColor(Tema.BORDE);
            // realiza esta operacion auxiliar para completar el paso actual
            formas.circle(centro, 46, 22, 32);
            // realiza esta operacion auxiliar para completar el paso actual
            formas.setColor(Tema.GRIS);
            // realiza esta operacion auxiliar para completar el paso actual
            formas.circle(centro, 46, 18, 32);
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (carga >= 1) {
                // realiza esta operacion auxiliar para completar el paso actual
                formas.setColor(Tema.AMARILLO);
                // realiza esta operacion auxiliar para completar el paso actual
                formas.circle(centro, 46, 18, 32);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } else if (carga > 0) {
                // realiza esta operacion auxiliar para completar el paso actual
                formas.setColor(Tema.AMARILLO);
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                float barrido = carga * 360;
                // este paso prepara o actualiza el estado que necesita el siguiente paso
                int segmentos = Math.max(1, (int) Math.ceil(barrido / 6));
                // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
                for (int segmento = 0; segmento < segmentos; segmento++) {
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    float inicio = -90 + barrido * segmento / segmentos;
                    // este paso prepara o actualiza el estado que necesita el siguiente paso
                    float fin = -90 + barrido * (segmento + 1) / segmentos;
                    // realiza esta operacion auxiliar para completar el paso actual
                    formas.triangle(centro, 46,
                        centro + (float) Math.cos(Math.toRadians(inicio)) * 18,
                        46 + (float) Math.sin(Math.toRadians(inicio)) * 18,
                        centro + (float) Math.cos(Math.toRadians(fin)) * 18,
                        46 + (float) Math.sin(Math.toRadians(fin)) * 18);
                }
            }
            // realiza esta operacion auxiliar para completar el paso actual
            formas.end();
            // realiza esta operacion auxiliar para completar el paso actual
            formas.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line);
            // realiza esta operacion auxiliar para completar el paso actual
            formas.setColor(Tema.BORDE);
            // realiza esta operacion auxiliar para completar el paso actual
            formas.circle(centro, 46, 22, 32);
            // realiza esta operacion auxiliar para completar el paso actual
            formas.end();
            // realiza esta operacion auxiliar para completar el paso actual
            centrado(nombres[i], centro, 82, 0.78f, Tema.CREMA);
            // realiza esta operacion auxiliar para completar el paso actual
            centrado(teclas[i], centro, 12, 0.8f, Tema.GRIS);
        }
    }

    // este metodo representa la proporcion actual de vida y estamina
    private void dibujarBarras(float x, Personaje personaje, Color color, boolean derecha) {
        // realiza esta operacion auxiliar para completar el paso actual
        formas.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(Tema.BORDE);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(x - 2, 629, 484, 33);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(x - 2, 608, 484, 11);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(Tema.PANEL);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(x, 631, 480, 29);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float vida = 480 * personaje.getVida() / personaje.getVidaMaxima();
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(color);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(derecha ? x + 480 - vida : x, 631, vida, 29);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.setColor(Tema.ESTAMINA);
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float estamina = 480 * personaje.getEstamina() / personaje.getEstaminaMaxima();
        // realiza esta operacion auxiliar para completar el paso actual
        formas.rect(derecha ? x + 480 - estamina : x, 610, estamina, 7);
        // realiza esta operacion auxiliar para completar el paso actual
        formas.end();
    }

    @Override
    // este metodo pause concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void pause() {
        // realiza esta operacion auxiliar para completar el paso actual
        controles.limpiar();
    }

    @Override
    // este metodo dispose concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void dispose() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (red != null) {
            // realiza esta operacion auxiliar para completar el paso actual
            red.close();
        }
        // realiza esta operacion auxiliar para completar el paso actual
        apariencia1.dispose();
        // realiza esta operacion auxiliar para completar el paso actual
        apariencia2.dispose();
        // realiza esta operacion auxiliar para completar el paso actual
        super.dispose();
    }
}
