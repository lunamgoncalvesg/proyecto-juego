package com.proyecto.juego.combate;

import com.proyecto.juego.personajes.EstadoPersonaje;
import com.proyecto.juego.personajes.Personaje;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// esta clase agrupa la logica y los datos principales de combate
public class Combate {

    // este campo representa jugador1 y conserva ese dato entre llamadas
    private Personaje jugador1;
    // este campo representa jugador2 y conserva ese dato entre llamadas
    private Personaje jugador2;
    // este campo representa tiemporonda y conserva ese dato entre llamadas
    private float tiempoRonda;
    // este campo representa tiempomaximo y conserva ese dato entre llamadas
    private final float tiempoMaximo = 60;
    // este campo representa rondasjugador1 y conserva ese dato entre llamadas
    private int rondasJugador1;
    // este campo representa rondasjugador2 y conserva ese dato entre llamadas
    private int rondasJugador2;
    // este campo representa tiemposinaccionjugador1 y conserva ese dato entre llamadas
    private float tiempoSinAccionJugador1;
    // este campo representa tiemposinaccionjugador2 y conserva ese dato entre llamadas
    private float tiempoSinAccionJugador2;
    // este campo representa recargabombajugador1 y conserva ese dato entre llamadas
    private float recargaBombaJugador1;
    // este campo representa recargabombajugador2 y conserva ese dato entre llamadas
    private float recargaBombaJugador2;
    // este campo representa recargaespecialjugador1 y conserva ese dato entre llamadas
    private float recargaEspecialJugador1;
    // este campo representa recargaespecialjugador2 y conserva ese dato entre llamadas
    private float recargaEspecialJugador2;

    // este metodo combate concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Combate(Personaje jugador1, Personaje jugador2) {
        // actualiza this.jugador1 con el valor que necesita el estado actual
        this.jugador1 = jugador1;
        // actualiza this.jugador2 con el valor que necesita el estado actual
        this.jugador2 = jugador2;
        // actualiza rondas jugador1 con el valor que necesita el estado actual
        rondasJugador1 = 0;
        // actualiza rondas jugador2 con el valor que necesita el estado actual
        rondasJugador2 = 0;
        // realiza esta operacion auxiliar para completar el paso actual
        iniciarRonda();
    }

    // este metodo iniciar ronda concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void iniciarRonda() {
        // actualiza tiempo ronda con el valor que necesita el estado actual
        tiempoRonda = tiempoMaximo;
        // actualiza tiempo sin accion jugador1 con el valor que necesita el estado actual
        tiempoSinAccionJugador1 = 0;
        // actualiza tiempo sin accion jugador2 con el valor que necesita el estado actual
        tiempoSinAccionJugador2 = 0;
        // actualiza recarga bomba jugador1 con el valor que necesita el estado actual
        recargaBombaJugador1 = 0;
        // actualiza recarga bomba jugador2 con el valor que necesita el estado actual
        recargaBombaJugador2 = 0;
        // actualiza recarga especial jugador1 con el valor que necesita el estado actual
        recargaEspecialJugador1 = 0;
        // actualiza recarga especial jugador2 con el valor que necesita el estado actual
        recargaEspecialJugador2 = 0;
        // realiza esta operacion auxiliar para completar el paso actual
        jugador1.reiniciarVida();
        // realiza esta operacion auxiliar para completar el paso actual
        jugador2.reiniciarVida();
    }

    // este metodo avanza la simulacion segun el tiempo transcurrido
    public void actualizar(float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (terminoRonda()) {
            // devuelve este resultado a quien llamo al metodo
            return;
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        tiempoRonda -= delta;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tiempoRonda < 0) {
            // actualiza tiempo ronda con el valor que necesita el estado actual
            tiempoRonda = 0;
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        tiempoSinAccionJugador1 += delta;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        tiempoSinAccionJugador2 += delta;
        // realiza esta operacion auxiliar para completar el paso actual
        actualizarRegeneracion();
        // realiza esta operacion auxiliar para completar el paso actual
        actualizarRecargas(delta);
    }

    // este metodo recupera estamina por cada segundo que pasa sin atacar
    private void actualizarRegeneracion() {
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        while (tiempoSinAccionJugador1 >= 1) {
            // realiza esta operacion auxiliar para completar el paso actual
            jugador1.recuperarEstamina(1);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            tiempoSinAccionJugador1 -= 1;
        }
        // repite este bloque mientras se cumpla la condicion o queden elementos por recorrer
        while (tiempoSinAccionJugador2 >= 1) {
            // realiza esta operacion auxiliar para completar el paso actual
            jugador2.recuperarEstamina(1);
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            tiempoSinAccionJugador2 -= 1;
        }
    }

    // este metodo reduce el tiempo restante de las recargas especiales
    private void actualizarRecargas(float delta) {
        // actualiza recarga bomba jugador1 con el valor que necesita el estado actual
        recargaBombaJugador1 = reducirRecarga(recargaBombaJugador1, delta);
        // actualiza recarga bomba jugador2 con el valor que necesita el estado actual
        recargaBombaJugador2 = reducirRecarga(recargaBombaJugador2, delta);
        // actualiza recarga especial jugador1 con el valor que necesita el estado actual
        recargaEspecialJugador1 = reducirRecarga(recargaEspecialJugador1, delta);
        // actualiza recarga especial jugador2 con el valor que necesita el estado actual
        recargaEspecialJugador2 = reducirRecarga(recargaEspecialJugador2, delta);
    }

    // este metodo descuenta el tiempo y limita la recarga para que nunca sea negativa
    private float reducirRecarga(float segundosRestantes, float delta) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (segundosRestantes > 0) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            segundosRestantes -= delta;
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (segundosRestantes < 0) {
                // actualiza segundos restantes con el valor que necesita el estado actual
                segundosRestantes = 0;
            }
        }
        // devuelve este resultado a quien llamo al metodo
        return segundosRestantes;
    }

    // este metodo atacar jugador1 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean atacarJugador1(TipoAtaque tipo) {
        // devuelve este resultado a quien llamo al metodo
        return realizarAtaque(jugador1, jugador2, tipo, true);
    }

    // este metodo atacar jugador2 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean atacarJugador2(TipoAtaque tipo) {
        // devuelve este resultado a quien llamo al metodo
        return realizarAtaque(jugador2, jugador1, tipo, false);
    }

    // este metodo valida estamina, bloqueo, distancia y recarga antes de causar danio
    private boolean realizarAtaque(Personaje atacante, Personaje defensor, TipoAtaque tipo, boolean esJugador1) {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (terminoRonda()) {
            // devuelve este resultado a quien llamo al metodo
            return false;
        }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        Ataque ataque;
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tipo == TipoAtaque.SUAVE) {
            // actualiza ataque con el valor que necesita el estado actual
            ataque = atacante.getAtaqueSuave();
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else if (tipo == TipoAtaque.BOMBA) {
            // actualiza ataque con el valor que necesita el estado actual
            ataque = atacante.getAtaqueBomba();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (esJugador1 && recargaBombaJugador1 > 0) {
                // devuelve este resultado a quien llamo al metodo
                return false;
            }
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (!esJugador1 && recargaBombaJugador2 > 0) {
                // devuelve este resultado a quien llamo al metodo
                return false;
            }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else {
            // actualiza ataque con el valor que necesita el estado actual
            ataque = atacante.getAtaqueEspecial();
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (esJugador1 && recargaEspecialJugador1 > 0) {
                // devuelve este resultado a quien llamo al metodo
                return false;
            }
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (!esJugador1 && recargaEspecialJugador2 > 0) {
                // devuelve este resultado a quien llamo al metodo
                return false;
            }
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (!atacante.tieneEstamina(ataque.getCosto())) {
            // devuelve este resultado a quien llamo al metodo
            return false;
        }
        // realiza esta operacion auxiliar para completar el paso actual
        atacante.gastarEstamina(ataque.getCosto());
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (defensor.getEstado() != EstadoPersonaje.BLOQUEANDO) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            float alcance;
            // elige una ruta distinta segun el valor que se esta evaluando
            switch (ataque.getTipo()) {
            // atiende este valor posible de la seleccion anterior
            case SUAVE:
                // actualiza alcance con el valor que necesita el estado actual
                alcance = 195.0F;
                // sale de la seleccion actual para no continuar con los otros casos
                break;

            // atiende este valor posible de la seleccion anterior
            case BOMBA:
                // actualiza alcance con el valor que necesita el estado actual
                alcance = 215.0F;
                // sale de la seleccion actual para no continuar con los otros casos
                break;

            // atiende este valor posible de la seleccion anterior
            case ESPECIAL:
                // actualiza alcance con el valor que necesita el estado actual
                alcance = 245.0F;
                // sale de la seleccion actual para no continuar con los otros casos
                break;

            // este paso prepara o actualiza el estado que necesita el siguiente paso
            default:
                // devuelve este resultado a quien llamo al metodo
                return false;

            }
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (estaEnAlcance(atacante, defensor, alcance)) {
                // realiza esta operacion auxiliar para completar el paso actual
                defensor.recibirDanio(ataque.getDanio());
            }
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (esJugador1) {
            // actualiza tiempo sin accion jugador1 con el valor que necesita el estado actual
            tiempoSinAccionJugador1 = 0;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else {
            // actualiza tiempo sin accion jugador2 con el valor que necesita el estado actual
            tiempoSinAccionJugador2 = 0;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (tipo == TipoAtaque.BOMBA) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (esJugador1) {
                // actualiza recarga bomba jugador1 con el valor que necesita el estado actual
                recargaBombaJugador1 = ataque.getTiempoRecarga();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } else {
                // actualiza recarga bomba jugador2 con el valor que necesita el estado actual
                recargaBombaJugador2 = ataque.getTiempoRecarga();
            }
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else if (tipo == TipoAtaque.ESPECIAL) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (esJugador1) {
                // actualiza recarga especial jugador1 con el valor que necesita el estado actual
                recargaEspecialJugador1 = ataque.getTiempoRecarga();
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } else {
                // actualiza recarga especial jugador2 con el valor que necesita el estado actual
                recargaEspecialJugador2 = ataque.getTiempoRecarga();
            }
        }
        // devuelve este resultado a quien llamo al metodo
        return true;
    }

    // este metodo bloquear jugador1 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void bloquearJugador1() {
        // actualiza tiempo sin accion jugador1 con el valor que necesita el estado actual
        tiempoSinAccionJugador1 = 0;
    }

    // este metodo bloquear jugador2 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void bloquearJugador2() {
        // actualiza tiempo sin accion jugador2 con el valor que necesita el estado actual
        tiempoSinAccionJugador2 = 0;
    }

    // este metodo get tiempo ronda concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getTiempoRonda() {
        // devuelve este resultado a quien llamo al metodo
        return tiempoRonda;
    }

    // este metodo get rondas jugador1 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public int getRondasJugador1() {
        // devuelve este resultado a quien llamo al metodo
        return rondasJugador1;
    }

    // este metodo get rondas jugador2 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public int getRondasJugador2() {
        // devuelve este resultado a quien llamo al metodo
        return rondasJugador2;
    }

    // este metodo get jugador1 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Personaje getJugador1() {
        // devuelve este resultado a quien llamo al metodo
        return jugador1;
    }

    // este metodo get jugador2 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public Personaje getJugador2() {
        // devuelve este resultado a quien llamo al metodo
        return jugador2;
    }

    // este metodo termino ronda concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean terminoRonda() {
        // devuelve este resultado a quien llamo al metodo
        return tiempoRonda <= 0 || !jugador1.estaVivo() || !jugador2.estaVivo();
    }

    // este metodo ronda esta terminada concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean rondaEstaTerminada() {
        // devuelve este resultado a quien llamo al metodo
        return terminoRonda();
    }

    // este metodo compara la vida o supervivencia para determinar quien gano
    public int obtenerGanadorRonda() {
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (jugador1.estaVivo() && jugador2.estaVivo()) {
            // comprueba esta condicion para decidir si ejecuta el bloque que sigue
            if (jugador1.getVida() > jugador2.getVida()) {
                // devuelve este resultado a quien llamo al metodo
                return 1;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } else if (jugador2.getVida() > jugador1.getVida()) {
                // devuelve este resultado a quien llamo al metodo
                return 2;
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            } else {
                // devuelve este resultado a quien llamo al metodo
                return 0;
            }
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (jugador1.estaVivo()) {
            // devuelve este resultado a quien llamo al metodo
            return 1;
        }
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (jugador2.estaVivo()) {
            // devuelve este resultado a quien llamo al metodo
            return 2;
        }
        // devuelve este resultado a quien llamo al metodo
        return 0;
    }

    // este metodo suma una victoria de ronda al jugador que corresponda
    public void registrarGanadorRonda() {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        int ganador = obtenerGanadorRonda();
        // comprueba esta condicion para decidir si ejecuta el bloque que sigue
        if (ganador == 1) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            rondasJugador1++;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        } else if (ganador == 2) {
            // este paso prepara o actualiza el estado que necesita el siguiente paso
            rondasJugador2++;
        }
    }

    // este metodo combate terminado concentra una operacion de esta clase y recibe el control desde quien lo llama
    public boolean combateTerminado() {
        // devuelve este resultado a quien llamo al metodo
        return rondasJugador1 >= 2 || rondasJugador2 >= 2;
    }

    // este metodo get recarga bomba jugador1 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getRecargaBombaJugador1() {
        // devuelve este resultado a quien llamo al metodo
        return recargaBombaJugador1;
    }

    // este metodo get recarga bomba jugador2 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getRecargaBombaJugador2() {
        // devuelve este resultado a quien llamo al metodo
        return recargaBombaJugador2;
    }

    // este metodo get recarga especial jugador1 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getRecargaEspecialJugador1() {
        // devuelve este resultado a quien llamo al metodo
        return recargaEspecialJugador1;
    }

    // este metodo get recarga especial jugador2 concentra una operacion de esta clase y recibe el control desde quien lo llama
    public float getRecargaEspecialJugador2() {
        // devuelve este resultado a quien llamo al metodo
        return recargaEspecialJugador2;
    }

    // este metodo comprueba la distancia horizontal y vertical para permitir el impacto
    private boolean estaEnAlcance(Personaje atacante, Personaje defensor, float alcance) {
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float centroAtacante = atacante.getX() + 150;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float centroDefensor = defensor.getX() + 150;
        // este paso prepara o actualiza el estado que necesita el siguiente paso
        float distancia = Math.abs(centroAtacante - centroDefensor);
        // devuelve este resultado a quien llamo al metodo
        return distancia <= alcance + 0.01f && Math.abs(atacante.getY() - defensor.getY()) < 90;
    }

    // este metodo escribir concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void escribir(DataOutputStream datos) throws IOException {
        // realiza esta operacion auxiliar para completar el paso actual
        jugador1.escribir(datos);
        // realiza esta operacion auxiliar para completar el paso actual
        jugador2.escribir(datos);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeFloat(tiempoRonda);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeInt(rondasJugador1);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeInt(rondasJugador2);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeFloat(recargaBombaJugador1);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeFloat(recargaBombaJugador2);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeFloat(recargaEspecialJugador1);
        // realiza esta operacion auxiliar para completar el paso actual
        datos.writeFloat(recargaEspecialJugador2);
    }

    // este metodo leer concentra una operacion de esta clase y recibe el control desde quien lo llama
    public void leer(DataInputStream datos) throws IOException {
        // realiza esta operacion auxiliar para completar el paso actual
        jugador1.leer(datos);
        // realiza esta operacion auxiliar para completar el paso actual
        jugador2.leer(datos);
        // actualiza tiempo ronda con el valor que necesita el estado actual
        tiempoRonda = datos.readFloat();
        // actualiza rondas jugador1 con el valor que necesita el estado actual
        rondasJugador1 = datos.readInt();
        // actualiza rondas jugador2 con el valor que necesita el estado actual
        rondasJugador2 = datos.readInt();
        // actualiza recarga bomba jugador1 con el valor que necesita el estado actual
        recargaBombaJugador1 = datos.readFloat();
        // actualiza recarga bomba jugador2 con el valor que necesita el estado actual
        recargaBombaJugador2 = datos.readFloat();
        // actualiza recarga especial jugador1 con el valor que necesita el estado actual
        recargaEspecialJugador1 = datos.readFloat();
        // actualiza recarga especial jugador2 con el valor que necesita el estado actual
        recargaEspecialJugador2 = datos.readFloat();
    }
}
