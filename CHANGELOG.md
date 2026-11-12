# Changelog

Todos los cambios importantes del proyecto serán registrados en este archivo.

## [Unreleased]

### Added

* Implementación de un juego de pelea 2D para dos jugadores, inspirado en Street Fighter, utilizando Java y LibGDX.
* Implementación de la clase `Personaje` para administrar los atributos y comportamientos de cada luchador.
* Implementación de los tres tipos de personaje: equilibrado (gato), rápido (tortuga) y fuerte (hombre).
* Implementación de atributos diferenciados para cada personaje, incluyendo velocidad, daño y tiempos de recarga.
* Implementación del sistema de vida y estamina de los personajes.
* Implementación de los ataques suave, bomba y especial.
* Implementación del consumo de estamina según el tipo de ataque.
* Implementación del sistema de recarga independiente para los ataques bomba y especial.
* Implementación de la recuperación de estamina cuando los personajes no realizan acciones de combate.
* Implementación del movimiento horizontal de los personajes.
* Implementación del sistema de salto y actualización de su movimiento.
* Implementación del sistema de bloqueo y defensa.
* Implementación de controles independientes para ambos jugadores.
* Implementación de la detección de pulsaciones nuevas para evitar la repetición involuntaria de ataques.
* Implementación del sistema de colisiones entre los personajes.
* Implementación de límites de movimiento dentro de la arena.
* Implementación de un sistema de alcance para determinar si los ataques impactan al oponente.
* Implementación del sistema de rondas con una duración máxima de 60 segundos.
* Implementación del sistema de puntuación por rondas, donde gana el primer jugador en conseguir dos victorias.
* Implementación del reinicio de la vida y de los tiempos de recarga al comenzar cada ronda.
* Implementación del sistema de resolución de empates.
* Incorporación de una cuenta regresiva antes del inicio de cada ronda.
* Incorporación de una pantalla de combate con los personajes y sus animaciones.
* Incorporación de barras de vida y estamina para ambos jugadores.
* Incorporación de indicadores visuales del tiempo de recarga de los ataques.
* Incorporación de las teclas correspondientes a cada ataque en los indicadores de combate.
* Incorporación de avisos visuales durante el combate y al finalizar las rondas.
* Implementación de una pantalla de portada para iniciar el juego.
* Implementación de una presentación cinematográfica que introduce la historia de los personajes.
* Incorporación de un menú principal con las opciones de jugar, acceder al repositorio de GitHub y salir del juego.
* Implementación de una pantalla de selección de modo de juego.
* Incorporación de la opción de partida local para dos jugadores en una misma computadora.
* Incorporación de la opción visual de juego en dos computadoras.
* Implementación de una pantalla de selección de personajes para ambos jugadores.
* Incorporación de retratos, nombres y descripciones de los personajes en la pantalla de selección.
* Implementación de una pantalla de instrucciones con los controles de ambos jugadores antes de comenzar el combate.
* Implementación de una pantalla de resultados con el ganador y el marcador final.
* Incorporación de animaciones de victoria y derrota en la pantalla de resultados.
* Implementación de una pantalla de créditos con desplazamiento automático.
* Incorporación de música durante la reproducción de los créditos.
* Incorporación del mensaje de despedida al finalizar el juego.
* Implementación de la posibilidad de omitir la presentación cinematográfica y los créditos mediante el teclado.
* Implementación de la gestión de pantallas para controlar las transiciones y liberar los recursos utilizados.
* Implementación de un sistema de carga de recursos gráficos externos y recursos incluidos en el proyecto.
* Implementación de fondos animados diferenciados para el día y la noche.
* Implementación de la selección automática del fondo según la hora local de la computadora.
* Implementación de la actualización periódica del fondo para adaptarlo a los cambios de horario.
* Implementación de un sistema de lectura y reproducción de archivos GIF animados.
* Incorporación de recursos gráficos en pixel art para los tres personajes.
* Incorporación de sprites individuales para las distintas poses de los personajes: quieto, caminar, salto, bloqueo, golpe, patada, especial, victoria y derrota.
* Incorporación de hojas de sprites de respaldo para los personajes.
* Incorporación de una portada gráfica para el juego.
* Incorporación de recursos audiovisuales para la presentación cinematográfica.
* Incorporación de archivos de texto para configurar el contenido de los créditos.
* Incorporación de compatibilidad con archivos de música MP3, OGG y WAV para los créditos.
* Implementación de una fuente y una estética visual de estilo retro.
* Incorporación de una paleta de colores para unificar la apariencia de las distintas pantallas.
* Incorporación de paneles, botones y elementos visuales reutilizables.
* Incorporación de documentación sobre la ejecución, los controles, los personajes, los recursos y la estructura del proyecto.
* Incorporación de instrucciones para ejecutar el juego mediante el archivo JAR generado.

### Changed

* Modificación de la estructura inicial del proyecto para organizar el desarrollo del juego de pelea.
* Organización del código fuente en paquetes para personajes, combate, controles y elementos visuales.
* Separación de la lógica del combate de la representación gráfica de la pantalla.
* Separación de la lectura de controles de la ejecución de las acciones de los personajes.
* Centralización de la administración de las pantallas y transiciones en la clase `Main`.
* Reorganización de la interfaz para incorporar un menú principal y distintas pantallas de navegación.
* Ampliación de la experiencia inicial mediante una portada y una presentación cinematográfica antes del menú.
* Modificación del flujo de inicio para permitir seleccionar el modo de juego antes de preparar una partida.
* Modificación del flujo de preparación para que cada jugador pueda elegir su propio personaje.
* Incorporación de una pantalla intermedia de instrucciones antes de comenzar el combate.
* Modificación de la pantalla de combate para representar los estados y movimientos de los personajes mediante sprites.
* Actualización de la presentación de las barras de vida y estamina.
* Actualización de los indicadores de ataques para mostrar sus tiempos de recarga.
* Modificación del sistema visual para utilizar una estética arcade de alto contraste y tipografía pixelada.
* Reemplazo de la representación gráfica inicial por recursos individuales para cada pose de los personajes.
* Actualización del sistema de carga de sprites para priorizar los archivos de poses individuales y utilizar las hojas de sprites como respaldo.
* Ajuste del recorte de las imágenes para eliminar los márgenes transparentes y evitar la visualización de píxeles de otras poses.
* Ajuste de la orientación de los personajes para que el segundo jugador mire hacia el primero.
* Ajuste del tamaño y la posición de los personajes dentro de la arena.
* Ajuste de los límites de movimiento de los personajes.
* Actualización del sistema de colisiones para evitar que los personajes se atraviesen.
* Modificación del sistema de movimiento para actualizar las posiciones en función del tiempo transcurrido entre cuadros.
* Actualización de los fondos para utilizar animaciones diferentes según el horario.
* Modificación del sistema de carga de recursos para permitir reemplazar los archivos gráficos y audiovisuales sin recompilar el proyecto.
* Actualización de la gestión de recursos para liberar texturas, música y elementos audiovisuales al cambiar de pantalla o finalizar el juego.
* Actualización del flujo de salida para mostrar los créditos antes de cerrar la aplicación.
* Actualización del README con la descripción del juego terminado, los controles, los personajes, los recursos y las instrucciones de ejecución.
* Actualización de la documentación técnica con una descripción de las principales clases y responsabilidades del proyecto.

### Fixed

* Corrección de la posición inicial de los personajes para evitar que aparezcan superpuestos.
* Corrección de la posición del segundo personaje dentro de la pantalla.
* Corrección de los límites de movimiento para evitar que los personajes salgan de la arena.
* Corrección de problemas de colisión entre los personajes.
* Corrección de la detección de alcance de los ataques.
* Corrección de problemas relacionados con el constructor de `Personaje`.
* Corrección del funcionamiento del bloqueo y la defensa.
* Corrección del consumo de estamina al ejecutar ataques.
* Corrección del funcionamiento de los tiempos de recarga de los ataques.
* Corrección de la recuperación de estamina durante los períodos de inactividad.
* Corrección del reinicio de los atributos de los personajes al comenzar una nueva ronda.
* Corrección de la actualización del temporizador de las rondas.
* Corrección de la visualización de las barras de vida, estamina y temporizador.
* Corrección de la visualización de los indicadores de recarga.
* Corrección de la orientación y representación gráfica de los personajes durante el combate.
* Corrección de problemas de visualización ocasionados por el muestreo de píxeles de celdas vecinas en las hojas de sprites.
* Corrección del recorte de transparencia de los recursos gráficos.
* Corrección de la liberación de recursos gráficos y audiovisuales al cambiar de pantalla.
* Corrección del comportamiento de la presentación cinematográfica cuando no se puede cargar o reproducir el video.
* Corrección de la gestión de la reproducción y liberación de la música de los créditos.
* Corrección de la transición entre el combate, la pantalla de resultados y el menú principal.