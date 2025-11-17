package com.example.peliculasapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

class MovieDetailActivity : AppCompatActivity() {
    private var movieId: Int = -1
    private var posterFileName: String = ""

    // Activity Result API (moderno)
    private val editMovieLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Recargar detalles cuando regresa de editar
            displayMovieDetails()
        }
    }

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
                intent.putExtra("movie_poster_file", movie.posterFileName)
                editMovieLauncher.launch(intent)  // ← REEMPLAZA startActivityForResult
            }
        }

        val btnDelete: Button = findViewById(R.id.btnDelete)
        btnDelete.setOnClickListener {
            // Mostrar diálogo de confirmación
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar película")
                .setMessage("¿Estás seguro de que deseas eliminar esta película?")
                .setPositiveButton("Sí, eliminar") { _, _ ->
                    MovieManager.deleteMovie(movieId)
                    finish()  // Regresa a la lista
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
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

            // Cargar imagen
            if (movie.posterFileName.isNotEmpty()) {
                val posterFile = File(filesDir, movie.posterFileName)
                if (posterFile.exists()) {
                    poster.setImageURI(Uri.fromFile(posterFile))
                } else {
                    poster.setImageResource(movie.posterResId)
                }
            } else {
                poster.setImageResource(movie.posterResId)
            }

            posterFileName = movie.posterFileName
            title.text = movie.title
            year.text = "Año: ${movie.year}"
            synopsis.text = movie.synopsis
            rating.text = "Puntuación: ${movie.rating}/10⭐"
            cast.text = "Reparto: ${movie.cast}"
        }
    }
}
