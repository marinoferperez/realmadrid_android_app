package com.example.real_madrid_museo // O tu paquete correspondiente

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

// Este adaptador recibe la lista de jugadores y crea las vistas
class PlayersAdapter(private val players: List<Jugador>) :
    RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>() {

    // Esta clase interna guarda las referencias a los elementos de la carta (solo la imagen por ahora)
    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPlayer: ImageView = view.findViewById(R.id.imgPlayer)
    }

    // Paso 1: Crea el diseño de la carta cuando hace falta
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player_card, parent, false)
        return PlayerViewHolder(view)
    }

    // Paso 2: Rellena los datos (pone la foto)
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.imgPlayer.setImageResource(player.imagenResId)
    }

    // Paso 3: Le dice al carrusel cuántas cartas hay
    override fun getItemCount(): Int = players.size
}