package com.proyecto.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.proyecto.juego.personajes.EstadoPersonaje;
import com.proyecto.juego.combate.Combate;
import com.proyecto.juego.combate.ControladorCombate;
import com.proyecto.juego.controles.Controles;
import com.proyecto.juego.personajes.EstadoPersonaje;
import com.proyecto.juego.personajes.Personaje;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PantallaCombate implements Screen {

        private OrthographicCamera camara;

        private SpriteBatch batch;

        private BitmapFont fuente;

        private ShapeRenderer formas;

        private Controles controles;

        private Combate combate;

        private ControladorCombate controlador;

        private Personaje jugador1;

        private Personaje jugador2;

        private Texture texturaJugador1;
        private TextureRegion spriteJugador1;

        private Texture texturaJugador2;
        private TextureRegion spriteJugador2;
        private final float anchoPersonaje = 300;

        private final float altoPersonaje = 400;

        private final float anchoColision = 120;
        private final float altoColision = 350;
        private final float margenColision = 90;
        private float tiempoResultado;

        private boolean mostrandoResultado;

        private int ganadorRonda;

        public PantallaCombate() {

                camara = new OrthographicCamera();

                camara.setToOrtho(false, 1280, 720);

                batch = new SpriteBatch();

                fuente = new BitmapFont();

                fuente.getData().setScale(2);

                formas = new ShapeRenderer();

                texturaJugador1 = new Texture("personajes/jugador1_idle.png");
                spriteJugador1 = new TextureRegion(texturaJugador1);

                texturaJugador2 = new Texture("personajes/jugador1_idle.png");
                spriteJugador2 = new TextureRegion(texturaJugador2);
                spriteJugador2.flip(true, false);

                controles = new Controles();

                Gdx.input.setInputProcessor(controles);

                jugador1 = new Personaje("Jugador 1", 300);
                jugador2 = new Personaje("Jugador 2", 300);

                jugador1.establecerPosicion(150, 0);
                jugador2.establecerPosicion(830, 0);

                jugador1.establecerLimites(20, 960);
                jugador2.establecerLimites(20, 960);

                combate = new Combate(
                                jugador1,
                                jugador2);

                controlador = new ControladorCombate(
                                combate,
                                controles);

                tiempoResultado = 0;

                mostrandoResultado = false;

                ganadorRonda = 0;
        }

        @Override
        public void show() {
        }

        @Override
        public void render(float delta) {

                if (mostrandoResultado) {

                        actualizarResultado(delta);

                } else {

                        controlador.actualizar(delta);

                        resolverColision();

                        if (combate.rondaEstaTerminada()) {

                                comenzarResultadoRonda();
                        }
                }

                camara.update();

                Gdx.gl.glClearColor(
                                0.08f,
                                0.08f,
                                0.08f,
                                1);

                Gdx.gl.glClear(
                                GL20.GL_COLOR_BUFFER_BIT);

                formas.setProjectionMatrix(
                                camara.combined);

                formas.begin(
                                ShapeRenderer.ShapeType.Filled);

                formas.setColor(
                                0.25f,
                                0.25f,
                                0.25f,
                                1);

                formas.rect(
                                0,
                                0,
                                1280,
                                80);

                if (jugador1.getEstado() == EstadoPersonaje.BLOQUEANDO) {

                        formas.setColor(
                                        0.3f,
                                        0.7f,
                                        1f,
                                        0.7f);

                        formas.circle(
                                        jugador1.getX() + anchoPersonaje + 10,
                                        jugador1.getY() + altoPersonaje / 2,
                                        45);
                }

                if (jugador2.getEstado() == EstadoPersonaje.BLOQUEANDO) {

                        formas.setColor(
                                        0.3f,
                                        0.7f,
                                        1f,
                                        0.7f);

                        formas.circle(
                                        jugador2.getX() - 10,
                                        jugador2.getY() + altoPersonaje / 2,
                                        45);
                }

                float vida1 = jugador1.getVida()
                                / jugador1.getVidaMaxima();

                float vida2 = jugador2.getVida()
                                / jugador2.getVidaMaxima();

                formas.setColor(
                                0.15f,
                                0.15f,
                                0.15f,
                                1);

                formas.rect(
                                70,
                                640,
                                500,
                                30);

                formas.rect(
                                710,
                                640,
                                500,
                                30);

                formas.setColor(
                                0.2f,
                                0.8f,
                                0.2f,
                                1);

                formas.rect(
                                70,
                                640,
                                500 * vida1,
                                30);

                formas.rect(
                                710 + (500 * (1 - vida2)),
                                640,
                                500 * vida2,
                                30);

                formas.end();

                batch.setProjectionMatrix(camara.combined);
                batch.begin();

                batch.draw(
                                spriteJugador1,
                                jugador1.getX(),
                                jugador1.getY(),
                                anchoPersonaje,
                                altoPersonaje);

                batch.draw(
                                spriteJugador1,
                                jugador1.getX(),
                                jugador1.getY(),
                                anchoPersonaje,
                                altoPersonaje);

                batch.draw(
                                spriteJugador2,
                                jugador2.getX(),
                                jugador2.getY(),
                                anchoPersonaje,
                                altoPersonaje);

                batch.end();

                batch.setProjectionMatrix(
                                camara.combined);

                batch.begin();

                int segundos = (int) Math.ceil(
                                combate.getTiempoRonda());

                String textoTiempo = String.valueOf(segundos);

                fuente.draw(
                                batch,
                                textoTiempo,
                                630,
                                680);

                String rondas1 = "Rondas: "
                                + combate.getRondasJugador1();

                String rondas2 = "Rondas: "
                                + combate.getRondasJugador2();

                fuente.draw(
                                batch,
                                rondas1,
                                70,
                                620);

                fuente.draw(
                                batch,
                                rondas2,
                                1050,
                                620);

                if (mostrandoResultado) {

                        String textoGanador;

                        if (ganadorRonda == 1) {

                                textoGanador = "El jugador 1 gana la ronda";

                        } else if (ganadorRonda == 2) {

                                textoGanador = "El jugador 2 gana la ronda";

                        } else {

                                textoGanador = "Empate";
                        }

                        fuente.draw(
                                        batch,
                                        textoGanador,
                                        430,
                                        380);
                }

                batch.end();
        }

        private void comenzarResultadoRonda() {

                if (mostrandoResultado) {
                        return;
                }

                ganadorRonda = combate.obtenerGanadorRonda();

                combate.registrarGanadorRonda();

                mostrandoResultado = true;

                tiempoResultado = 2;
        }

        private void actualizarResultado(float delta) {

                tiempoResultado -= delta;

                if (tiempoResultado > 0) {
                        return;
                }

                if (combate.combateTerminado()) {

                        mostrandoResultado = false;

                        return;
                }

                combate.iniciarRonda();

                jugador1.establecerPosicion(
                                250,
                                0);

                jugador2.establecerPosicion(
                                950,
                                0);

                mostrandoResultado = false;
        }

        private void resolverColision() {

                float izquierda1 = jugador1.getX() + margenColision;
                float derecha1 = izquierda1 + anchoColision;

                float izquierda2 = jugador2.getX() + margenColision;
                float derecha2 = izquierda2 + anchoColision;

                boolean seSuperponen = derecha1 > izquierda2 &&
                                izquierda1 < derecha2;

                if (!seSuperponen) {
                        return;
                }

                float superposicion;

                if (jugador1.getX() < jugador2.getX()) {

                        superposicion = derecha1 - izquierda2;

                        jugador1.establecerPosicion(
                                        jugador1.getX() - superposicion / 2,
                                        jugador1.getY());

                        jugador2.establecerPosicion(
                                        jugador2.getX() + superposicion / 2,
                                        jugador2.getY());

                } else {

                        superposicion = derecha2 - izquierda1;

                        jugador1.establecerPosicion(
                                        jugador1.getX() + superposicion / 2,
                                        jugador1.getY());

                        jugador2.establecerPosicion(
                                        jugador2.getX() - superposicion / 2,
                                        jugador2.getY());
                }
        }

        @Override
        public void resize(
                        int width,
                        int height) {

                camara.viewportWidth = 1280;

                camara.viewportHeight = 720;

                camara.update();
        }

        @Override
        public void pause() {
        }

        @Override
        public void resume() {
        }

        @Override
        public void hide() {
        }

        @Override
        public void dispose() {

                batch.dispose();

                formas.dispose();

                fuente.dispose();
        }

}