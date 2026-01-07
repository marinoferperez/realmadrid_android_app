package com.example.real_madrid_museo.home

import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.real_madrid_museo.Jugador
import com.example.real_madrid_museo.PlayersAdapter
import com.example.real_madrid_museo.R
import com.google.android.material.button.MaterialButton
import kotlin.math.sqrt

class PlayersActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var viewPager: ViewPager2
    private lateinit var tvPlayerName: TextView
    private lateinit var tvPlayerPos: TextView
    private lateinit var btnInfo: MaterialButton
    private lateinit var btnLaunchTotem: MaterialButton

    // AUDIO
    private var clickPlayer: MediaPlayer? = null
    private var magicSound: MediaPlayer? = null

    // SENSORES
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private var accelerometer: Sensor? = null

    // ESTADO DEL JUGADOR
    private var isUnlocked = false
    private var lastShakeTime: Long = 0
    
    // NUEVA VARIABLE: Controla si ya hemos desbloqueado la sala entera
    private var yaDesbloqueadoParaSiempre = false

    // DATOS
    private val playerList = listOf(
        Jugador("Cristiano Ronaldo", "Delantero Leyenda",
            "Máximo goleador de la historia del Real Madrid...",
            "¡El Bicho!",
            "Biografía extensa de CR7...", R.drawable.cr7),
        Jugador("Vinícius Jr.", "Delantero",
            "La estrella brasileña actual...",
            "Es muy rápido...",
            "Biografía extensa de Vini...", R.drawable.vinicius),
        Jugador("Luka Modrić", "Centrocampista",
            "Balón de Oro 2018...",
            "El mago del balón.",
            "Biografía extensa de Modric...", R.drawable.modric),
        Jugador("Toni Kroos", "Centrocampista",
            "La leyenda alemana...",
            "Nunca falla un pase.",
            "Biografía extensa de Kroos...", R.drawable.kroos),
        Jugador("Iker Casillas", "Portero Leyenda",
            "El mejor portero de la historia...",
            "El Santo.",
            "Biografía extensa de Iker...", R.drawable.casillas),
        Jugador("Karim Benzema", "Delantero Leyenda",
            "Balón de Oro 2022...",
            "Juega con clase.",
            "Biografía extensa de Benzema...", R.drawable.benzema)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players)

        // 1. INICIALIZAR VISTAS
        viewPager = findViewById(R.id.viewPagerPlayers)
        tvPlayerName = findViewById(R.id.tvPlayerName)
        tvPlayerPos = findViewById(R.id.tvPlayerPos)
        btnInfo = findViewById(R.id.btnInfo)
        btnLaunchTotem = findViewById(R.id.btnLaunchTotem)

        // 2. SENSORES
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // 3. SONIDOS
        clickPlayer = MediaPlayer.create(this, R.raw.tech_click)
        magicSound = MediaPlayer.create(this, R.raw.tech_click)

        // 4. ADAPTER
        val adapter = PlayersAdapter(playerList)
        viewPager.adapter = adapter
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        viewPager.offscreenPageLimit = 3
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val middle = Int.MAX_VALUE / 2
        val startPosition = middle - (middle % playerList.size)
        viewPager.setCurrentItem(startPosition, false)

        // 5. CAMBIO DE PÁGINA
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // LÓGICA DE BLOQUEO NUEVA:
                // Si ya desbloqueamos una vez para siempre, NO bloqueamos.
                // Si aún no, entonces sí bloqueamos.
                
                if (!yaDesbloqueadoParaSiempre) {
                    viewPager.post { bloquearJugador() }
                } else {
                    // Si ya está desbloqueado para siempre, nos aseguramos 
                    // de que el nuevo jugador se vea en color
                    viewPager.post { asegurarJugadorVisible() }
                }

                val realPos = position % playerList.size
                actualizarInfoTexto(realPos)
            }
        })

        actualizarInfoTexto(0)
        
        // Estado inicial: Bloqueado (usamos post para esperar a que cargue la UI)
        viewPager.post { bloquearJugador() }

        btnLaunchTotem.setOnClickListener { lanzarVideoAccion() }
        btnInfo.setOnClickListener { abrirFichaAccion() }
    }

    // --------------------------------------------------------
    // SENSORES
    // --------------------------------------------------------
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            // PROXIMIDAD -> DESBLOQUEAR
            Sensor.TYPE_PROXIMITY -> {
                val distancia = event.values[0]
                // Si detecta mano (< rango max) Y aún no está desbloqueado
                if (distancia < proximitySensor!!.maximumRange && !isUnlocked) {
                    desbloquearJugador()
                }
            }

            // ACELERÓMETRO
            Sensor.TYPE_ACCELEROMETER -> {
                if (!isUnlocked) return 

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                // SHAKE
                val gForce = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH
                if (gForce > 2.5f) {
                    val now = System.currentTimeMillis()
                    if (now - lastShakeTime > 1000) {
                        lastShakeTime = now
                        abrirFichaAccion()
                    }
                }

                // TILT (INCLINAR)
                if (y < 3.0f && y > -3.0f && z > 5.0f) {
                     val now = System.currentTimeMillis()
                     if (now - lastShakeTime > 2000) {
                         lastShakeTime = now
                         lanzarVideoAccion()
                     }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // --------------------------------------------------------
    // LÓGICA DE ESTADO
    // --------------------------------------------------------

    private fun bloquearJugador() {
        isUnlocked = false
        btnInfo.visibility = View.INVISIBLE
        btnLaunchTotem.visibility = View.INVISIBLE
        Toast.makeText(this, "✋ Pasa la mano para desbloquear", Toast.LENGTH_SHORT).show()
        aplicarFiltroBlancoYNegro(true)
    }

    private fun desbloquearJugador() {
        isUnlocked = true
        
        // ¡AQUÍ ESTÁ EL TRUCO! 
        // Marcamos que ya se ha cumplido el ritual para siempre en esta sesión
        yaDesbloqueadoParaSiempre = true 

        viewPager.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        magicSound?.start()
        Toast.makeText(this, "✨ ¡Sala Desbloqueada!", Toast.LENGTH_SHORT).show()

        btnInfo.visibility = View.VISIBLE
        btnLaunchTotem.visibility = View.VISIBLE
        aplicarFiltroBlancoYNegro(false)
    }

    // Función auxiliar para cuando cambiamos de jugador y YA estamos desbloqueados
    private fun asegurarJugadorVisible() {
        // Simplemente aseguramos que se ve en color y botones visibles
        // pero SIN hacer sonido ni vibración ni toast
        btnInfo.visibility = View.VISIBLE
        btnLaunchTotem.visibility = View.VISIBLE
        aplicarFiltroBlancoYNegro(false)
    }

    private fun aplicarFiltroBlancoYNegro(activar: Boolean) {
        val recyclerView = viewPager.getChildAt(0) as? RecyclerView
        val viewHolder = recyclerView?.findViewHolderForAdapterPosition(viewPager.currentItem)
        val imgView = viewHolder?.itemView?.findViewById<ImageView>(R.id.imgPlayer)

        if (imgView != null) {
            if (activar) {
                val matrix = ColorMatrix()
                matrix.setSaturation(0f)
                val filter = ColorMatrixColorFilter(matrix)
                imgView.colorFilter = filter
                imgView.imageAlpha = 150
            } else {
                imgView.colorFilter = null
                imgView.imageAlpha = 255
            }
        }
    }

    // --------------------------------------------------------
    // ACCIONES
    // --------------------------------------------------------
    private fun lanzarVideoAccion() {
        reproducirClick()
        viewPager.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        val realPos = viewPager.currentItem % playerList.size
        val jugador = playerList[realPos]
        Toast.makeText(this, "📡 LANZANDO VÍDEO: ${jugador.nombre}", Toast.LENGTH_SHORT).show()
    }

    private fun abrirFichaAccion() {
        reproducirClick()
        val realPos = viewPager.currentItem % playerList.size
        val jugador = playerList[realPos]
        
        val intent = Intent(this, PlayerDetailActivity::class.java)
        intent.putExtra("EXTRA_NOMBRE", jugador.nombre)
        intent.putExtra("EXTRA_POSICION", jugador.posicion)
        intent.putExtra("EXTRA_IMG", jugador.imagenResId)
        intent.putExtra("EXTRA_RESUMEN", jugador.descripcionAdulto)
        intent.putExtra("EXTRA_BIO_LARGA", jugador.biografiaExtensa)
        startActivity(intent)
    }

    private fun actualizarInfoTexto(index: Int) {
        val jugador = playerList[index]
        tvPlayerName.text = jugador.nombre
        tvPlayerPos.text = jugador.posicion
    }

    private fun reproducirClick() {
        if (clickPlayer?.isPlaying == true) clickPlayer?.seekTo(0)
        clickPlayer?.start()
    }

    override fun onResume() {
        super.onResume()
        proximitySensor?.also { sensor -> sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI) }
        accelerometer?.also { sensor -> sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}