package com.example.finals_comic_app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.finals_comic_app.R
import com.example.finals_comic_app.data.model.Manga

class MangaAdapter(private val onMangaClick: (Int) -> Unit) :
    ListAdapter<Manga, MangaAdapter.MangaViewHolder>(MangaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manga, parent, false)
        return MangaViewHolder(view, onMangaClick)
    }

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MangaViewHolder(itemView: View, private val onMangaClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.mangaImage)
        private val titleView: TextView = itemView.findViewById(R.id.mangaTitle)
        private val scoreView: TextView = itemView.findViewById(R.id.mangaScore)

        fun bind(manga: Manga) {
            titleView.text = manga.title
            scoreView.text = "Score: ${manga.score ?: "N/A"}"
            imageView.load(manga.images.jpg.largeImageUrl) {
                crossfade(true)
            }
            itemView.setOnClickListener { onMangaClick(manga.malId) }
        }
    }

    class MangaDiffCallback : DiffUtil.ItemCallback<Manga>() {
        override fun areItemsTheSame(oldItem: Manga, newItem: Manga): Boolean {
            return oldItem.malId == newItem.malId
        }

        override fun areContentsTheSame(oldItem: Manga, newItem: Manga): Boolean {
            return oldItem == newItem
        }
    }
}