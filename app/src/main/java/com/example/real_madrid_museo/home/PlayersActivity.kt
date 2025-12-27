package com.example.real_madrid_museo.home

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.real_madrid_museo.Jugador
import com.example.real_madrid_museo.PlayersAdapter
import com.example.real_madrid_museo.R
import com.google.android.material.button.MaterialButton

class PlayersActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tvPlayerName: TextView
    private lateinit var tvPlayerPos: TextView
    private lateinit var btnInfo: MaterialButton
    private lateinit var btnLaunchTotem: MaterialButton

    // Variable para el sonido del click
    private var clickPlayer: MediaPlayer? = null

    // LISTA MAESTRA: Textos completos e imágenes correctas
    private val playerList = listOf(
        Jugador(
            "Cristiano Ronaldo",
            "Delantero Leyenda",
            "Máximo goleador de la historia del Real Madrid (451 goles). Ganador de 4 Champions League y 4 Balones de Oro con la camiseta blanca. Una leyenda absoluta.",
            "¡El Bicho! Salta mucho y mete muchos goles.",
            """Cristiano Ronaldo dos Santos Aveiro (Funchal, 5 de febrero de 1985) es considerado uno de los mejores futbolistas de todos los tiempos. Llegó al Real Madrid en 2009 procedente del Manchester United en un traspaso récord.

Durante sus nueve temporadas en el club (2009-2018), reescribió la historia. Se convirtió en el máximo goleador histórico del club con 451 goles en 438 partidos, superando a Raúl González. Promedió más de un gol por partido, una cifra inédita en la élite.

Palmarés con el Real Madrid:
• 4 Champions League (incluyendo tres consecutivas).
• 3 Mundiales de Clubes.
• 2 Ligas de España.
• 2 Copas del Rey.
• 4 Balones de Oro como madridista.

Sus duelos con Messi, sus goles de chilena (como ante la Juventus) y su liderazgo férreo lo convirtieron en el ídolo de una generación.""",
            R.drawable.cr7 // Asegúrate de que el archivo se llama cr7.png en drawable
        ),
        Jugador(
            "Vinícius Jr.",
            "Delantero",
            "La estrella brasileña actual. Autor del gol de la Decimocuarta Champions. Destaca por su velocidad, regate y alegría en el juego.",
            "Es muy rápido y baila cuando marca.",
            """Vinícius José Paixão de Oliveira Júnior (São Gonçalo, 12 de julio de 2000). Formado en el Flamengo, fichó por el Real Madrid al cumplir los 18 años.

Sus inicios fueron difíciles, alternando con el Castilla, pero su persistencia lo llevó a la cima. La temporada 2021-2022 marcó su explosión definitiva, formando una dupla letal con Karim Benzema.

El momento cumbre de su carrera llegó el 28 de mayo de 2022 en París, donde marcó el único gol de la final de la Champions League contra el Liverpool, dando al Real Madrid su 14ª Copa de Europa. Heredero del mítico dorsal '7', Vini lidera la lucha contra el racismo en el fútbol y representa el 'Jogo Bonito' moderno.""",
            R.drawable.vinicius
        ),
        Jugador(
            "Luka Modrić",
            "Centrocampista",
            "Balón de Oro 2018. El cerebro croata que ha dirigido el juego del Madrid durante más de una década. Magia pura con el exterior.",
            "El mago del balón.",
            """Luka Modrić (Zadar, 9 de septiembre de 1985). Llegó al Real Madrid en 2012 procedente del Tottenham. Aunque su inicio fue cuestionado, pronto se convirtió en la brújula del equipo.

Es el único jugador capaz de romper la hegemonía Messi-Ronaldo al ganar el Balón de Oro y el premio The Best en 2018, tras ganar la Champions y llevar a Croacia a la final del Mundial.

Con 6 Champions League en su palmarés (récord compartido con Gento, Nacho, Carvajal y Kroos), Modrić destaca por su longevidad, su técnica exquisita con el exterior del pie y su liderazgo silencioso. A sus casi 40 años, sigue siendo ovacionado en todos los estadios del mundo.""",
            R.drawable.modric
        ),
        Jugador(
            "Toni Kroos",
            "Centrocampista",
            "La leyenda alemana. Se retiró en 2024 tras ganar su sexta Champions. Conocido como el 'Metrónomo' por su precisión en el pase.",
            "Nunca falla un pase.",
            """Toni Kroos (Greifswald, 4 de enero de 1990). Fichado del Bayern de Múnich en 2014 por solo 25 millones, es considerado uno de los mayores 'robos' de la historia del mercado.

Kroos definió una era en el mediocampo junto a Modrić y Casemiro (la 'Triángulo de las Bermudas'). Su efectividad en el pase superó el 93% de media en su carrera. Jamás se le vio nervioso; jugaba las finales como si estuviera en el jardín de su casa.

Se retiró del fútbol de clubes en lo más alto: ganando la Champions League 2024 en Wembley como titular indiscutible, siendo despedido como una leyenda absoluta del Santiago Bernabéu.""",
            R.drawable.kroos
        ),
        Jugador(
            "Iker Casillas",
            "Portero Leyenda",
            "El mejor portero de la historia del club. Capitán, canterano y salvador. Héroe de la Novena y del Mundial.",
            "El Santo que paraba todo.",
            """Iker Casillas Fernández (Móstoles, 20 de mayo de 1981). Ingresó en la cantera del Real Madrid siendo un niño y debutó con el primer equipo en 1999.

Apodado 'El Santo' por sus paradas milagrosas en momentos críticos. Dos momentos definen su carrera blanca:
1. La final de la Champions 2002 (La Novena), donde salió del banquillo para hacer tres paradas históricas al final.
2. Su liderazgo como capitán en la Décima (Lisboa, 2014).

Disputó 725 partidos oficiales con el Real Madrid. Ganó 3 Champions League, 5 Ligas y fue elegido mejor portero del mundo durante 5 años consecutivos.""",
            R.drawable.casillas
        ),
        Jugador(
            "Karim Benzema",
            "Delantero Leyenda",
            "Balón de Oro 2022. Segundo máximo goleador histórico. Calidad, elegancia y gol en un solo jugador.",
            "Juega con clase y mete goles.",
            """Karim Benzema (Lyon, 19 de diciembre de 1987). Durante años fue el socio perfecto de Cristiano Ronaldo, sacrificando su brillo personal por el equipo. Tras la marcha del portugués, Benzema dio un paso al frente.

Su temporada 2021-2022 es considerada una de las mejores actuaciones individuales de la historia del fútbol: Hat-tricks contra PSG y Chelsea, y goles decisivos contra el City llevaron al Madrid a la 14ª.

Ganó el Balón de Oro 2022 por unanimidad. Dejó el club en 2023 como el segundo máximo goleador histórico y el jugador con más títulos junto a Marcelo (25 trofeos), récord luego superado.""",
            R.drawable.benzema
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players)

        // 1. Inicializar Vistas
        viewPager = findViewById(R.id.viewPagerPlayers)
        tvPlayerName = findViewById(R.id.tvPlayerName)
        tvPlayerPos = findViewById(R.id.tvPlayerPos)
        btnInfo = findViewById(R.id.btnInfo)
        btnLaunchTotem = findViewById(R.id.btnLaunchTotem)

        // 2. Inicializar Sonido Click (tech_click.mp3 en res/raw)
        try {
            clickPlayer = MediaPlayer.create(this, R.raw.tech_click)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Configurar el Adaptador
        val adapter = PlayersAdapter(playerList)
        viewPager.adapter = adapter
        
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3
        viewPager.getChildAt(0).overScrollMode = androidx.recyclerview.widget.RecyclerView.OVER_SCROLL_NEVER

        // 4. LÓGICA INFINITA: Empezamos en la mitad exacta
        val middle = Int.MAX_VALUE / 2
        val startPosition = middle - (middle % playerList.size)
        viewPager.setCurrentItem(startPosition, false)

        // 5. Callback de cambio de página
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                
                // Efecto de Vibración al girar
                viewPager.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)

                // Cálculo de posición real (Módulo %)
                val realPosition = position % playerList.size
                actualizarInfoJugador(realPosition)
            }
        })
        
        // Cargar info del primero (el 0)
        actualizarInfoJugador(0)

        // BOTÓN 1: PANTALLA GRANDE (Con sonido y lógica infinita)
        btnLaunchTotem.setOnClickListener {
            reproducirClick()
            val realPos = viewPager.currentItem % playerList.size
            val jugador = playerList[realPos]
            Toast.makeText(this, "📡 Conectando con Sala: ${jugador.nombre}", Toast.LENGTH_SHORT).show()
        }

        // BOTÓN 2: FICHA TÉCNICA (Con sonido y lógica infinita)
        btnInfo.setOnClickListener {
            reproducirClick()
            val realPos = viewPager.currentItem % playerList.size
            val jugador = playerList[realPos]
            
            val intent = Intent(this, PlayerDetailActivity::class.java)
            
            intent.putExtra("EXTRA_NOMBRE", jugador.nombre)
            intent.putExtra("EXTRA_POSICION", jugador.posicion)
            intent.putExtra("EXTRA_IMG", jugador.imagenResId)
            
            // Enviamos AMBOS textos
            intent.putExtra("EXTRA_RESUMEN", jugador.descripcionAdulto)
            intent.putExtra("EXTRA_BIO_LARGA", jugador.biografiaExtensa)
            
            startActivity(intent)
        }
    }

    private fun actualizarInfoJugador(index: Int) {
        val jugador = playerList[index]
        tvPlayerName.text = jugador.nombre
        tvPlayerPos.text = jugador.posicion
    }

    private fun reproducirClick() {
        if (clickPlayer != null) {
            if (clickPlayer!!.isPlaying) {
                clickPlayer!!.seekTo(0)
            }
            clickPlayer!!.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clickPlayer?.release()
        clickPlayer = null
    }
}