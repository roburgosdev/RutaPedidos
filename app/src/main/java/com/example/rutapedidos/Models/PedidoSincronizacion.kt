package com.example.rutapedidos.Models

data class PedidoSincronizacion(val pedido: Pedido, val detalles: List<DetallePedido>) {
    fun idDocumento(dispositivoId: String): String =
        "${pedido.fireBaseUID}_${dispositivoId}_${pedido.idPedido}"

    fun datosFirestore(dispositivoId: String): Map<String, Any> = mapOf(
        "idPedido" to pedido.idPedido,
        "idCliente" to pedido.idCliente,
        "idUsuario" to pedido.idUsuario,
        "firebaseUid" to pedido.fireBaseUID,
        "dispositivoId" to dispositivoId,
        "fechaEmision" to pedido.fechaEmision.toString(),
        "fechaEntrega" to pedido.fechaEntrega.toString(),
        "ordenCompra" to pedido.ordenCompra,
        "neto" to pedido.neto,
        "descuento" to pedido.descuento,
        "impuesto" to pedido.impuesto,
        "total" to pedido.total,
        "sincronizado" to true,
        "detalles" to detalles.map { detalle ->
            mapOf(
                "idDetalle" to detalle.idDetalle,
                "idPedido" to detalle.idPedido,
                "idProducto" to detalle.idProducto,
                "cantidad" to detalle.cantidad,
                "precioUnitario" to detalle.precioUnitario,
                "porcentajeDescuento" to detalle.porcentajeDescuento,
                "descuento" to detalle.descuento,
                "neto" to detalle.neto,
                "impuesto" to detalle.impuesto,
                "total" to detalle.total
            )
        }
    )
}
