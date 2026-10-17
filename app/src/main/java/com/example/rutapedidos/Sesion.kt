package com.example.rutapedidos

import android.content.Context
import com.example.rutapedidos.DataAccessObjects.UsuarioDao
import com.google.firebase.auth.FirebaseAuth

object Sesion {

    fun idUsuarioLocal(context: Context): Long? {
        val user = FirebaseAuth.getInstance().currentUser ?: return null
        return UsuarioDao(context).obtenerOInsertar(user.uid, user.email ?: "")
    }
}