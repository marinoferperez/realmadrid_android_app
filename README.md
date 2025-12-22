# realmadrid_android_app

## ideas
* onboarding con slides previo al login que explique que ofrece la app MUY BREVEMENTE (una imagen, dos lineas) y aumente la probabilidad de que el usuario acepte los permisos de camara, micro...
  * primer slide: ¡bienvenido al museo del real madrid! (que es la app, que va descubrir el visitante)
  * segundo slide: escanea los qrs del museo (info camisetas, trofeos, partidos, historia -- justificar el uso de la camara)
  * tercer slide: descubre el futuro, presente y pasado del club (calendario, fechas históricas...)
  * ...
  * 
* conexion con openstreetmap api (gratis) para poder indicar la direccion de donde se encuentra el museo/santiago bearnabeu en madrid
* hay que saber que sensores tenemos en nuestro dispositivo android (acelerometro, luz ambiental, giroscopio, sensor de proximidad, magnetometro (brujula/gps), reconocimiento de huella, reconocimiento optico...)
    * es necesario implementar funcinoalidades con cada uno de ellos (preguntar a chatgpt despues de poner en contexto)
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
    * ademas, se deberia trackear el recorrido del cliente y su experiencia en los juegos, ademas de la capacidad de poder valorar las salas (comentarios/estrellas)
    * cambio de idioma (ingles/español)
* calendario de eventos del real madrid donde el dia que haya partido, se marque en rojo y si se pincha, muestr informacion del partido, la hora, y si es copa no es copa...
