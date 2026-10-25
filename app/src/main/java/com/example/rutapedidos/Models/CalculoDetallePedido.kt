package com.example.rutapedidos.Models

object CalculoDetallePedido {
    fun actualizar(detalle: DetallePedido, cantidad: Int, porcentaje: Double): DetallePedido {
        require(cantidad > 0) { "Ingrese una cantidad válida" }
        require(porcentaje.isFinite() && porcentaje in 0.0..100.0) {
            "El descuento debe estar entre 0 y 100"
        }
        val bruto = Math.multiplyExact(cantidad, detalle.precioUnitario)
        val descuento = Math.round(bruto * porcentaje / 100).toInt()
        val neto = Math.subtractExact(bruto, descuento)
        val impuesto = Math.round(neto * 0.19).toInt()
        val total = Math.addExact(neto, impuesto)
        return detalle.copy(
            cantidad = cantidad, porcentajeDescuento = porcentaje, descuento = descuento,
            neto = neto, impuesto = impuesto, total = total
        )
    }
}
