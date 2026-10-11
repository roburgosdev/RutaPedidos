package com.example.rutapedidos.Models

data class Producto(
    var idProducto: Int,
    var codigo: String,
    var nombre: String,
    var unidadMedida: String,
    var precio: Int,
    var stock: Int
)