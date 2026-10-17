package com.example.rutapedidos.DataAccessObjects

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.rutapedidos.Models.Producto
import com.example.rutapedidos.SQL.miSQLiteHelper

class ProductoDao(context: Context) {

    private val helper = miSQLiteHelper.obtener(context)

    fun guardarTodos(productos: List<Producto>) {
        val db = helper.writableDatabase

        db.beginTransaction()
        try {
            productos.forEach { producto ->
                val values = ContentValues().apply {
                    put("id_producto", producto.idProducto)
                    put("codigo", producto.codigo)
                    put("nombre", producto.nombre)
                    put("unidad_medida", producto.unidadMedida)
                    put("precio", producto.precio)
                    put("stock", producto.stock)
                }

                val filas = db.update("producto", values, "id_producto = ?", arrayOf(producto.idProducto.toString()))

                if(filas == 0){
                    db.insertOrThrow("producto", null, values)
                }
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun obtenerTodos(): List<Producto> {
        var resultado = mutableListOf<Producto>()
        var db = helper.readableDatabase

        db.rawQuery("SELECT * FROM producto", null).use { fila ->
            while (fila.moveToNext()) {
                resultado.add(
                    Producto(
                        idProducto = fila.getString(fila.getColumnIndexOrThrow("id_producto")).toInt(),
                        codigo = fila.getString(fila.getColumnIndexOrThrow("codigo")),
                        nombre = fila.getString(fila.getColumnIndexOrThrow("nombre")),
                        unidadMedida = fila.getString(fila.getColumnIndexOrThrow("unidad_medida")),
                        precio = fila.getString(fila.getColumnIndexOrThrow("precio")).toInt(),
                        stock = fila.getString(fila.getColumnIndexOrThrow("stock")).toInt()
                    )
                )
            }
        }

        return resultado
    }
}
