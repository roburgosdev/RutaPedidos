package com.example.rutapedidos.DataAccessObjects

import android.content.ContentValues
import android.content.Context
import com.example.rutapedidos.Models.DetallePedido
import com.example.rutapedidos.SQL.miSQLiteHelper

class DetallePedidoDao(context: Context) {

    private val helper = miSQLiteHelper.obtener(context)

    fun obtenerTodosPorId(idPedido: Int): List<DetallePedido> {
        var resultado = mutableListOf<DetallePedido>()
        var db = helper.readableDatabase

        db.query("detalle_pedido", null, "id_pedido = ?", arrayOf(idPedido.toString()), null, null, null).use { fila ->
            while (fila.moveToNext()) {
                resultado.add(
                    DetallePedido(
                        idDetalle = fila.getString(fila.getColumnIndexOrThrow("id_detalle")).toInt(),
                        idPedido = fila.getString(fila.getColumnIndexOrThrow("id_pedido")).toInt(),
                        idProducto = fila.getString(fila.getColumnIndexOrThrow("id_producto")).toInt(),
                        cantidad = fila.getString(fila.getColumnIndexOrThrow("cantidad")).toInt(),
                        precioUnitario = fila.getString(fila.getColumnIndexOrThrow("precio_unitario")).toInt(),
                        porcentajeDescuento = fila.getString(fila.getColumnIndexOrThrow("porcentaje_descuento")).toDouble(),
                        descuento = fila.getString(fila.getColumnIndexOrThrow("descuento")).toInt(),
                        neto = fila.getString(fila.getColumnIndexOrThrow("neto")).toInt(),
                        impuesto = fila.getString(fila.getColumnIndexOrThrow("impuesto")).toInt(),
                        total = fila.getString(fila.getColumnIndexOrThrow("total")).toInt(),
                    )
                )
            }
        }
        return resultado
    }

    fun guardarUno(detalle_pedido: DetallePedido): Long {
        val db = helper.writableDatabase

        var datos = ContentValues()
        //datos.put("id_detalle", detalle_pedido.idDetalle)
        datos.put("id_pedido", detalle_pedido.idPedido)
        datos.put("id_producto", detalle_pedido.idProducto)
        datos.put("cantidad", detalle_pedido.cantidad)
        datos.put("precio_unitario", detalle_pedido.precioUnitario)
        datos.put("porcentaje_descuento", detalle_pedido.porcentajeDescuento)
        datos.put("descuento", detalle_pedido.descuento)
        datos.put("neto", detalle_pedido.neto)
        datos.put("impuesto", detalle_pedido.impuesto)
        datos.put("total", detalle_pedido.total)

        return db.insertOrThrow("detalle_pedido", null, datos)
    }
}