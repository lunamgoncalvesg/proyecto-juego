# Street Fighter Ponele

Juego de pelea 2D para dos personas, desarrollado con Java y LibGDX. Se puede jugar en una computadora o en dos computadoras conectadas a un servidor.

- Colegio: Ing. E. Latzina.
- Profesor: Morales Diego.
- Curso: 6°2, T.M, C.

## Integrantes

- Domínguez Victoria.
- Galíndez Sofía.
- Goncalves Luna.
- Iannopollo Angeline.
- Seiberth Valentín.

## Ejecutar el juego

Requiere Java 17 o superior. Abrir el JAR o ejecutar desde PowerShell:

```powershell
java -jar Proyecto-Juego-1.0.1.jar
```

También se puede ejecutar el artefacto de Gradle:

```powershell
java -jar lwjgl3/build/libs/Proyecto-Juego-1.0.1.jar
```

Al abrir, se muestra la portada de `assets/ui/portada.png`; ENTER o cualquier clic inicia la presentación del gato que llega al templo, se enfrenta con la tortuga y aprende a entrenar junto a ella. Al terminar, aparece el menú. La presentación se puede omitir con ENTER, ESPACIO o ESC.

JUGAR abre la selección de modo. Después de una partida aparece el resultado y se vuelve al menú. ESC durante el combate abandona la partida. Al salir del juego aparecen los créditos con música y el mensaje «Gracias por jugar»; ENTER, ESPACIO o ESC permiten omitirlos.

## Partida local

En el menú de modo, elegir «EN ESTA COMPUTADORA». Cada persona selecciona su personaje y luego se muestra la pantalla de controles antes de comenzar.

## Partida en red

Se necesitan dos computadoras que ejecuten el juego y un servidor. El servidor puede correr en una de esas dos computadoras o en una tercera; atiende hasta dos jugadores en el puerto TCP `54555`.

1. En la computadora que hará de servidor, ejecutar `iniciar-servidor.bat`. El script prepara y arranca el servidor; requiere Java y puede necesitar descargar dependencias de Gradle la primera vez.
2. Permitir conexiones TCP entrantes al puerto `54555` en el firewall de esa computadora. Si los jugadores están fuera de la red local, configurar también el reenvío de ese puerto en el router.
3. En ambas computadoras, abrir el juego, elegir JUGAR y «EN DOS COMPUTADORAS».
4. Escribir la dirección IP o el nombre de host del servidor y presionar ENTER o «CONECTAR». Cuando aparezca «Rival conectado», presionar «CONTINUAR».
5. Cada persona elige su personaje y presiona CONTINUAR. En la pantalla de instrucciones, ambas deben presionar JUGAR; el combate comienza cuando las dos estén listas.

El primer cliente conectado juega como jugador 1, a la izquierda; el segundo, como jugador 2, a la derecha. El servidor simula la partida y envía el estado del combate a ambos clientes. Si un jugador se desconecta, la partida termina y el rival vuelve al menú.

## Controles

| Acción | Jugador 1 en local | Jugador 2 en local | Cualquiera en red |
| --- | --- | --- | --- |
| Moverse | A / D | Flechas izquierda / derecha | A / D o flechas izquierda / derecha |
| Saltar | W | Flecha arriba | W o flecha arriba |
| Defender | S | Flecha abajo | S o flecha abajo |
| Golpe suave | E | L | E o L |
| Bomba | ESPACIO | M | ESPACIO o M |
| Especial | R | P | R o P |

## Personajes y ataques

| Personaje | Velocidad | Daño | Recarga bomba | Recarga especial |
| --- | ---: | ---: | ---: | ---: |
| Gato guerrero | 300 | ×1 | 3 s | 15 s |
| Tortuga luchadora | 400 | ×0,8 | 2 s | 10 s |
| Hombre fuerte | 220 | ×1,4 | 4 s | 20 s |

El golpe suave consume 1 de estamina, la bomba 5 y el especial 10. Sus daños base son 5, 10 y 15. El bloqueo evita el daño. La estamina se recupera a razón de 1 por segundo sin atacar ni bloquear. La vida se reinicia al comenzar cada ronda.

Las barras superiores muestran vida y estamina. Los círculos inferiores se rellenan durante la recarga de los ataques y muestran las teclas correspondientes. Gana quien consigue dos rondas; cada ronda dura hasta 60 segundos y los empates se repiten.

## Recursos

Los personajes están en `assets/personajes/equilibrado` (gato), `assets/personajes/rapido` (tortuga) y `assets/personajes/fuerte` (hombre fuerte). Cada pose se guarda en su propio PNG para evitar que se mezclen celdas vecinas. Los archivos `arcade.png` se conservan como hojas de respaldo con este orden 3×3:

| Fila | Columna 1 | Columna 2 | Columna 3 |
| --- | --- | --- | --- |
| 1 | Quieto | Caminar | Saltar |
| 2 | Defender | Golpear | Patear |
| 3 | Especial | Victoria | Derrota |

Las imágenes de pose deben mirar hacia la derecha; el juego invierte las del jugador 2 y ajusta el tamaño proporcionalmente. Si falta una pose individual, se usa la hoja `arcade.png`; si falta también esa pose, se usa `idle.png`.

- `assets/fondo_dia.gif`: fondo de 07:00 a 18:59.
- `assets/fondo_noche.gif`: fondo de 19:00 a 06:59.
- `assets/ui/portada.png`: imagen estática de inicio.
- `assets/cinematicas/presentacion.webm`: secuencia de escenas con frases que cuentan la historia del gato, el maestro y la tortuga.
- `assets/cinematicas/creditos.txt`: nombres y textos de los créditos.
- `assets/cinematicas/musica_creditos.mp3`: música del cierre. También se admiten OGG y WAV con el mismo nombre base.

El fondo se elige según el reloj de cada computadora y se revisa cada 30 segundos. Los GIF se reproducen en bucle y admiten hasta 300 cuadros y 64 millones de píxeles totales.

Para cambiar recursos sin recompilar, colocar una carpeta `assets` junto al JAR con las mismas rutas. Tiene prioridad sobre los archivos incluidos. Reiniciar el juego después de modificarlos.

## Desarrollo

El código del juego está en `core/src/main/java/com/proyecto/juego`; el cliente de red, en `core/src/main/java/com/proyecto/juego/red`; el servidor, en `server/src/main/java/com/proyecto/juego/server`; y el lanzador de escritorio, en `lwjgl3/src/main/java`. El JAR se genera en `lwjgl3/build/libs`.

Para compilar el juego y el servidor:

```powershell
./gradlew.bat :core:compileJava :lwjgl3:jar :server:installDist
```

[Repositorio](https://github.com/lunamgoncalvesg/proyecto-juego) · [Wiki](https://github.com/lunamgoncalvesg/proyecto-juego/wiki)

## Por dónde empezar a leer el código

Una clase agrupa los datos y las acciones de una parte del juego. Un método es una acción con nombre, como `saltar()` o `iniciarRonda()`. Para entender el proyecto, conviene leer en este orden:

| Archivo | Qué hace |
| --- | --- |
| `personajes/Personaje.java` | Guarda posición, vida y estamina; mueve, salta y recibe daño. |
| `combate/Ataque.java` | Describe el costo, daño y recarga de un ataque. |
| `combate/Combate.java` | Aplica ataques, recargas y reglas de las rondas. |
| `controles/Controles.java` | Lee las teclas y determina qué acciones pide cada jugador. |
| `combate/ControladorCombate.java` | Usa esas acciones para mover y atacar con los personajes. |
| `red/SesionRed.java` y `red/ProtocoloRed.java` | Administran la conexión del cliente y los mensajes de red. |
| `server/ServidorCentral.java` y `server/Partida.java` | Aceptan clientes y simulan las partidas en el servidor. |
| `Main.java` | Inicia el juego y cambia de pantalla. |
| `PantallaCombate.java` | Coordina y dibuja la partida. |

LibGDX llama a `render(float delta)` para actualizar lo que aparece en pantalla. `delta` indica cuántos segundos pasaron desde la actualización anterior: al multiplicar la velocidad por ese tiempo, el movimiento mantiene su ritmo aunque cambien los cuadros por segundo. Los métodos `actualizar...` calculan el estado del juego; los métodos `dibujar...` muestran ese estado.

Las pantallas del menú, selección, instrucciones, resultado y créditos tienen sus propias clases. `PantallaBase` reúne las herramientas que comparten, como dibujar texto, paneles y botones.

Las clases de `visual` cargan imágenes, reproducen el fondo y definen colores. La lectura interna de GIF es una de las partes más técnicas; se puede estudiar después de entender el combate local.
