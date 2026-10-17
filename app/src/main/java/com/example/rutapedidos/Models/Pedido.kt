package com.example.rutapedidos.Models

import java.time.LocalDate

data class Pedido(
    var idPedido: Int,
    var idCliente: Int,
    var idUsuario: Int,
    var fireBaseUID: String,
    var fechaEmision: LocalDate,
    var fechaEntrega: LocalDate,
    var ordenCompra: String,
    var neto: Int,
    var descuento: Int,
    var impuesto: Int,
    var total: Int,
    var estado: String,
    var sincronizado: Boolean
)