package com.example.real_madrid_museo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PlayersAdapter(private val players: List<Jugador>) :
    RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPlayer: ImageView = view.findViewById(R.id.imgPlayer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player_card, parent, false)
        return PlayerViewHolder(view)
    }

    // INFINITO: Calculamos la posición real usando el resto de la división
    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        // Si la posición es 100 y hay 6 jugadores, 100 % 6 = 4. Toca el jugador 4.
        val realPosition = position % players.size
        val player = players[realPosition]
        holder.imgPlayer.setImageResource(player.imagenResId)
    }

    // INFINITO: Decimos que hay "casi infinitos" elementos
    override fun getItemCount(): Int = Int.MAX_VALUE
}