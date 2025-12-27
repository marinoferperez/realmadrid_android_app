package com.example.real_madrid_museo.home

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.real_madrid_museo.R
import com.google.android.material.button.MaterialButton

class PlayerDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_detail)

        // 1. Recibimos los datos
        val nombre = intent.getStringExtra("EXTRA_NOMBRE")
        val posicion = intent.getStringExtra("EXTRA_POSICION")
        val imagenId = intent.getIntExtra("EXTRA_IMG", R.mipmap.ic_launcher)
        
        // RECIBIMOS LOS DOS TEXTOS
        val resumenCorto = intent.getStringExtra("EXTRA_RESUMEN") ?: ""
        val biografiaLarga = intent.getStringExtra("EXTRA_BIO_LARGA") ?: ""

        // 2. Vinculamos vistas
        val imgHeader = findViewById<ImageView>(R.id.imgDetailHeader)
        val tvName = findViewById<TextView>(R.id.tvDetailName)
        val tvPos = findViewById<TextView>(R.id.tvDetailPos)
        val tvDesc = findViewById<TextView>(R.id.tvDetailDescription)
        val btnMoreInfo = findViewById<MaterialButton>(R.id.btnMoreInfo)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // 3. Rellenamos lo básico
        tvName.text = nombre
        tvPos.text = posicion
        imgHeader.setImageResource(imagenId)

        // 4. LÓGICA: Empezamos mostrando el RESUMEN
        tvDesc.text = resumenCorto

        // 5. AL PULSAR EL BOTÓN: CAMBIAMOS A LA BIBLIOGRAFÍA
        btnMoreInfo.setOnClickListener {
            // Sustituimos el texto actual por la biografía completa
            tvDesc.text = biografiaLarga
            
            // Ocultamos el botón porque ya no hay más info que mostrar
            btnMoreInfo.visibility = View.GONE
        }

        btnBack.setOnClickListener { finish() }
    }
}