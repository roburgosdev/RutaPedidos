package com.example.rutapedidos.DataAccessObjects

import android.content.ContentValues
import android.content.Context
import com.example.rutapedidos.Models.Usuario
import com.example.rutapedidos.SQL.miSQLiteHelper

class UsuarioDao(context: Context) {

    private val helper = miSQLiteHelper.obtener(context)

    fun obtenerPorUid(uid: String): Usuario? {
        val db = helper.readableDatabase
        db.query("usuario", null, "firebase_uid = ?", arrayOf(uid), null, null, null).use { c ->
            if (!c.moveToFirst()) return null
            return Usuario(
                idUsuario = c.getInt(c.getColumnIndexOrThrow("id_usuario")),
                fireBaseUID = c.getString(c.getColumnIndexOrThrow("firebase_uid")),
                rut = c.getString(c.getColumnIndexOrThrow("rut")) ?: "",
                nombre = c.getString(c.getColumnIndexOrThrow("nombre")) ?: "",
                email = c.getString(c.getColumnIndexOrThrow("email"))
            )
        }
    }

    fun insertar(uid: String, email: String): Long {
        val values = ContentValues().apply {
            put("firebase_uid", uid)
            put("email", email)
        }
        return helper.writableDatabase.insertOrThrow("usuario", null, values)
    }

    fun obtenerOInsertar(uid: String, email: String): Long {
        val existente = obtenerPorUid(uid)
        return existente?.idUsuario?.toLong() ?: insertar(uid, email)
    }
}