package com.example.rutapedidos.SyncServices

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.rutapedidos.DataAccessObjects.PedidoDao
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

class PedidoSyncService(context: Context) {
    private val contexto = context.applicationContext
    private val pedidoDao = PedidoDao(contexto)
    private val mainHandler = Handler(Looper.getMainLooper())

    fun sincronizar(onSuccess: (Int, Int) -> Unit, onError: (Exception) -> Unit) {
        if (!enCurso.compareAndSet(false, true)) {
            onError(IllegalStateException("Ya hay una sincronización de pedidos en curso"))
            return
        }
        Thread {
            var sincronizados = 0
            try {
                val auth = FirebaseAuth.getInstance()
                val uid = auth.currentUser?.uid
                    ?: throw IllegalStateException("Debe iniciar sesión para sincronizar")
                val preferencias = contexto.getSharedPreferences("sincronizacion_pedidos", Context.MODE_PRIVATE)
                val dispositivoId = preferencias.getString("dispositivo_id", null) ?: UUID.randomUUID().toString().also {
                    check(preferencias.edit().putString("dispositivo_id", it).commit()) {
                        "No se pudo guardar el identificador del dispositivo"
                    }
                }
                val firestore = FirebaseFirestore.getInstance()
                for (copia in pedidoDao.obtenerPendientesParaSincronizar(uid)) {
                    check(auth.currentUser?.uid == uid) { "La sesión cambió. Vuelva a sincronizar" }
                    val referencia = firestore.collection("pedidos").document(copia.idDocumento(dispositivoId))
                    val datos = copia.datosFirestore(dispositivoId) +
                        ("actualizadoEn" to FieldValue.serverTimestamp())
                    // La transacción necesita conexión y confirma la cabecera y el detalle juntos.
                    Tasks.await(firestore.runTransaction { transaccion ->
                        val existente = transaccion.get(referencia)
                        check(!existente.exists() || existente.getString("firebaseUid") == uid) {
                            "El pedido de destino pertenece a otro usuario"
                        }
                        transaccion.set(referencia, datos)
                        true
                    })
                    if (pedidoDao.marcarSincronizadoSiNoCambio(copia)) sincronizados++
                }
                val pendientes = pedidoDao.obtenerPendientesParaSincronizar(uid).size
                mainHandler.post { onSuccess(sincronizados, pendientes) }
            } catch (e: Exception) {
                val causa = e.cause ?: e
                val error = Exception("$sincronizados pedidos sincronizados. ${causa.message ?: "No se pudo conectar con Firebase"}", e)
                mainHandler.post { onError(error) }
            } finally {
                enCurso.set(false)
            }
        }.start()
    }

    companion object {
        private val enCurso = AtomicBoolean(false)
    }
}
