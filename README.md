# Street Fighter Ponele

Juego de pelea 2D para dos personas, desarrollado con Java y LibGDX.

- Colegio: Ing. E. Latzina.
- Materia: Programación sobre Redes.
- Profesor: Morales Diego.
- Curso: 6°2, T.M, C.

## Integrantes

- Domínguez Victoria.
- Galíndez Sofía.
- Goncalves Luna.
- Iannopollo Angeline.
- Seiberth Valentín.

## Ejecutar

Requiere Java 17 o superior. Abrir el JAR o ejecutar:

```powershell
java -jar lwjgl3/build/libs/Proyecto-Juego-1.0.0.jar
```

Al abrir, se muestra la portada arcade de `assets/ui/portada.png`; ENTER o cualquier clic inicia la presentación del gato que llega al templo, se enfrenta con la tortuga y aprende a entrenar junto a ella. Al terminar, aparece el menú. JUGAR abre la selección de modo; también se puede abrir el GitHub o salir. La presentación se puede omitir con ENTER, ESPACIO o ESC.

Elegir el modo, seleccionar personajes, revisar los controles y presionar JUGAR. Gana quien consigue dos rondas. Cada ronda dura hasta 60 segundos; los empates se repiten. Al terminar se muestra el resultado y se vuelve al menú. ESC durante el combate abandona la partida.

Al salir, incluso al cerrar la ventana, aparecen los créditos con música y el mensaje «Gracias por jugar». ENTER, ESPACIO o ESC permiten omitirlos.

## Controles

| Acción | Jugador 1 en local | Jugador 2 en local |
| --- | --- | --- |
| Moverse | A / D | Flechas izquierda / derecha |
| Saltar | W | Flecha arriba |
| Defender | S | Flecha abajo |
| Golpe suave | E | L |
| Bomba | ESPACIO | M |
| Especial | R | P |

En una misma computadora, cada jugador usa su columna. En red, cada persona puede usar cualquiera de las dos columnas o combinarlas: todas sus teclas controlan únicamente a su personaje. El anfitrión es J1, a la izquierda, y el invitado J2, a la derecha.

## Jugar por Internet o en una red local

La conexión es directa entre las dos computadoras (TCP, puerto 54555). No requiere que estén en la misma red, pero el anfitrión debe poder recibir conexiones desde Internet:

1. Copiar el mismo JAR a ambas computadoras e instalar Java 17 o superior.
2. En el router del anfitrión, reenviar el puerto **TCP 54555** a la IPv4 local de la computadora donde corre el juego. No hace falta abrir UDP.
3. Permitir Java en el firewall del anfitrión y dejar el juego en la pantalla de espera.
4. El anfitrión comparte su IPv4 pública o un nombre DDNS. La IP local que muestra el juego sirve solo para conexiones en la misma red.
5. La otra persona entra en **JUGAR → EN DOS COMPUTADORAS → UNIRME A UNA PARTIDA** e ingresa esa IPv4 pública o dominio, sin `http://` ni `:54555`.
6. Ambos eligen su personaje y presionan **JUGAR**. El anfitrión debe iniciar primero y permanecer esperando.

Si el proveedor de Internet usa CGNAT o bloquea conexiones entrantes, el reenvío del router no alcanza: hace falta pedir una IPv4 pública al proveedor o usar una VPN de juegos que permita conexiones directas. Para dos instancias en la misma computadora, usar `127.0.0.1`; para una red local, usar la IPv4 local del anfitrión. Si alguien abandona o se corta la conexión, el otro vuelve al menú con un aviso. El proyecto no incluye salas públicas, emparejamiento ni un servidor alojado: el anfitrión comparte la dirección y recibe la conexión.

## Personajes y ataques

| Personaje | Velocidad | Daño | Recarga bomba | Recarga especial |
| --- | ---: | ---: | ---: | ---: |
| Gato guerrero | 300 | ×1 | 3 s | 15 s |
| Tortuga luchadora | 400 | ×0,8 | 2 s | 10 s |
| Hombre fuerte | 220 | ×1,4 | 4 s | 20 s |

El golpe suave consume 1 de estamina, la bomba 5 y el especial 10. Sus daños base son 5, 10 y 15. El bloqueo evita el daño. La estamina se recupera a razón de 1 por segundo sin atacar ni bloquear. La vida se reinicia al comenzar cada ronda.

Las barras superiores muestran vida y estamina. Los círculos inferiores se rellenan durante la recarga de los ataques y muestran las teclas correspondientes.

## Recursos

Los personajes están en `assets/personajes/equilibrado` (guerrero felino), `assets/personajes/rapido` (tortuga luchadora) y `assets/personajes/fuerte` (hombre fuerte). Cada pose se guarda en su propio PNG para evitar que se mezclen celdas vecinas. Los archivos `arcade.png` se conservan como hojas de respaldo con este orden 3×3:

| Fila | Columna 1 | Columna 2 | Columna 3 |
| --- | --- | --- | --- |
| 1 | Quieto | Caminar | Saltar |
| 2 | Defender | Golpear | Patear |
| 3 | Especial | Victoria | Derrota |

Las imágenes de pose deben mirar hacia la derecha; el juego invierte las del jugador 2 y ajusta el tamaño proporcionalmente. Si falta una pose individual, se usa la hoja `arcade.png`; si falta también esa pose, se usa `idle.png`.

- `assets/fondo_dia.gif`: fondo de 07:00 a 18:59.
- `assets/fondo_noche.gif`: fondo de 19:00 a 06:59.
- `assets/ui/portada.png`: imagen estática de inicio, dibujada fuera del juego.
- `assets/cinematicas/presentacion.webm`: secuencia de escenas con frases que cuentan la historia del gato, el maestro y la tortuga.
- `assets/cinematicas/creditos.txt`: nombres y textos de los créditos.
- `assets/cinematicas/musica_creditos.mp3`: música del cierre. También se admiten OGG y WAV con el mismo nombre base.

El fondo conserva su archivo y presentación originales. Se elige según el reloj de cada computadora y se revisa cada 30 segundos. Los GIF se reproducen en bucle y admiten hasta 300 cuadros y 64 millones de píxeles totales.

Para cambiar recursos sin recompilar, colocar una carpeta `assets` junto al JAR con las mismas rutas. Tiene prioridad sobre los archivos incluidos. Reiniciar el juego después de modificarlos.

## Desarrollo

```powershell
./gradlew.bat :lwjgl3:run
./gradlew.bat :core:compileJava :lwjgl3:jar
```

El código está en `core/src/main/java/com/proyecto/juego`. El lanzador de escritorio está en `lwjgl3/src/main/java`. El JAR se genera en `lwjgl3/build/libs`.

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
| `Main.java` | Inicia el juego y cambia de pantalla. |
| `PantallaCombate.java` | Coordina la partida, la dibuja y sincroniza la información en red. |

LibGDX llama a `render(float delta)` para actualizar lo que aparece en pantalla. `delta` indica cuántos segundos pasaron desde la actualización anterior: al multiplicar la velocidad por ese tiempo, el movimiento mantiene su ritmo aunque cambien los cuadros por segundo. Los métodos `actualizar...` calculan el estado del juego; los métodos `dibujar...` muestran ese estado.

Las pantallas del menú, selección, instrucciones, resultado y créditos tienen sus propias clases. `PantallaBase` reúne las herramientas que comparten, como dibujar texto, paneles y botones.

La conexión está en `red/SesionRed.java`: abre el socket y envía o recibe mensajes en segundo plano. El anfitrión calcula el combate y el invitado recibe su estado. En `Controles`, cada acción tiene un número distinto para poder combinar varias teclas en un único mensaje; esos números tienen nombres como `SALTAR`, `DEFENDER` y `BOMBA`.

Las clases de `visual` cargan imágenes, reproducen el fondo y definen colores. La lectura interna de GIF y los hilos de red son las partes más técnicas; se pueden estudiar después de entender el combate local.
