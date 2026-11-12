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
import com.proyecto.juego.visual.Apariencia;
import com.proyecto.juego.visual.Tema;

public class PantallaCombate extends PantallaBase {
    private TipoPersonaje tipo1, tipo2;
    private final Controles controles;
    private Combate combate;
    private ControladorCombate controlador;
    private Apariencia apariencia1, apariencia2;
    private Personaje jugador1, jugador2;
    private boolean resultadoRonda;
    private boolean finalizado;
    private int ganadorRonda;
    private float cuentaRegresiva = 3, esperaEntreRondas, tiempoDesdeElFinal;
    private final int[] poses = new int[2];
    private final float[] tiemposPose = new float[2];
    private final boolean[] caminando = new boolean[2];

    public PantallaCombate(Main juego, TipoPersonaje tipo1, TipoPersonaje tipo2) {
        super(juego);
        this.tipo1 = tipo1;
        this.tipo2 = tipo2;
        controles = new Controles();
        crearCombate();
    }

    private void crearCombate() {
        if (apariencia1 != null) {
            apariencia1.dispose();
        }
        if (apariencia2 != null) {
            apariencia2.dispose();
        }
        apariencia1 = new Apariencia(tipo1);
        apariencia2 = new Apariencia(tipo2);
        jugador1 = new Personaje("Jugador 1", tipo1);
        jugador2 = new Personaje("Jugador 2", tipo2);
        jugador1.establecerLimites(20, 960);
        jugador2.establecerLimites(20, 960);
        combate = new Combate(jugador1, jugador2);
        controlador = new ControladorCombate(combate);
        ubicarJugadores();
    }

    private void ubicarJugadores() {
        jugador1.establecerPosicion(70, 80);
        jugador2.establecerPosicion(910, 80);
        poses[0] = poses[1] = 0;
        tiemposPose[0] = tiemposPose[1] = 0;
        caminando[0] = caminando[1] = false;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controles);
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 0.05f);
        if (tecla(Input.Keys.ESCAPE)) {
            juego.setScreen(new PantallaInicio(juego));
            return;
        }
        actualizarCombate(delta);
        if (finalizado) {
            tiempoDesdeElFinal += delta;
            if (tiempoDesdeElFinal >= 2.5f) {
                mostrarResultado();
                return;
            }
        }
        dibujarCombate(delta);
    }

    private void mostrarResultado() {
        int ganador = combate.getRondasJugador1() >= 2 ? 1 : 2;
        TipoPersonaje personajeResultado = ganador == 1 ? tipo1 : tipo2;
        juego.setScreen(new PantallaResultado(juego, ganador, 0, personajeResultado,
            combate.getRondasJugador1(), combate.getRondasJugador2()));
    }

    private void actualizarCombate(float delta) {
        if (finalizado) {
            return;
        }
        if (cuentaRegresiva > 0) {
            cuentaRegresiva = Math.max(0, cuentaRegresiva - delta);
            return;
        }
        if (resultadoRonda) {
            esperaEntreRondas -= delta;
            if (esperaEntreRondas <= 0) {
                if (combate.combateTerminado()) {
                    finalizado = true;
                } else {
                    combate.iniciarRonda();
                    ubicarJugadores();
                    resultadoRonda = false;
                    cuentaRegresiva = 2;
                }
            }
            return;
        }
        float x1 = jugador1.getX(), x2 = jugador2.getX();
        controlador.actualizar(delta, controles.mascaraJugador1(), controles.mascaraJugador2());
        controlador.resolverColision();
        caminando[0] = Math.abs(jugador1.getX() - x1) > 0.01f;
        caminando[1] = Math.abs(jugador2.getX() - x2) > 0.01f;
        for (int i = 0; i < 2; i++) {
            int ataque = i == 0 ? controlador.getAtaque1() : controlador.getAtaque2();
            tiemposPose[i] = Math.max(0, tiemposPose[i] - delta);
            if (ataque != 0) {
                poses[i] = ataque;
                if (ataque == 3) {
                    tiemposPose[i] = 0.45f;
                } else if (ataque == 2) {
                    tiemposPose[i] = 0.3f;
                } else {
                    tiemposPose[i] = 0.18f;
                }
            }
        }
        if (combate.terminoRonda()) {
            ganadorRonda = combate.obtenerGanadorRonda();
            combate.registrarGanadorRonda();
            resultadoRonda = true;
            esperaEntreRondas = 2;
        }
    }

    private String elegirPose(Personaje personaje, int indice) {
        if (resultadoRonda || finalizado) {
            if (ganadorRonda == 0) {
                return "idle";
            }
            if (ganadorRonda == indice + 1) {
                return "win";
            }
            return "lose";
        }
        if (personaje.getEstado() == EstadoPersonaje.BLOQUEANDO) {
            return "block";
        }
        if (tiemposPose[indice] > 0) {
            if (poses[indice] == 3) {
                return "special";
            }
            if (poses[indice] == 2) {
                return "kick";
            }
            return "punch";
        }
        if (personaje.estaEnElAire() || personaje.getY() > 81) {
            return "jump";
        }
        if (caminando[indice]) {
            return "walk";
        }
        return "idle";
    }

    private void dibujarCombate(float delta) {
        fondo(delta);
        batch.begin();
        juego.fondo.dibujar(batch, 0, 75, 1280, 540);
        batch.end();
        formas.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        formas.setColor(Tema.FONDO);
        formas.rect(0, 75, 1280, 18);
        formas.rect(0, 0, 1280, 75);
        formas.setColor(Tema.AMARILLO);
        formas.rect(0, 91, 1280, 2);
        formas.setColor(Tema.BORDE);
        formas.end();
        batch.begin();
        float x1 = jugador1.getX();
        float x2 = jugador2.getX();
        float y1 = jugador1.getY();
        float y2 = jugador2.getY();
        float centro1 = resultadoRonda || finalizado ? 225 : x1 + 150;
        float centro2 = resultadoRonda || finalizado ? 1055 : x2 + 150;
        apariencia1.dibujar(batch, elegirPose(jugador1, 0), centro1, y1, 350, false);
        apariencia2.dibujar(batch, elegirPose(jugador2, 1), centro2, y2, 350, true);
        batch.end();
        dibujarBarras(64, jugador1, Tema.AMARILLO, false);
        dibujarBarras(736, jugador2, Tema.AMARILLO, true);
        texto("J1 / " + tipo1.getNombreVisible(), 64, 687, 1.15f, Tema.AMARILLO);
        texto("J2 / " + tipo2.getNombreVisible(), 830, 687, 1.15f, Tema.AMARILLO);
        centrado(String.format("%02d", (int) Math.ceil(combate.getTiempoRonda())), 640, 677, 3.1f, Tema.CREMA);
        texto("RONDAS  " + combate.getRondasJugador1() + " / 2", 64, 594, 1.25f, Tema.CREMA);
        texto("RONDAS  " + combate.getRondasJugador2() + " / 2", 1032, 594, 1.25f, Tema.CREMA);
        dibujarIndicadoresAtaque(115, jugador1, combate.getRecargaBombaJugador1(), combate.getRecargaEspecialJugador1(), false);
        dibujarIndicadoresAtaque(945, jugador2, combate.getRecargaBombaJugador2(), combate.getRecargaEspecialJugador2(), true);
        dibujarAvisos();
    }

    private void dibujarAvisos() {
        if (cuentaRegresiva > 0 && !finalizado) {
            centrado("PREPARADOS", 640, 500, 1.5f, Tema.CREMA);
            centrado("" + (int) Math.ceil(cuentaRegresiva), 640, 418, 5, Tema.AMARILLO);
        } else if (resultadoRonda || finalizado) {
            panel(300, 468, 680, 110, Tema.AMARILLO);
            centrado(ganadorRonda == 0 ? "EMPATE" : "GANÓ EL JUGADOR " + ganadorRonda, 640, 535, 2.2f, Tema.CREMA);
            centrado(finalizado ? "COMBATE TERMINADO" : "PREPARATE PARA LA SIGUIENTE", 640, 490, 1.2f, Tema.AMARILLO);
        }
    }

    private void dibujarIndicadoresAtaque(float x, Personaje personaje, float bomba, float especial, boolean segundo) {
        float[] tiempos = {0, bomba, especial};
        float[] maximos = {1, personaje.getAtaqueBomba().getTiempoRecarga(), personaje.getAtaqueEspecial().getTiempoRecarga()};
        String[] nombres = {"GOLPE", "BOMBA", "ESPECIAL"};
        String[] teclas;
        if (segundo) {
            teclas = new String[]{"L", "M", "P"};
        } else {
            teclas = new String[]{"E", "ESP", "R"};
        }
        for (int i = 0; i < 3; i++) {
            float centro = x + i * 110;
            float carga = Math.max(0, Math.min(1, 1 - tiempos[i] / maximos[i]));
            formas.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
            formas.setColor(Tema.BORDE);
            formas.circle(centro, 46, 22, 32);
            formas.setColor(Tema.GRIS);
            formas.circle(centro, 46, 18, 32);
            if (carga >= 1) {
                formas.setColor(Tema.AMARILLO);
                formas.circle(centro, 46, 18, 32);
            } else if (carga > 0) {
                formas.setColor(Tema.AMARILLO);
                float barrido = carga * 360;
                int segmentos = Math.max(1, (int) Math.ceil(barrido / 6));
                for (int segmento = 0; segmento < segmentos; segmento++) {
                    float inicio = -90 + barrido * segmento / segmentos;
                    float fin = -90 + barrido * (segmento + 1) / segmentos;
                    formas.triangle(centro, 46,
                        centro + (float) Math.cos(Math.toRadians(inicio)) * 18,
                        46 + (float) Math.sin(Math.toRadians(inicio)) * 18,
                        centro + (float) Math.cos(Math.toRadians(fin)) * 18,
                        46 + (float) Math.sin(Math.toRadians(fin)) * 18);
                }
            }
            formas.end();
            formas.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line);
            formas.setColor(Tema.BORDE);
            formas.circle(centro, 46, 22, 32);
            formas.end();
            centrado(nombres[i], centro, 82, 0.78f, Tema.CREMA);
            centrado(teclas[i], centro, 12, 0.8f, Tema.GRIS);
        }
    }

    private void dibujarBarras(float x, Personaje personaje, Color color, boolean derecha) {
        formas.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        formas.setColor(Tema.BORDE);
        formas.rect(x - 2, 629, 484, 33);
        formas.rect(x - 2, 608, 484, 11);
        formas.setColor(Tema.PANEL);
        formas.rect(x, 631, 480, 29);
        float vida = 480 * personaje.getVida() / personaje.getVidaMaxima();
        formas.setColor(color);
        formas.rect(derecha ? x + 480 - vida : x, 631, vida, 29);
        formas.setColor(Tema.ESTAMINA);
        float estamina = 480 * personaje.getEstamina() / personaje.getEstaminaMaxima();
        formas.rect(derecha ? x + 480 - estamina : x, 610, estamina, 7);
        formas.end();
    }

    @Override
    public void pause() {
        controles.limpiar();
    }

    @Override
    public void dispose() {
        apariencia1.dispose();
        apariencia2.dispose();
        super.dispose();
    }
}
