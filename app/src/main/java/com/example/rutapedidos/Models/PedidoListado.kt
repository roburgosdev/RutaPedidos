package com.example.rutapedidos.Models

data class PedidoListado(
    val pedido: Pedido,
    val cliente: Cliente?,
    val emailCreador: String?
)
