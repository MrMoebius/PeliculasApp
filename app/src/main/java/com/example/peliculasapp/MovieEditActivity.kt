package com.example.peliculasapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.peliculasapp.data.Movie
import java.io.File
import java.io.FileOutputStream

class MovieEditActivity : AppCompatActivity() {
    private var posterUri: Uri? = null
    private var posterFileName: String? = null
    private lateinit var posterImageView: ImageView

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            posterUri = it
            posterImageView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_movie_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val isNew = intent.getBooleanExtra("is_new", false)
        val movieId = intent.getIntExtra("movie_id", -1)
        val movieTitle = intent.getStringExtra("movie_title") ?: ""
        val movieYear = intent.getIntExtra("movie_year", 0)
        val moviePoster = intent.getIntExtra("movie_poster", 0)
        val movieSynopsis = intent.getStringExtra("movie_synopsis") ?: ""
        val movieRating = intent.getDoubleExtra("movie_rating", 0.0)
        val movieCast = intent.getStringExtra("movie_cast") ?: ""
        posterFileName = intent.getStringExtra("movie_poster_file")

        val editTitle: EditText = findViewById(R.id.editTitle)
        val editYear: EditText = findViewById(R.id.editYear)
        val editSynopsis: EditText = findViewById(R.id.editSynopsis)
        val editRating: EditText = findViewById(R.id.editRating)
        val editCast: EditText = findViewById(R.id.editCast)
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnHome: Button = findViewById(R.id.btnHome)
        val btnSelectPoster: Button = findViewById(R.id.btnSelectPoster)
        posterImageView = findViewById(R.id.posterPreview)

        if (isNew) {
            editTitle.setText("")
            editYear.setText("")
            editSynopsis.setText("")
            editRating.setText("")
            editCast.setText("")
            posterFileName = null
        } else {
            editTitle.setText(movieTitle)
            editYear.setText(if (movieYear != 0) movieYear.toString() else "")
            editSynopsis.setText(movieSynopsis)
            editRating.setText(if (movieRating != 0.0) movieRating.toString() else "")
            editCast.setText(movieCast)

            // Cargar imagen guardada si existe
            if (!posterFileName.isNullOrEmpty()) {
                val posterFile = File(filesDir, posterFileName!!)
                if (posterFile.exists()) {
                    posterImageView.setImageURI(Uri.fromFile(posterFile))
                }
            }
        }

        btnSelectPoster.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        btnSave.setOnClickListener {
            val newTitle = editTitle.text.toString()
            val newYear = editYear.text.toString().toIntOrNull() ?: 0
            val newSynopsis = editSynopsis.text.toString()
            val newRating = editRating.text.toString().toDoubleOrNull() ?: 0.0
            val newCast = editCast.text.toString()

            // Guardar imagen si fue seleccionada
            val savedFileName = if (posterUri != null) {
                savePosterImage(posterUri!!)
            } else {
                posterFileName ?: ""
            }

            if (isNew) {
                val newMovie = Movie(
                    0, newTitle, newYear, 0, newSynopsis, newRating, newCast,
                    posterFileName = savedFileName
                )
                MovieManager.addMovie(newMovie)
            } else {
                val updatedMovie = Movie(
                    movieId, newTitle, newYear, moviePoster, newSynopsis, newRating, newCast,
                    posterFileName = savedFileName
                )
                MovieManager.updateMovie(updatedMovie)
            }

            finish()
        }

        btnHome.setOnClickListener {
            val intent = Intent(this, MovieListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun savePosterImage(imageUri: Uri): String {
        val fileName = "poster_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, fileName)

        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return fileName
    }
}
