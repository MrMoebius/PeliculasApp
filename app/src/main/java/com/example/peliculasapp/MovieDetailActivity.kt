package com.example.peliculasapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MovieDetailActivity : AppCompatActivity() {
    private var movieId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_movie_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        movieId = intent.getIntExtra("movie_id", -1)
        displayMovieDetails()

        val btnEdit: Button = findViewById(R.id.btnEdit)
        val btnBack: Button = findViewById(R.id.btnBack)

        btnEdit.setOnClickListener {
            val movie = MovieManager.getMovieById(movieId)
            if (movie != null) {
                val intent = Intent(this, MovieEditActivity::class.java)
                intent.putExtra("movie_id", movie.id)
                intent.putExtra("movie_title", movie.title)
                intent.putExtra("movie_year", movie.year)
                intent.putExtra("movie_poster", movie.posterResId)
                intent.putExtra("movie_synopsis", movie.synopsis)
                intent.putExtra("movie_rating", movie.rating)
                intent.putExtra("movie_cast", movie.cast)
                startActivityForResult(intent, EDIT_REQUEST_CODE)
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar detalles cada vez que vuelves a esta pantalla
        displayMovieDetails()
    }

    private fun displayMovieDetails() {
        val movie = MovieManager.getMovieById(movieId)
        if (movie != null) {
            val poster: ImageView = findViewById(R.id.detailPoster)
            val title: TextView = findViewById(R.id.detailTitle)
            val year: TextView = findViewById(R.id.detailYear)
            val synopsis: TextView = findViewById(R.id.detailSynopsis)
            val rating: TextView = findViewById(R.id.detailRating)
            val cast: TextView = findViewById(R.id.detailCast)

            poster.setImageResource(movie.posterResId)
            title.text = movie.title
            year.text = "Año: ${movie.year}"
            synopsis.text = movie.synopsis
            rating.text = "Puntuación: ${movie.rating}/10⭐"
            cast.text = "Reparto: ${movie.cast}"
        }
    }

    companion object {
        const val EDIT_REQUEST_CODE = 1
    }
}
