package com.example.images_garnetaki

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class AlbumListAdapter(val context: Context,
                       val list: MutableList<Album>
) : RecyclerView.Adapter<AlbumsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumsViewHolder {
        return AlbumsViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AlbumsViewHolder, position: Int) {
        holder.albumTitle.text = list[position].name.plus(" [").plus(list[position].size).plus("]")
        Glide.with(context).load(list[position].img).centerCrop().into(holder.albumButton)
        holder.albumButton.setOnClickListener {
            val tmp = context as MainActivity
            tmp.albumId = list[position].id
            tmp.requestPermission()
        }
    }
}

class AlbumsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val albumsLayout: androidx.cardview.widget.CardView = itemView.findViewById(R.id.albums_layout)
    val albumTitle: TextView = itemView.findViewById(R.id.albumTitle)
    val albumButton: ImageButton = itemView.findViewById(R.id.albumAdd)
}