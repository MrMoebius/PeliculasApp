package com.example.peliculasapp.data

data class Movie(
    val id: Int,
    var title: String,
    var year: Int,
    var posterResId: Int,           // ID del recurso drawable (R.drawable.nombre)
    var synopsis: String,
    var rating: Double,             // Ejemplo: 8.5
    var cast: String,               // Ejemplo: "Actor 1, Actor 2, Actor 3"
    val posterFileName: String = "" // Nombre del archivo de imagen (sin extensión)
)
