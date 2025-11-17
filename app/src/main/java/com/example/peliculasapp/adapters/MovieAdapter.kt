package com.example.peliculasapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.peliculasapp.R
import com.example.peliculasapp.data.Movie
import android.util.Log
import java.io.File

class MovieAdapter(
    private val movies: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.moviePoster)
        private val title: TextView = itemView.findViewById(R.id.movieTitle)
        private val year: TextView = itemView.findViewById(R.id.movieYear)
        private val rating: TextView = itemView.findViewById(R.id.movieRating)

        fun bind(movie: Movie) {
            Log.d("MovieAdapter", "Cargando película: ${movie.title}, posterFileName: ${movie.posterFileName}, ID: ${movie.id}")

            // Cargar imagen: primero intenta cargar la imagen guardada
            if (movie.posterFileName.isNotEmpty()) {
                val posterFile = File(itemView.context.filesDir, movie.posterFileName)
                if (posterFile.exists()) {
                    poster.setImageURI(Uri.fromFile(posterFile))
                } else {
                    poster.setImageResource(movie.posterResId)
                }
            } else {
                poster.setImageResource(movie.posterResId)
            }

            title.text = movie.title
            year.text = "Año: ${movie.year}"
            rating.text = "${movie.rating}⭐"
            itemView.setOnClickListener { onItemClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size
}
