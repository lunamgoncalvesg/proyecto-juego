# Changelog

Todos los cambios importantes del proyecto se registran en este archivo. Esta versión continúa el juego documentado en la versión anterior.

## [Unreleased]

### Added

* Incorporación del modo de juego en dos computadoras mediante una conexión cliente-servidor.
* Incorporación del módulo `server`, que permite iniciar un servidor dedicado para una partida de hasta dos personas.
* Incorporación de `iniciar-servidor.bat` para preparar y ejecutar el servidor en Windows.
* Incorporación de la pantalla para ingresar la dirección IP o el nombre de host del servidor.
* Incorporación de estados de conexión que informan si el cliente está conectando, esperando al rival o listo para continuar.
* Incorporación de selección de personaje y confirmación de inicio sincronizadas entre los dos clientes.
* Incorporación de la simulación de combate en el servidor y del envío de estados de partida a los clientes.
* Incorporación de mensajes de conexión, espera, latido, error y desconexión para administrar la sesión de red.
* Incorporación de asignación de puestos: el primer cliente es el jugador 1 y el segundo, el jugador 2.
* Incorporación de compatibilidad con ambos esquemas de teclado para la persona que juega en red.
* Incorporación de instrucciones de conexión, requisitos de red, puerto TCP y ejecución del servidor en el README.
* Incorporación de una tabla de controles que diferencia la partida local del modo en red.
* Incorporación de documentación de los paquetes de red y servidor en la guía para leer el código.

### Changed

* Activación de la opción «EN DOS COMPUTADORAS» del menú para iniciar una conexión al servidor.
* Actualización de la preparación de partida para permitir ingresar al modo de red, conectarse y elegir personaje.
* Actualización de la pantalla de instrucciones para esperar a que ambos jugadores confirmen antes de comenzar el combate en red.
* Actualización de la pantalla de combate para transmitir controles, recibir el estado simulado por el servidor y mostrar el personaje de cada jugador.
* Actualización del flujo de salida del combate para cerrar la sesión de red y volver al menú si el rival se desconecta.
* Actualización del README con el nuevo modo de juego, la ejecución del servidor y la estructura del módulo de red.

### Fixed

* Manejo de fallos de conexión y tiempos de espera con mensajes de estado para que el cliente pueda volver a intentar conectarse.
* Liberación de los lugares del servidor cuando un jugador se desconecta, para permitir nuevas conexiones.
* Validación de los mensajes y estados recibidos por red antes de aplicarlos al combate.
* Control de colas de entrada y salida para mantener al día los controles y los estados de combate enviados por la conexión.

