package com.example.rutapedidos.DataAccessObjects

import android.content.ContentValues
import android.content.Context
import com.example.rutapedidos.Models.Pedido
import com.example.rutapedidos.Models.Producto
import com.example.rutapedidos.Models.Usuario
import com.example.rutapedidos.Pedidos
import com.example.rutapedidos.SQL.miSQLiteHelper
import java.time.LocalDate

class PedidoDao(context: Context) {

    private val helper = miSQLiteHelper.obtener(context)

    fun guardarTodos(pedidos: List<Pedido>) {
        val db = helper.writableDatabase

        db.beginTransaction()
        try {
            pedidos.forEach { pedido ->
                val values = ContentValues().apply {
                    put("id_pedido", pedido.idPedido)
                    put("id_cliente", pedido.idCliente)
                    put("id_usuario", pedido.idUsuario)
                    put("firebase_uid", pedido.fireBaseUID)
                    put("fecha_emision", pedido.fechaEmision.toString())
                    put("fecha_entrega", pedido.fechaEntrega.toString())
                    put("orden_compra", pedido.ordenCompra)
                    put("neto", pedido.neto)
                    put("descuento", pedido.descuento)
                    put("impuesto", pedido.impuesto)
                    put("total", pedido.total)
                    put("estado", pedido.estado)
                    put("sincronizado", if (pedido.sincronizado) 1 else 0)
                }

                val filas = db.update("pedido", values, "id_pedido = ?", arrayOf(pedido.idPedido.toString()))

                if(filas == 0){
                    db.insertOrThrow("pedido", null, values)
                }
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun obtenerTodos(): List<Pedido> {
        var resultado = mutableListOf<Pedido>()
        var db = helper.readableDatabase

        db.rawQuery("SELECT * FROM pedido", null).use { fila ->
            while (fila.moveToNext()) {
                resultado.add(
                    Pedido(
                        idPedido = fila.getString(fila.getColumnIndexOrThrow("id_pedido")).toInt(),
                        idCliente = fila.getString(fila.getColumnIndexOrThrow("id_cliente")).toInt(),
                        idUsuario = fila.getString(fila.getColumnIndexOrThrow("id_usuario")).toInt(),
                        fireBaseUID = fila.getString(fila.getColumnIndexOrThrow("firebase_uid")),
                        fechaEmision = LocalDate.parse(fila.getString(fila.getColumnIndexOrThrow("fecha_emision"))),
                        fechaEntrega = LocalDate.parse(fila.getString(fila.getColumnIndexOrThrow("fecha_entrega"))),
                        ordenCompra = fila.getString(fila.getColumnIndexOrThrow("orden_compra")),
                        neto = fila.getString(fila.getColumnIndexOrThrow("neto")).toInt(),
                        descuento = fila.getString(fila.getColumnIndexOrThrow("descuento")).toInt(),
                        impuesto = fila.getString(fila.getColumnIndexOrThrow("impuesto")).toInt(),
                        total = fila.getString(fila.getColumnIndexOrThrow("total")).toInt(),
                        estado = fila.getString(fila.getColumnIndexOrThrow("estado")),
                        sincronizado = fila.getInt(fila.getColumnIndexOrThrow("sincronizado")) == 1
                    )
                )
            }
        }
        return resultado
    }

    fun guardarUno(pedido: Pedido): Long{
        val db = helper.writableDatabase

        var datos = ContentValues()
        //datos.put("id_pedido", pedido.idPedido)
        datos.put("id_cliente", pedido.idCliente)
        datos.put("id_usuario", pedido.idUsuario)
        datos.put("firebase_uid", pedido.fireBaseUID)
        datos.put("fecha_emision", pedido.fechaEmision.toString())
        datos.put("fecha_entrega", pedido.fechaEntrega.toString())
        datos.put("orden_compra", pedido.ordenCompra)
        datos.put("neto", pedido.neto)
        datos.put("descuento", pedido.descuento)
        datos.put("impuesto", pedido.impuesto)
        datos.put("total", pedido.total)
        datos.put("estado", pedido.estado)
        datos.put("sincronizado", if (pedido.sincronizado) 1 else 0)

        return db.insertOrThrow("pedido", null, datos)
    }

    fun obtenerUno(idPedido: Int): Pedido? {
        var db = helper.readableDatabase

        db.query("pedido", null, "id_pedido = ?", arrayOf(idPedido.toString()), null, null, null).use { c ->
            if (!c.moveToFirst()) return null
            return Pedido(
                idPedido = c.getString(c.getColumnIndexOrThrow("id_pedido")).toInt(),
                idCliente = c.getString(c.getColumnIndexOrThrow("id_cliente")).toInt(),
                idUsuario = c.getString(c.getColumnIndexOrThrow("id_usuario")).toInt(),
                fireBaseUID = c.getString(c.getColumnIndexOrThrow("firebase_uid")),
                fechaEmision = LocalDate.parse(c.getString(c.getColumnIndexOrThrow("fecha_emision"))),
                fechaEntrega = LocalDate.parse(c.getString(c.getColumnIndexOrThrow("fecha_entrega"))),
                ordenCompra = c.getString(c.getColumnIndexOrThrow("orden_compra")),
                neto = c.getString(c.getColumnIndexOrThrow("neto")).toInt(),
                descuento = c.getString(c.getColumnIndexOrThrow("descuento")).toInt(),
                impuesto = c.getString(c.getColumnIndexOrThrow("impuesto")).toInt(),
                total = c.getString(c.getColumnIndexOrThrow("total")).toInt(),
                estado = c.getString(c.getColumnIndexOrThrow("estado")),
                sincronizado = c.getInt(c.getColumnIndexOrThrow("sincronizado")) == 1
            )
        }
    }
}
