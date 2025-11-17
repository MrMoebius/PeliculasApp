package com.example.peliculasapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.peliculasapp.data.Movie

class MovieEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_movie_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val movieId = intent.getIntExtra("movie_id", -1)
        val movieTitle = intent.getStringExtra("movie_title") ?: ""
        val movieYear = intent.getIntExtra("movie_year", 0)
        val moviePoster = intent.getIntExtra("movie_poster", 0)
        val movieSynopsis = intent.getStringExtra("movie_synopsis") ?: ""
        val movieRating = intent.getDoubleExtra("movie_rating", 0.0)
        val movieCast = intent.getStringExtra("movie_cast") ?: ""

        val editTitle: EditText = findViewById(R.id.editTitle)
        val editYear: EditText = findViewById(R.id.editYear)
        val editSynopsis: EditText = findViewById(R.id.editSynopsis)
        val editRating: EditText = findViewById(R.id.editRating)
        val editCast: EditText = findViewById(R.id.editCast)
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnHome: Button = findViewById(R.id.btnHome)

        editTitle.setText(movieTitle)
        editYear.setText(movieYear.toString())
        editSynopsis.setText(movieSynopsis)
        editRating.setText(movieRating.toString())
        editCast.setText(movieCast)

        btnSave.setOnClickListener {
            val newTitle = editTitle.text.toString()
            val newYear = editYear.text.toString().toIntOrNull() ?: movieYear
            val newSynopsis = editSynopsis.text.toString()
            val newRating = editRating.text.toString().toDoubleOrNull() ?: movieRating
            val newCast = editCast.text.toString()

            val updatedMovie = Movie(movieId, newTitle, newYear, moviePoster, newSynopsis, newRating, newCast)
            MovieManager.updateMovie(updatedMovie)

            // Simplemente cerrar esta Activity, no crear una nueva
            finish()
        }

        btnHome.setOnClickListener {
            // Ir a lista y limpiar la pila
            val intent = Intent(this, MovieListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
