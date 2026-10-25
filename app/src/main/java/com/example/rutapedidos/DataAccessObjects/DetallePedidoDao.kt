package com.example.rutapedidos.DataAccessObjects

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.rutapedidos.Models.CalculoDetallePedido
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

        db.beginTransaction()
        try {
            val id = db.insertOrThrow("detalle_pedido", null, datos)
            actualizarTotales(db, detalle_pedido.idPedido)
            db.setTransactionSuccessful()
            return id
        } finally {
            db.endTransaction()
        }
    }

    fun actualizar(idPedido: Int, idDetalle: Int, cantidad: Int, porcentaje: Double) {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val actual = obtenerTodosPorId(idPedido).firstOrNull { it.idDetalle == idDetalle }
                ?: throw IllegalArgumentException("El producto ya no está en el pedido")
            val actualizado = CalculoDetallePedido.actualizar(actual, cantidad, porcentaje)
            db.query("producto", arrayOf("stock"), "id_producto = ?",
                arrayOf(actual.idProducto.toString()), null, null, null).use { cursor ->
                require(cursor.moveToFirst()) { "Producto no encontrado" }
                val stock = cursor.getInt(0)
                require(cantidad <= stock) { "Stock insuficiente. Disponible: $stock" }
            }
            val datos = ContentValues().apply {
                put("cantidad", actualizado.cantidad)
                put("porcentaje_descuento", actualizado.porcentajeDescuento)
                put("descuento", actualizado.descuento)
                put("neto", actualizado.neto)
                put("impuesto", actualizado.impuesto)
                put("total", actualizado.total)
            }
            check(db.update("detalle_pedido", datos, "id_detalle = ? AND id_pedido = ?",
                arrayOf(idDetalle.toString(), idPedido.toString())) == 1)
            actualizarTotales(db, idPedido)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun eliminar(idPedido: Int, idDetalle: Int) {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            check(db.delete("detalle_pedido", "id_detalle = ? AND id_pedido = ?",
                arrayOf(idDetalle.toString(), idPedido.toString())) == 1) {
                "El producto ya no está en el pedido"
            }
            actualizarTotales(db, idPedido)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun actualizarTotales(db: SQLiteDatabase, idPedido: Int) {
        db.rawQuery("""
            SELECT COALESCE(SUM(neto), 0), COALESCE(SUM(descuento), 0),
                   COALESCE(SUM(impuesto), 0), COALESCE(SUM(total), 0)
            FROM detalle_pedido WHERE id_pedido = ?
        """.trimIndent(), arrayOf(idPedido.toString())).use { cursor ->
            cursor.moveToFirst()
            val datos = ContentValues().apply {
                put("neto", Math.toIntExact(cursor.getLong(0)))
                put("descuento", Math.toIntExact(cursor.getLong(1)))
                put("impuesto", Math.toIntExact(cursor.getLong(2)))
                put("total", Math.toIntExact(cursor.getLong(3)))
                put("sincronizado", 0)
            }
            check(db.update("pedido", datos, "id_pedido = ?", arrayOf(idPedido.toString())) == 1)
        }
    }
}
