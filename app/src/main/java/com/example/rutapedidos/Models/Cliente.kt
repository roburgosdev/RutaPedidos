package com.example.rutapedidos.Models

data class Cliente(
    var idCliente: Int,
    var rut: String,
    var razonSocial: String,
    var direccion: String,
    var comuna: String,
    var telefono: String,
    var diaVisita: String
)