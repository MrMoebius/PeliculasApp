package com.example.peliculasapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peliculasapp.adapters.MovieAdapter

class MovieListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_movie_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewMovies)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        // Recargar el adapter cuando vuelves a esta pantalla
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Obtener películas FRESCAS de MovieManager
        val movies = MovieManager.getMovies().toMutableList()

        val adapter = MovieAdapter(movies) { movie ->
            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtra("movie_id", movie.id)
            intent.putExtra("movie_title", movie.title)
            intent.putExtra("movie_year", movie.year)
            intent.putExtra("movie_poster", movie.posterResId)
            intent.putExtra("movie_synopsis", movie.synopsis)
            intent.putExtra("movie_rating", movie.rating)
            intent.putExtra("movie_cast", movie.cast)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}
