package com.example.rutapedidos.Models

data class DetallePedido(
    var idDetalle: Int,
    var idPedido: Int,
    var idProducto: Int,
    var cantidad: Int,
    var precioUnitario: Int,
    var porcentajeDescuento: Double,
    var descuento: Int,
    var neto: Int,
    var impuesto: Int,
    var total: Int
)