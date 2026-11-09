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

public class PantallaCombate extends PantallaBase {
    private TipoPersonaje tipo1, tipo2;
    private final SesionRed red;
    private final Controles controles;
    private Combate combate;
    private ControladorCombate controlador;
    private Apariencia apariencia1, apariencia2;
    private Personaje jugador1, jugador2;
    private boolean rivalListo;
    private boolean resultadoRonda;
    private boolean finalizado;
    private int ganadorRonda;
    private float cuentaRegresiva = 3, esperaEntreRondas, tiempoDesdeElFinal;
    private final int[] poses = new int[2];
    private final float[] tiemposPose = new float[2];
    private final boolean[] caminando = new boolean[2];
    private byte[] ultimoEstado;
    private int mascaraAnterior;
    private float tiempoDesdeElEnvio;
    private final TipoPersonaje seleccionLocal;

    public PantallaCombate(Main juego, TipoPersonaje tipo1, TipoPersonaje tipo2, SesionRed red) {
        super(juego);
        this.tipo1 = tipo1;
        this.tipo2 = tipo2;
        this.red = red;
        seleccionLocal = tipo1;
        controles = new Controles(red != null);
        rivalListo = red == null;
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
        controlador = new ControladorCombate(combate, controles);
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
        if (red != null) {
            actualizarRed(delta);
        }
        if (red != null && red.haTerminado() && !finalizado) {
            juego.setScreen(new PantallaInicio(juego, "La conexion termino. Podes crear una nueva partida."));
            return;
        }
        if (red == null || red.esAnfitrion()) {
            if (rivalListo && (red == null || red.conectado())) {
                actualizarCombate(delta);
            }
            if (red != null && red.conectado()) {
                enviarEstado();
            }
        }
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
        boolean anfitrion = red != null && red.esAnfitrion();
        int jugadorLocal = red == null ? 0 : anfitrion ? 1 : 2;
        TipoPersonaje personajeResultado = red == null
            ? (ganador == 1 ? tipo1 : tipo2)
            : (anfitrion ? tipo1 : tipo2);
        juego.setScreen(new PantallaResultado(juego, ganador, jugadorLocal, personajeResultado,
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
        controlador.actualizar(delta);
        resolverColision();
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

    private void resolverColision() {
        float superposicion = 195 - (jugador2.getX() - jugador1.getX());
        if (superposicion <= 0) {
            return;
        }
        float mover1 = Math.min(superposicion / 2, jugador1.getX() - 20);
        float mover2 = Math.min(superposicion - mover1, 960 - jugador2.getX());
        mover1 += Math.min(superposicion - mover1 - mover2, jugador1.getX() - 20 - mover1);
        jugador1.mover(-mover1);
        jugador2.mover(mover2);
    }

    private void actualizarRed(float delta) {
        if (!red.conectado()) {
            return;
        }
        tiempoDesdeElEnvio += delta;
        if (red.esAnfitrion()) {
            recibirControlesDelInvitado();
        } else {
            enviarControlesAlAnfitrion();
            recibirEstadoDelAnfitrion();
        }
    }

    private void enviarControlesAlAnfitrion() {
        int mascara = controles.mascaraJugador1();
        int pulsados = mascara & ~mascaraAnterior;
        if (tiempoDesdeElEnvio >= 1f / 60f || mascara != mascaraAnterior) {
            red.enviar(new byte[]{(byte) seleccionLocal.ordinal(), (byte) mascara, (byte) pulsados});
            tiempoDesdeElEnvio = 0;
        }
        mascaraAnterior = mascara;
    }

    private void recibirEstadoDelAnfitrion() {
        byte[] estado = red.ultimoMensaje();
        if (estado == null || estado == ultimoEstado) {
            return;
        }
        ultimoEstado = estado;
        try (DataInputStream datos = new DataInputStream(new ByteArrayInputStream(estado))) {
            TipoPersonaje primero = TipoPersonaje.values()[datos.readUnsignedByte()];
            TipoPersonaje segundo = TipoPersonaje.values()[datos.readUnsignedByte()];
            boolean listo = datos.readBoolean();
            if (primero != tipo1 || segundo != tipo2) {
                tipo1 = primero;
                tipo2 = segundo;
                crearCombate();
            }
            rivalListo = listo;
            combate.leer(datos);
            cuentaRegresiva = datos.readFloat();
            resultadoRonda = datos.readBoolean();
            finalizado = datos.readBoolean();
            ganadorRonda = datos.readInt();
            for (int i = 0; i < 2; i++) {
                poses[i] = datos.readUnsignedByte();
                tiemposPose[i] = datos.readFloat();
                caminando[i] = datos.readBoolean();
            }
        } catch (Exception e) {
            Gdx.app.error("Red", "Estado de combate invalido", e);
            red.close();
        }
    }

    private void recibirControlesDelInvitado() {
        byte[] entrada;
        int pulsados = 0, mascara = controles.mascaraJugador2();
        while ((entrada = red.siguienteMensaje()) != null) {
            if (entrada.length != 3 || entrada[0] < 0 || entrada[0] >= TipoPersonaje.values().length) {
                red.close();
                return;
            }
            if (!rivalListo) {
                tipo2 = TipoPersonaje.values()[entrada[0]];
                crearCombate();
                rivalListo = true;
            }
            mascara = entrada[1] & Controles.TODAS_LAS_ACCIONES;
            pulsados |= entrada[2] & Controles.TODAS_LAS_ACCIONES;
        }
        controles.aplicarJugador2(mascara | pulsados);
    }

    private void enviarEstado() {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream datos = new DataOutputStream(bytes);
            datos.writeByte(tipo1.ordinal());
            datos.writeByte(tipo2.ordinal());
            datos.writeBoolean(rivalListo);
            combate.escribir(datos);
            datos.writeFloat(cuentaRegresiva);
            datos.writeBoolean(resultadoRonda);
            datos.writeBoolean(finalizado);
            datos.writeInt(ganadorRonda);
            for (int i = 0; i < 2; i++) {
                datos.writeByte(poses[i]);
                datos.writeFloat(tiemposPose[i]);
                datos.writeBoolean(caminando[i]);
            }
            red.enviar(bytes.toByteArray());
        } catch (IOException e) {
            red.close();
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
        fondo(delta, "");
        batch.begin();
        juego.fondo.dibujar(batch, 0, 75, 1280, 540);
        batch.end();
        formas.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        formas.setColor(Tema.FONDO);
        formas.rect(0, 75, 1280, 18);
        formas.rect(0, 0, 1280, 75);
        formas.setColor(Tema.ROJO);
        formas.rect(0, 91, 1280, 2);
        formas.setColor(Tema.BORDE);
        formas.end();
        batch.begin();
        float centro1 = resultadoRonda || finalizado ? 225 : jugador1.getX() + 150;
        float centro2 = resultadoRonda || finalizado ? 1055 : jugador2.getX() + 150;
        apariencia1.dibujar(batch, elegirPose(jugador1, 0), centro1, jugador1.getY(), 350, false);
        apariencia2.dibujar(batch, elegirPose(jugador2, 1), centro2, jugador2.getY(), 350, true);
        batch.end();
        dibujarBarras(64, jugador1, Tema.AMARILLO, false);
        dibujarBarras(736, jugador2, Tema.ROJO, true);
        texto("J1 / " + tipo1.getNombreVisible(), 64, 687, 1.15f, Tema.AMARILLO);
        texto("J2 / " + tipo2.getNombreVisible(), 830, 687, 1.15f, Tema.ROJO);
        centrado(String.format("%02d", (int) Math.ceil(combate.getTiempoRonda())), 640, 677, 3.1f, Tema.CREMA);
        texto("RONDAS  " + combate.getRondasJugador1() + " / 2", 64, 594, 1.25f, Tema.CREMA);
        texto("RONDAS  " + combate.getRondasJugador2() + " / 2", 1032, 594, 1.25f, Tema.CREMA);
        dibujarIndicadoresAtaque(115, jugador1, combate.getRecargaBombaJugador1(), combate.getRecargaEspecialJugador1(), false);
        dibujarIndicadoresAtaque(945, jugador2, combate.getRecargaBombaJugador2(), combate.getRecargaEspecialJugador2(), true);
        String ubicacion = "J1: IZQUIERDA / J2: DERECHA";
        if (red != null) {
            if (red.esAnfitrion()) {
                ubicacion = "SOS J1 / IZQUIERDA";
            } else {
                ubicacion = "SOS J2 / DERECHA";
            }
        }
        centrado(ubicacion, 640, 49, 1.1f, Tema.CREMA);
        centrado("ESC: volver al menu", 640, 24, 1, Tema.GRIS);
        dibujarAvisos();
    }

    private void dibujarAvisos() {
        if (!rivalListo) {
            panel(240, 260, 800, 140, Tema.AMARILLO);
            centrado("ESPERANDO AL RIVAL", 640, 360, 2.4f, Tema.CREMA);
            centrado(red == null ? "" : red.getEstado(), 640, 310, 1.2f, Tema.GRIS);
        } else if (cuentaRegresiva > 0 && !finalizado) {
            centrado("PREPARADOS", 640, 500, 1.5f, Tema.CREMA);
            centrado("" + (int) Math.ceil(cuentaRegresiva), 640, 418, 5, Tema.ROJO);
        } else if (resultadoRonda || finalizado) {
            panel(300, 468, 680, 110, Tema.ROJO);
            centrado(ganadorRonda == 0 ? "EMPATE" : "RONDA PARA JUGADOR " + ganadorRonda, 640, 535, 2.2f, Tema.CREMA);
            centrado(finalizado ? "COMBATE TERMINADO" : "PREPARATE PARA LA SIGUIENTE", 640, 490, 1.2f, Tema.AMARILLO);
        }
    }

    private void dibujarIndicadoresAtaque(float x, Personaje personaje, float bomba, float especial, boolean segundo) {
        float[] tiempos = {0, bomba, especial};
        float[] maximos = {1, personaje.getAtaqueBomba().getTiempoRecarga(), personaje.getAtaqueEspecial().getTiempoRecarga()};
        String[] nombres = {"GOLPE", "BOMBA", "ESPECIAL"};
        String[] teclas;
        if (red != null) {
            teclas = new String[]{"E / L", "ESP / M", "R / P"};
        } else if (segundo) {
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
        if (red != null) {
            red.close();
        }
        apariencia1.dispose();
        apariencia2.dispose();
        super.dispose();
    }
}
