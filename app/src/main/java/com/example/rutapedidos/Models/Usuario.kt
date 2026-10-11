package com.example.rutapedidos.Models

data class Usuario (
    var idUsuario: Int,
    var fireBaseUID: String,
    var rut: String,
    var nombre: String,
    var email: String
)