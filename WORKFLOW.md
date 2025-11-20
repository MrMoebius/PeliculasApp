# 🔄 Flujo de trabajo - PeliculasApp

Documentación técnica del flujo de datos, navegación y procesos principales de la aplicación.

---

## 📊 Diagrama general del flujo

```
┌────────────────────────────────────────────────────┐
│                     MyApplication.kt               │
│              (Inicializa MovieManager)             │
└────────────────────────┬───────────────────────────┘
                         │
                         ▼
         ┌───────────────────────────────┐
         │   MovieListActivity           │
         │  (Pantalla principal)         │
         │  - RecyclerView               │
         │  - Botón Agregar              │
         └───────┬───────────────────┬───┘
                 │                   │
        Click item│                  │Agregar
                 │                   │
                 ▼                   ▼
         ┌──────────────────┐  ┌──────────────────┐
         │ MovieDetailAct   │  │ MovieEditAct     │
         │ (Ver detalles)   │  │ (is_new=true)    │
         │ - Info película  │  │ - Formulario     │
         │ - Editar botón   │  │ - Seleccionar img│
         │ - Eliminar botón │  │ - Guardar        │
         └────┬─────────┬───┘  └────────┬─────────┘
              │         │               │
       Editar │         │Eliminar   Guardar
              │         │               │
              ▼         ▼               ▼
         ┌──────────────────┐  ┌──────────────────┐
         │ MovieEditAct     │  │   MovieManager   │
         │ (is_new=false)   │  │  addMovie()      │
         │ - Precargar datos│  │  updateMovie()   │
         │ - Editar campos  │  │  deleteMovie()   │
         │ - Guardar cambios│  │  saveMoviesToFile│
         └────────┬─────────┘  └────────┬─────────┘
                  │                     │
                  └──────────┬──────────┘
                             │
                    Guardar datos
                             │
                             ▼
              ┌──────────────────────────┐
              │  MovieManager            │
              │  - movies (en memoria)   │
              │  - JSON (persistencia)   │
              │  - Imágenes (filesDir)   │
              └──────────────────────────┘
```

---

## 🔄 Ciclo de vida de la aplicación

### 1️⃣ Inicio de la app

```
MyApplication.onCreate()
    ↓
MovieManager.initialize()
    ├─ Leer movies.json
    ├─ Si no existe, crear películas por defecto
    └─ Cargar en memoria (movies list)
    ↓
MovieListActivity.onCreate()
    ├─ Inicializar UI
    ├─ Obtener lista de MovieManager
    ├─ Crear adapter
    └─ Mostrar RecyclerView
```

### 2️⃣ Ver detalles de película

```
MovieListActivity
    ↓ (usuario hace clic en item)
MovieAdapter.onBindViewHolder() - onClick listener
    ↓
Intent → MovieDetailActivity
    ├─ movieId
    ├─ movieTitle
    ├─ movieYear
    ├─ moviePoster (posterResId)
    ├─ movieSynopsis
    ├─ movieRating
    ├─ movieCast
    └─ moviePosterFile (posterFileName)
    ↓
MovieDetailActivity.onCreate()
    ├─ Recibir datos del Intent
    ├─ displayMovieDetails()
    │   ├─ Buscar película en MovieManager
    │   ├─ Si posterFileName ≠ vacío:
    │   │   └─ Cargar imagen desde filesDir
    │   └─ Si no: cargar drawable por defecto
    └─ Mostrar todos los datos
```

### 3️⃣ Agregar película nueva

```
MovieListActivity
    ↓ (usuario pulsa "Agregar Película")
Intent → MovieEditActivity (is_new=true)
    ↓
MovieEditActivity.onCreate()
    ├─ is_new = true
    ├─ Limpiar todos los campos
    ├─ posterFileName = null
    ├─ posterImageView muestra iconpeliculasapp
    └─ Mostrar formulario vacío
    ↓
(usuario completa formulario)
    ├─ Selecciona imagen: galleryLauncher.launch()
    │   ├─ Abre galería
    │   ├─ Usuario elige imagen
    │   ├─ posterUri = imagen seleccionada
    │   └─ posterImageView.setImageURI(posterUri)
    └─ Completa campos: título, año, sinopsis, etc.
    ↓
(usuario pulsa "Guardar")
btnSave.onClick()
    ├─ newTitle = editTitle.text
    ├─ newYear = editYear.text.toInt()
    ├─ newSynopsis = editSynopsis.text
    ├─ newRating = editRating.text.toDouble()
    ├─ newCast = editCast.text
    ├─ Si posterUri ≠ null:
    │   ├─ savePosterImage(posterUri)
    │   │   ├─ Generar nombre único: "poster_[timestamp].jpg"
    │   │   ├─ Copiar imagen a filesDir
    │   │   └─ Retornar nombreArchivo
    │   └─ savedFileName = nombreArchivo
    ├─ Crear Movie con datos
    ├─ MovieManager.addMovie(newMovie)
    │   ├─ Generar ID único
    │   ├─ Agregar a lista (movies)
    │   └─ saveMoviesToFile()
    │       ├─ Convertir movies a JSON (Gson)
    │       └─ Guardar en movies.json
    └─ finish() → vuelve a MovieListActivity
```

### 4️⃣ Editar película existente

```
MovieDetailActivity (mostrando película)
    ↓ (usuario pulsa "Editar")
btnEdit.onClick()
    ├─ Obtener datos de la película actual
    └─ Intent → MovieEditActivity (is_new=false)
        ├─ movieId
        ├─ movieTitle
        ├─ movieYear
        ├─ moviePoster
        ├─ movieSynopsis
        ├─ movieRating
        ├─ movieCast
        └─ moviePosterFile
    ↓
MovieEditActivity.onCreate()
    ├─ is_new = false
    ├─ Recibir datos del Intent
    ├─ Precargar en EditTexts:
    │   ├─ editTitle.setText(movieTitle)
    │   ├─ editYear.setText(movieYear)
    │   ├─ editSynopsis.setText(movieSynopsis)
    │   ├─ editRating.setText(movieRating)
    │   └─ editCast.setText(movieCast)
    ├─ Cargar imagen guardada:
    │   ├─ Si posterFileName ≠ vacío:
    │   │   ├─ posterFile = File(filesDir, posterFileName)
    │   │   ├─ Si existe:
    │   │   │   └─ posterImageView.setImageURI(posterFile)
    │   │   └─ posterUri = null (aún no ha seleccionado)
    │   └─ Mostrar formulario con datos
    ↓
(usuario modifica campos)
    ├─ Edita campos necesarios
    ├─ Opcionalmente selecciona imagen nueva
    │   └─ posterUri = nueva imagen
    └─ Pulsa "Guardar"
    ↓
btnSave.onClick()
    ├─ Obtener datos del formulario
    ├─ Si posterUri ≠ null (nueva imagen):
    │   ├─ savePosterImage(posterUri)
    │   └─ savedFileName = nombreArchivo
    ├─ Si no (mantener imagen anterior):
    │   └─ savedFileName = posterFileName original
    ├─ Crear Movie actualizada
    ├─ MovieManager.updateMovie(updatedMovie)
    │   ├─ Encontrar película por ID
    │   ├─ Reemplazar con datos nuevos
    │   └─ saveMoviesToFile()
    └─ finish() → vuelve a MovieDetailActivity
        └─ onResume() → displayMovieDetails() → recarga datos
```

### 5️⃣ Eliminar película

```
MovieDetailActivity (mostrando película)
    ↓ (usuario pulsa "Eliminar")
btnDelete.onClick()
    ├─ AlertDialog.Builder()
    │   ├─ setTitle("Eliminar película")
    │   ├─ setMessage("¿Estás seguro...?")
    │   ├─ Positivo: "Sí, eliminar"
    │   │   ├─ MovieManager.deleteMovie(movieId)
    │   │   │   ├─ Buscar película por ID
    │   │   │   ├─ Eliminar de lista (movies)
    │   │   │   └─ saveMoviesToFile()
    │   │   └─ finish() → vuelve a MovieListActivity
    │   └─ Negativo: "Cancelar"
    │       └─ Cerrar diálogo
    └─ Si usuario confirma: película eliminada
```

---

## 💾 Gestión de datos

### MovieManager - Singleton pattern

```kotlin
object MovieManager {
    private var movies = mutableListOf<Movie>()
    
    // Operaciones en memoria
    fun getMovies() → List<Movie>
    fun getMovieById(id: Int) → Movie?
    fun addMovie(movie: Movie) → crea ID único
    fun updateMovie(movie: Movie) → actualiza en lista
    fun deleteMovie(id: Int) → elimina de lista
    
    // Persistencia
    fun saveMoviesToFile() → JSON en filesDir/movies.json
    fun loadMoviesFromFile() → JSON desde filesDir
    fun initializeDefaultMovies() → 3 películas por defecto
}
```

### Flujo de persistencia

```
1. En memoria (movies list)
   ↓
2. Operación (add/update/delete)
   ↓
3. saveMoviesToFile()
   ├─ Convertir movies a JSON (Gson)
   ├─ Crear FileOutputStream
   └─ Escribir JSON en filesDir/movies.json
   ↓
4. Al reiniciar app
   ├─ loadMoviesFromFile()
   ├─ Leer JSON desde movies.json
   ├─ Convertir JSON a List<Movie> (Gson)
   └─ Cargar en movies list
```

### Almacenamiento de imágenes

```
1. Usuario selecciona imagen
   ├─ posterUri = Uri de galería
   └─ posterImageView.setImageURI(posterUri)
   ↓
2. Usuario pulsa "Guardar"
   ├─ savePosterImage(posterUri)
   │   ├─ fileName = "poster_${System.currentTimeMillis()}.jpg"
   │   ├─ file = File(filesDir, fileName)
   │   ├─ inputStream = contentResolver.openInputStream(posterUri)
   │   ├─ outputStream = FileOutputStream(file)
   │   ├─ inputStream.copyTo(outputStream)
   │   └─ Retornar fileName
   └─ posterFileName = fileName
   ↓
3. Guardar película
   ├─ movie.posterFileName = fileName
   └─ saveMoviesToFile() → JSON con posterFileName
   ↓
4. Cargar película
   ├─ posterFile = File(filesDir, movie.posterFileName)
   └─ posterImageView.setImageURI(Uri.fromFile(posterFile))
```

---

## 🎯 Flujos principales

### Flujo 1: Buscar película en MovieManager

```
getMovieById(movieId)
    ├─ movies.find { it.id == movieId }
    └─ Retorna Movie o null
    
Uso:
    val movie = MovieManager.getMovieById(1)
    if (movie != null) {
        // Mostrar datos
    }
```

### Flujo 2: Cargar imagen

```
if (!movie.posterFileName.isNullOrEmpty()) {
    val posterFile = File(itemView.context.filesDir, movie.posterFileName)
    if (posterFile.exists()) {
        imageView.setImageURI(Uri.fromFile(posterFile))
    } else {
        imageView.setImageResource(movie.posterResId)  // Fallback
    }
} else {
    imageView.setImageResource(movie.posterResId)
}
```

### Flujo 3: Validar película

```
// En MovieEditActivity
if (newTitle.isEmpty()) {
    Toast.makeText(this, "El título no puede estar vacío", 
                   Toast.LENGTH_SHORT).show()
    return  // No guardar
}

movie = Movie(
    id = if(isNew) generateUniqueId() else movieId,
    title = newTitle,
    year = newYear,
    posterResId = 0,  // Por defecto
    synopsis = newSynopsis,
    rating = newRating,
    cast = newCast,
    posterFileName = savedFileName
)
```

---

## 🔌 Integración de componentes

### MovieListActivity ↔ MovieManager

```
onCreate() {
    val movies = MovieManager.getMovies()
    adapter = MovieAdapter(movies) { movie ->
        // Navegar a detalles
    }
    recyclerView.adapter = adapter
}
```

### MovieDetailActivity ↔ MovieManager

```
displayMovieDetails() {
    val movie = MovieManager.getMovieById(movieId)  ← Consulta
    if (movie != null) {
        // Mostrar datos
        // Cargar imagen
    }
}
```

### MovieEditActivity ↔ MovieManager

```
btnSave.setOnClickListener {
    val newMovie = Movie(...)
    
    if (isNew) {
        MovieManager.addMovie(newMovie)  ← Agrega
    } else {
        MovieManager.updateMovie(newMovie)  ← Actualiza
    }
    
    finish()  ← Retorna a Activity anterior
}
```

### Activity Result API (flujo moderno)

```
// En MovieDetailActivity
val editMovieLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result →
    if (result.resultCode == RESULT_OK) {
        displayMovieDetails()  ← Recargar datos
    }
}

// Al editar
editMovieLauncher.launch(intent)  ← Abrir MovieEditActivity
```

---

## 📱 Navegación entre Activities

### Stack de Activities

```
Inicio de app:
┌──────────────────────┐
│ MovieListActivity    │ ← Current
└──────────────────────┘

Ir a detalles:
┌──────────────────────┐
│ MovieDetailActivity  │ ← Current
├──────────────────────┤
│ MovieListActivity    │ (Back stack)
└──────────────────────┘

Ir a editar:
┌──────────────────────┐
│ MovieEditActivity    │ ← Current
├──────────────────────┤
│ MovieDetailActivity  │ (Back stack)
├──────────────────────┤
│ MovieListActivity    │ (Back stack)
└──────────────────────┘

Presionar back:
- MovieEditActivity.finish()
  └─ MovieDetailActivity (vuelve)
- MovieDetailActivity.finish()
  └─ MovieListActivity (vuelve)
```

---

## 🎨 Adaptación UI al teclado

### NestedScrollView

```
MovieEditActivity (activity_movie_edit.xml)
    ├─ ConstraintLayout (main)
    ├─ LinearLayout (header)
    ├─ NestedScrollView
    │   └─ LinearLayout
    │       ├─ ImageView (poster preview)
    │       ├─ Button (seleccionar)
    │       ├─ CardView (título)
    │       ├─ CardView (año)
    │       ├─ CardView (sinopsis)
    │       ├─ CardView (puntuación)
    │       └─ CardView (reparto)
    │           └─ marginBottom = 100dp  ← Espacio para teclado
    └─ LinearLayout (botones inferiores)
```

Cuando aparece el teclado:
1. `android:windowSoftInputMode="adjustPan"` mueve contenido hacia arriba
2. NestedScrollView permite scroll para ver todos los campos
3. El margen inferior (100dp) evita que el último campo quede bajo el teclado

---

## 🔐 Manejo de errores

### NullPointerException - posterFileName

```
Problema:
    movie.posterFileName.length() en null

Soluciones implementadas:
    1. En MovieAdapter:
       if (!movie.posterFileName.isNullOrEmpty()) { ... }
    
    2. En MovieDetailActivity:
       if (!movie.posterFileName.isNullOrEmpty()) { ... }
    
    3. Al pasar Intent:
       intent.putExtra("movie_poster_file", movie.posterFileName ?: "")
```

### Imagen no encontrada

```
Problema:
    Archivo de imagen no existe en filesDir

Solución:
    if (posterFile.exists()) {
        imageView.setImageURI(Uri.fromFile(posterFile))
    } else {
        imageView.setImageResource(movie.posterResId)  ← Fallback
    }
```

---

## 📊 Ejemplo: Flujo completo - Agregar película

```
1. Usuario abre app
   └─ MyApplication.onCreate() → Cargar películas

2. Ve lista en MovieListActivity
   └─ RecyclerView muestra 3 películas por defecto

3. Pulsa "Agregar Película"
   └─ Intent → MovieEditActivity (is_new=true)

4. Rellena formulario
   ├─ Título: "Pulp Fiction"
   ├─ Año: 1994
   ├─ Sinopsis: "..."
   ├─ Puntuación: 8.9
   └─ Reparto: "John Travolta, Uma Thurman, Samuel L. Jackson"

5. Pulsa "Seleccionar Póster"
   └─ Gallery abre → Selecciona imagen

6. Ve preview del póster en ImageView

7. Pulsa "Guardar"
   ├─ savePosterImage(posterUri)
   │   ├─ Copia imagen a filesDir
   │   └─ Retorna: "poster_1700277600000.jpg"
   ├─ Crea Movie:
   │   ├─ id = 4 (generado único)
   │   ├─ title = "Pulp Fiction"
   │   ├─ year = 1994
   │   ├─ synopsis = "..."
   │   ├─ rating = 8.9
   │   ├─ cast = "..."
   │   └─ posterFileName = "poster_1700277600000.jpg"
   ├─ MovieManager.addMovie(movie)
   ├─ saveMoviesToFile()
   │   └─ movies.json (ahora con 4 películas)
   └─ finish() → vuelve a MovieListActivity

8. RecyclerView se actualiza
   └─ Muestra 4 películas (la nueva aparece en la lista)

9. Usuario toca la película nueva
   └─ MovieDetailActivity muestra todos los datos
      ├─ Imagen desde filesDir
      └─ Información completa

10. Película guardada en JSON
    ├─ En memoria (movies list)
    └─ En disco (movies.json + imagen en filesDir)
```

---

## 🔄 Ciclo de persistencia

```
Inicial (primera vez):
    └─ initializeDefaultMovies()
       └─ saveMoviesToFile()
          └─ Crear movies.json

Agregar película:
    ├─ Movie nuevo
    ├─ addMovie(movie)
    ├─ saveMoviesToFile()
    └─ Actualizar movies.json

Editar película:
    ├─ Modificar datos
    ├─ updateMovie(movie)
    ├─ saveMoviesToFile()
    └─ Actualizar movies.json

Eliminar película:
    ├─ Eliminar de lista
    ├─ deleteMovie(id)
    ├─ saveMoviesToFile()
    └─ Actualizar movies.json

Cerrar app:
    └─ movies.json se mantiene en filesDir

Abrir app nuevamente:
    ├─ MyApplication.initialize()
    ├─ loadMoviesFromFile()
    ├─ Leer movies.json
    └─ Restaurar movies list
```

---

## 🎯 Puntos clave del flujo

✅ **Singleton pattern** - MovieManager único en toda la app
✅ **Persistencia automática** - Cada cambio se guarda
✅ **Activity Result API** - Navegación moderna y segura
✅ **Fallback en imágenes** - Si imagen no existe, usar drawable
✅ **Validación de datos** - Campos vacíos no se guardan
✅ **Manejo de null** - isNullOrEmpty() para seguridad
✅ **Stack de Activities** - Navegación con back stack correcto
✅ **NestedScrollView** - Scroll suave con teclado

---

**Este flujo mantiene la aplicación funcional, segura y fácil de mantener.** 🚀
