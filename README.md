# Proyecto de juego 2D - 6°2° Computación T.M

## Integrantes

* Domínguez Victoria
* Galíndez Sofía
* Goncalves Luna
* Iannopollo Angeline
* Seiberth Valentín

## Descripción

Proyecto de desarrollo de un juego 2D multijugador realizado como proyecto final de la materia de Programación sobre Redes (código) articulado con Desarrollo de Sistemas (documentación).

La propuesta seleccionada y aprobada para el proyecto es un **juego de pelea 2D inspirado en Street Fighter**, con combates entre dos jugadores, diferentes tipos de personajes, ataques, defensa, movimiento y rondas.

El proyecto se desarrolla de manera incremental, comenzando por la implementación de las mecánicas principales del combate y continuando posteriormente con la incorporación de las pantallas, gráficos, sonidos, efectos y demás elementos del juego.

## Tecnologías principales

**Java** con **LibGDX** y **Git/GitHub**

### Plataformas objetivo

* Escritorio

## Wiki

La documentación del proyecto y el desarrollo de la propuesta se encuentran en la [Wiki del proyecto](../../wiki).

La propuesta de juego aprobada y su planificación se encuentran documentadas en la Wiki.

## Estado actual

**Desarrollo de las mecánicas principales del combate.**

El proyecto base de LibGDX fue generado y probado correctamente en la plataforma de escritorio.

Actualmente se encuentran implementadas distintas mecánicas del combate, entre ellas:

* Movimiento horizontal de los personajes.
* Salto.
* Defensa.
* Ataque suave.
* Ataque bomba.
* Ataque especial.
* Sistema de vida.
* Regeneración de vida.
* Recarga de ataques.
* Temporizador de ronda.
* Sistema de rondas.
* Barras de vida.
* Controles para ambos jugadores.
* Colisión entre los personajes.
* Representación visual de los jugadores.
* Primer recurso gráfico en estilo pixel art para los personajes.

Los personajes comienzan cada ronda con **100 puntos de vida** y las rondas tienen una duración máxima de **60 segundos**.

El proyecto continúa en desarrollo y posteriormente se incorporarán elementos como las diferentes poses de los personajes, efectos visuales, sonidos, pantallas del juego, cinematográficas y demás recursos necesarios para completar la propuesta.

## Características del juego

El juego está planteado como un combate de hasta **3 rondas** entre dos jugadores.

Cada personaje cuenta con diferentes características según el tipo seleccionado:

* **Equilibrado**
* **Rápido y débil**
* **Fuerte pero lento**

Los jugadores pueden:

* Caminar hacia la izquierda y derecha.
* Saltar.
* Defenderse.
* Realizar un ataque suave.
* Realizar un ataque bomba.
* Utilizar una habilidad especial.

Los ataques poseen diferentes costos de vida, daños y tiempos de recarga.

El ataque suave está disponible permanentemente, mientras que el ataque bomba posee una recarga de **3 segundos** y el ataque especial una recarga de **15 segundos**.

Además, un jugador puede recuperar **1 punto de vida por cada segundo sin realizar acciones**, sin superar los 100 puntos de vida.

## Controles

### Jugador 1

* Mover a la izquierda: `A` o `←`
* Mover a la derecha: `D` o `→`
* Saltar: `W` o `↑`
* Defender: `S` o `↓`
* Ataque fuerte / bomba: `ESPACIO` o `M`
* Ataque suave: `E` o `L`
* Ataque especial: `R` o `P`

### Jugador 2

En el modo para dos jugadores, los controles se dividen entre ambos jugadores para permitir que cada uno utilice su propio conjunto de teclas.

## Compilación y ejecución

### Requisitos

* Java 17 o superior.
* Gradle, incluido mediante el Gradle Wrapper del proyecto.

### Ejecución

1. Clonar este repositorio.
2. Abrir la carpeta del proyecto.
3. Ejecutar el proyecto utilizando el módulo de escritorio `lwjgl3`.
4. También es posible ejecutar el proyecto desde el entorno de desarrollo utilizado para trabajar con Java.

## Estructura principal del proyecto

El código principal del juego se encuentra dentro del módulo `core`.

```text
core/
└── src/
    └── main/
        └── java/
            └── com/
                └── proyecto/
                    └── juego/
```

Dentro del proyecto se organizan las diferentes partes del juego, incluyendo:

* `combate` — lógica del combate, ataques y control de las rondas.
* `personajes` — personajes y estados de los personajes.
* `controles` — manejo de los controles de los jugadores.

Los recursos gráficos utilizados por el juego se encuentran dentro de:

```text
assets/
```

Actualmente los recursos de los personajes se organizan dentro de:

```text
assets/personajes/
```

## Distribución

Además de poder clonar y ejecutar el proyecto desde el repositorio, se busca generar un archivo **JAR ejecutable** para poder distribuir el juego y ejecutarlo directamente en una computadora de escritorio.

## Estado del desarrollo

El proyecto se encuentra actualmente **en desarrollo**.

La propuesta de juego ya fue aprobada y se continúa trabajando de manera incremental sobre las mecánicas, gráficos, interfaz, sonidos y demás componentes necesarios para completar el videojuego.