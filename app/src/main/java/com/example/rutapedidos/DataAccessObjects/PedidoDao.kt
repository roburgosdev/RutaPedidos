package com.example.rutapedidos.DataAccessObjects

import android.content.ContentValues
import android.content.Context
import com.example.rutapedidos.Models.Pedido
import com.example.rutapedidos.Models.PedidoSincronizacion
import com.example.rutapedidos.SQL.miSQLiteHelper
import java.time.LocalDate

class PedidoDao(context: Context) {

    private val helper = miSQLiteHelper.obtener(context)
    private val detalleDao = DetallePedidoDao(context.applicationContext)

    fun obtenerPendientesParaSincronizar(uid: String): List<PedidoSincronizacion> {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val resultado = obtenerTodos().filter { !it.sincronizado && it.fireBaseUID == uid }.map {
                PedidoSincronizacion(it, detalleDao.obtenerTodosPorId(it.idPedido).sortedBy { d -> d.idDetalle })
            }
            db.setTransactionSuccessful()
            return resultado
        } finally {
            db.endTransaction()
        }
    }

    // No marcar cambios locales que se hayan hecho mientras se estaba subiendo la copia.
    fun marcarSincronizadoSiNoCambio(copia: PedidoSincronizacion): Boolean {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val actual = obtenerUno(copia.pedido.idPedido)
            val detalles = detalleDao.obtenerTodosPorId(copia.pedido.idPedido).sortedBy { it.idDetalle }
            val coincide = actual == copia.pedido && detalles == copia.detalles
            if (coincide) {
                val datos = ContentValues().apply { put("sincronizado", 1) }
                check(db.update("pedido", datos, "id_pedido = ?",
                    arrayOf(copia.pedido.idPedido.toString())) == 1)
            }
            db.setTransactionSuccessful()
            return coincide
        } finally {
            db.endTransaction()
        }
    }

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
        datos.put("sincronizado", if (pedido.sincronizado) 1 else 0)

        return db.insertOrThrow("pedido", null, datos)
    }

    fun actualizarEntregaYOrdenCompra(idPedido: Int, fechaEntrega: LocalDate, ordenCompra: String) {
        val datos = ContentValues().apply {
            put("fecha_entrega", fechaEntrega.toString())
            put("orden_compra", ordenCompra.trim())
            put("sincronizado", 0)
        }
        check(helper.writableDatabase.update(
            "pedido", datos, "id_pedido = ?", arrayOf(idPedido.toString())
        ) == 1) { "Pedido no encontrado" }
    }

    fun eliminarConDetalles(idPedido: Int) {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val argumentos = arrayOf(idPedido.toString())
            db.delete("detalle_pedido", "id_pedido = ?", argumentos)
            check(db.delete("pedido", "id_pedido = ?", argumentos) == 1) {
                "Pedido no encontrado"
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
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
                sincronizado = c.getInt(c.getColumnIndexOrThrow("sincronizado")) == 1
            )
        }
    }
}
