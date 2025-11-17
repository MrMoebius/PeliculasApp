package com.example.peliculasapp

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.peliculasapp.data.Movie
import java.io.File

object MovieManager {
    private const val MOVIES_FILE = "movies.json"
    private lateinit var context: Context
    private var movies: MutableList<Movie> = mutableListOf()

    fun init(appContext: Context) {
        context = appContext
        loadMoviesFromFile()
        if (movies.isEmpty()) {
            initializeDefaultMovies()
        }
    }

    private fun initializeDefaultMovies() {
        movies = mutableListOf(
            Movie(1, "Origen", 2010, R.drawable.posterorigen, "Un brillante ladrón de secretos corporativos que se especializa en tecnología de extracción de información mediante sueños compartidos debe ejecutar una peligrosa misión: implantar una idea en lugar de robarla. Acompañado por un equipo de expertos, debe adentrarse en múltiples capas de realidad onírica mientras los límites entre sueño y realidad se desvanecen.", 8.8, "Leonardo DiCaprio, Ellen Page, Joseph Gordon-Levitt, Marion Cotillard, Tom Hardy, Michael Caine"),
            Movie(2, "El Caballero Oscuro", 2008, R.drawable.postercaballerooscuro, "Batman se enfrenta a su mayor amenaza: el Joker, un criminal caótico que siembra el terror en Gotham con sus planes diabólicos. Mientras el vigilante enmascarado intenta mantener el orden en la ciudad, debe hacer frente a decisiones morales cada vez más difíciles que desafían sus principios y convicciones.", 9.0, "Christian Bale, Heath Ledger, Aaron Eckhart, Maggie Gyllenhaal, Michael Caine, Gary Oldman"),
            Movie(3, "Interestellar", 2014, R.drawable.posterinterestellar, "Un equipo de astronautas viaja a través de un agujero de gusano descubierto cerca de Saturno en busca de un nuevo mundo habitable para la humanidad. Mientras la Tierra se degrada lentamente, los exploradores deben superar obstáculos imposibles y afrontar el desgarro de las relaciones humanas a través del tiempo y el espacio.", 8.6, "Matthew McConaughey, Anne Hathaway, Jessica Chastain, Michael Caine, Wes Bentley, Casey Affleck"),
            Movie(4, "Pulp Fiction", 1994, R.drawable.posterpulpfiction, "Un collage de historias entrelazadas que giran en torno a varios criminales, boxeadores y miembros de la mafia de Los Ángeles. Con un diálogo ingenioso y escenas de violencia cruda, la película teje múltiples narrativas que se interseccionan de formas inesperadas e impactantes.", 8.9, "John Travolta, Samuel L. Jackson, Uma Thurman, Harvey Keitel, Tim Roth, Quentin Tarantino"),
            Movie(5, "Matrix", 1999, R.drawable.postermatrix, "Un hacker informático descubre la verdad perturbadora: la realidad en la que vive es en realidad una simulación sofisticada creada por máquinas inteligentes. Debe unirse a un grupo de rebeldes que luchan contra la inteligencia artificial para liberar a la humanidad del control digital.", 8.7, "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss, Hugo Weaving, Joe Pantoliano, Marcus Chong"),
            Movie(6, "Forrest Gump", 1994, R.drawable.posterforrestgump, "La épica historia de un hombre de capacidad mental limitada que, contra todo pronóstico, logra hazañas extraordinarias a lo largo de varias décadas. Desde su participación en la Guerra de Vietnam hasta convertirse en campeón de tenis de mesa, Forrest experimenta momentos cruciales de la historia estadounidense.", 8.8, "Tom Hanks, Sally Field, Gary Sinise, Mykelti Williamson, Rebecca Williams, Siobhan Fallon"),
            Movie(7, "Gladiador", 2000, R.drawable.postergladiator, "Un general romano respetado es traicionado y vendido como esclavo en la arena de combate. Obligado a luchar por su supervivencia en sangrientos enfrentamientos, debe encontrar la forma de vengarse mientras gana el favor del pueblo romano y expone la corrupción del imperio.", 8.5, "Russell Crowe, Joaquin Phoenix, Lucilla Ridley Scott, Djimon Hounsou, Oliver Reed, Derek Jacobi"),
            Movie(8, "Shawshank Redemption", 1994, R.drawable.postershawshank, "Un hombre inocente es condenado a cadena perpetua por un crimen que no cometió y encarcelado en la brutal prisión de Shawshank. A lo largo de décadas, forma una amistad profunda con un compañero preso mientras planifica meticulosamente su escape y redemción.", 9.3, "Tim Robbins, Morgan Freeman, Bob Gunton, William Sadler, Clancy Brown, David Proval"),
            Movie(9, "Avatar", 2009, R.drawable.posteravatar, "Un soldado parapléjico es reclutado para infiltrarse en la civilización alienígena de Pandora usando un cuerpo de avatar biológico. Enamorándose de una guerrera indígena, debe elegir entre lealtad a su especie o defender su nuevo hogar de la explotación corporativa y militar.", 7.8, "Sam Worthington, Zoe Saldana, Sigourney Weaver, Stephen Lang, Michelle Yeoh, Giovanni Ribisi"),
            Movie(10, "Titanic", 1997, R.drawable.postertitanic, "Una historia épica de amor que trasciende las barreras de clase durante el viaje inaugural del Titanic. Mientras el barco más grande del mundo se hunde en las aguas heladas del Atlántico, dos pasajeros de mundos opuestos comparten momentos de pasión que desafían el destino y la muerte.", 7.8, "Leonardo DiCaprio, Kate Winslet, Billy Zane, Kathy Bates, Frances Fisher, Danny Nucci")
        )
        saveMoviesToFile()
    }

    fun getMovies(): List<Movie> = movies

    fun getMovieById(id: Int): Movie? = movies.find { it.id == id }

    fun updateMovie(movie: Movie) {
        val index = movies.indexOfFirst { it.id == movie.id }
        if (index != -1) {
            movies[index] = movie
            saveMoviesToFile()
        }
    }

    fun addMovie(movie: Movie) {
        val newId = if (movies.isEmpty()) 1 else movies.maxOf { it.id } + 1
        val newMovie = movie.copy(id = newId)
        movies.add(newMovie)
        saveMoviesToFile()
    }

    private fun saveMoviesToFile() {
        try {
            val file = File(context.filesDir, MOVIES_FILE)
            val gson = Gson()
            val json = gson.toJson(movies)
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadMoviesFromFile() {
        try {
            val file = File(context.filesDir, MOVIES_FILE)
            if (file.exists()) {
                val json = file.readText()
                val gson = Gson()
                val type = object : TypeToken<List<Movie>>() {}.type
                movies = gson.fromJson(json, type) ?: mutableListOf()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // En MovieManager, agrega una función para obtener la URI del archivo guardado
    fun getPosterUri(fileName: String): Uri? {
        if (fileName.isEmpty()) return null
        val file = File(context.filesDir, fileName)
        return if (file.exists()) Uri.fromFile(file) else null
    }
    fun deleteMovie(movieId: Int) {
        movies.removeAll { it.id == movieId }
        saveMoviesToFile()
    }

}
