# realmadrid_android_app

## notificaciones

* cuando se ABANDONE el museo -> gracias por visitar el museo!
* tras completar el kahoot --> kahoot completado!
* al iniciar sesion --> podemos variar entre dos notificaciones aleatorias (descuento para la siguiente visita / descuento en la tienda)
  
## interfaz

* onboarding con slides previo al login que explique que ofrece la app MUY BREVEMENTE (una imagen, dos lineas) y aumente la probabilidad de que el usuario acepte los permisos de camara, micro...
  * primer slide: ¡bienvenido al museo del real madrid! (que es la app, que va descubrir el visitante)
  * segundo slide: escanea los qrs del museo (info camisetas, trofeos, partidos, historia -- justificar el uso de la camara)
  * tercer slide: descubre el futuro, presente y pasado del club (calendario, fechas históricas...)
  * ...
* interfaz del cliente:
  * opciones:
     * registro (no debe dejar continuar si no se han rellenado todos los campos):
       * nombre
       * telefono
       * nacimiento (en funcion de la edad, debe saber con que registro se deben mostrar los textos con el usuario)
       * email
       * password
       * al crear una cuenta, debes aceptar los terminos y condiciones...
     * inicio de sesion (si no se han rellenado los campos debe solicitarlo):
        * email
        * password
     * invitado
   * quien este registrado, cada vez que inicie sesion, se enviara una notificacion indicando un descuento del 5/10 % (da igual que siempre se envie)
   
## accesibilidad

* MODO OSCURO para la APP AL COMPLETO (ACCESIBILIDAD PARA MEJOR VISUALIZACION)
* cambio de idioma (ingles/español)
* capacidad de poder valorar las salas (comentarios/estrellas)

## SENSORES

a rellenar

## FUNCIONALIDADES EXTRA

* calendario de eventos del real madrid donde el dia que haya partido, se marque en rojo y si se pincha, muestr informacion del partido, la hora, y si es copa no es copa...
* conexion con openstreetmap api (gratis) para poder indicar la direccion de donde se encuentra el museo/santiago bearnabeu en madrid
  
## SALAS
las formas de acceder a las salas (no es como tal (unity)) son dos:
* mapa inicial cualquiera en el que se indiquen las salas y clickando sobre una de ellas, nos lleva a esta en la app (forma indirecta)
* escanear qr real en el museo
  
### KAHOOT:
   * SOLAMENTE las respuestas van a aparecer en la pantalla del movil
   * permitir solicitar pistas (se puede introducir el chatbot de voiceflow)
   * permitir microfono para seleccionar la respuesta correcta
   * seleccionar una respuesta sacudiendo el movil (acelerometro)
     * introducir mensaje de advertencia -> si detecta movimiento brusco se bloquea la respuesta 1 segundo
   * si se acerca el movil al oido, la pregunta NO VISIBLE en el movil se lee en voz alta.
   * vibraciones consecutivas cuando queda poco tiempo para que termine el juego
   * vibraciones para indicar correcto/incorrecto:
    * doble vibracion: incorrecto
    * vibracion: correcto
   * flujo completo:
     * entra a la sala kahoot
     * aparece un qr
     * se abre la app para escanear el qr y se une a la partida
   * mi progueso: estadisticas del usuario (dia y fecha en la que realizado el kahoot "x" con puntuacion y total de preguntas)
      * solo revisable si es usuario registrado (no sirve para invitado)
      * añadir una grafica de evolucion (meter un grafico que represente la misma informacion (mpandroichart)
   * habria que aumentar el numero de preugntas
