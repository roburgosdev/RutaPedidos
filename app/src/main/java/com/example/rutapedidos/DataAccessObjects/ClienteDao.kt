package com.example.rutapedidos.DataAccessObjects


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.rutapedidos.Models.Cliente
import com.example.rutapedidos.SQL.miSQLiteHelper

class ClienteDao(context: Context) {

    private val helper = miSQLiteHelper.obtener(context)

    fun guardarTodos(clientes: List<Cliente>) {
        val db = helper.writableDatabase

        db.beginTransaction()
        try {
            clientes.forEach { cliente ->
                val values = ContentValues().apply {
                    put("id_cliente", cliente.idCliente)
                    put("rut", cliente.rut)
                    put("razon_social", cliente.razonSocial)
                    put("direccion", cliente.direccion)
                    put("comuna", cliente.comuna)
                    put("telefono", cliente.telefono)
                    put("dia_visita", cliente.diaVisita)
                }

                val filas = db.update("cliente",values,"id_cliente = ?", arrayOf(cliente.idCliente.toString()))

                if(filas == 0){
                    db.insertOrThrow("cliente", null, values)
                }
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun obtenerTodos(): List<Cliente> {
        var resultado = mutableListOf<Cliente>()
        var db = helper.readableDatabase

        db.rawQuery("SELECT * FROM cliente", null).use{ fila ->
            while (fila.moveToNext()) {
                resultado.add(
                    Cliente(
                        idCliente = fila.getString(fila.getColumnIndexOrThrow("id_cliente")).toInt(),
                        rut = fila.getString(fila.getColumnIndexOrThrow("rut")),
                        razonSocial = fila.getString(fila.getColumnIndexOrThrow("razon_social")),
                        direccion = fila.getString(fila.getColumnIndexOrThrow("direccion")),
                        comuna = fila.getString(fila.getColumnIndexOrThrow("comuna")),
                        telefono = fila.getString(fila.getColumnIndexOrThrow("telefono")),
                        diaVisita = fila.getString(fila.getColumnIndexOrThrow("dia_visita"))
                    )
                )
            }
        }

        return resultado
    }
}
