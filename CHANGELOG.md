# Changelog

## 1.0.0 — 2026-10-08

### Agregado

- Menú con opciones para jugar, abrir GitHub y salir, con teclado o mouse.
- Partidas locales y conexión TCP entre dos computadoras.
- Selección de equilibrado, rápido y fuerte, con imágenes y nueve poses por personaje.
- Pantalla de instrucciones antes del combate y cuenta regresiva.
- Movimiento, salto, bloqueo y ataques suave, bomba y especial.
- Barras de vida y estamina, temporizador de 60 segundos y victoria al ganar dos rondas.
- Círculos de recarga con las teclas de cada ataque.
- Resultado personal de victoria o derrota y regreso al menú.
- Fondos GIF de día y noche según el reloj de la computadora.
- Presentación de pelea sin texto y créditos con desplazamiento, datos del curso y música.
- Carga de recursos desde una carpeta `assets` junto al JAR.

### Ajustado

- Código reorganizado en métodos más cortos para movimiento, ataques, selección y comunicación, manteniendo el comportamiento de la versión final.
- Acciones de teclado con nombres explícitos, condiciones más legibles y guía de lectura del código en el README.

- Presentación con una historia breve y sin texto: el equilibrado entrena, la rápida lo desafía y el fuerte reta a ambos.

- Interfaz con botones más simples y colores amarillos y AMARILLOs.
- El anfitrión controla J1 a la izquierda y el invitado J2 a la derecha.
- En red, ambos esquemas de teclado controlan solo al personaje local; en modo local permanecen separados.
- Daño, velocidad y recargas según el personaje: rápido 2/10 s, equilibrado 3/15 s y fuerte 4/20 s para bomba/especial.
- Imágenes proporcionales, transparencia y orientación del segundo jugador.
- Sincronización de selección, ataques, vida, recargas, rondas y resultados.

### Corregido

- Colisiones para evitar que los personajes se atraviesen y alcance de los ataques en el límite de contacto.
- Manejo de desconexiones y regreso al menú.
- Recorte de las poses en las hojas de personajes.

### Limpieza

- Eliminadas las sombras circulares debajo de los personajes en combate y en la presentación.
- Eliminado el destello amarillo superpuesto en el video inicial.
- Retirado el antiguo video de créditos, marcadores de carpetas vacías y referencias a recursos eliminados.
- Actualizados el README y este registro para describir la versión actual.
