package com.example.rutapedidos.SyncServices

import android.os.Handler
import android.os.Looper
import com.example.rutapedidos.Models.Cliente
import com.example.rutapedidos.DataAccessObjects.ClienteDao
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class ClienteSyncService(private var clienteDao: ClienteDao) {

    private var firestore = FirebaseFirestore.getInstance()

    private var mainHandler = Handler(Looper.getMainLooper())

    fun sincronizar(onSuccess: (Int) -> Unit, onError: (Exception) -> Unit) {

        // 1. Obtener clientes desde Cloud Firestore
        firestore.collection("clientes")
            .get(Source.SERVER)
            .addOnSuccessListener { snapshot ->

                try {

                    // 2. Convertir documentos a objetos Cliente
                    var clientes = snapshot.documents.map { doc ->

                        Cliente(
                            idCliente = Math.toIntExact(
                                doc.getLong("idCliente")
                                    ?: throw IllegalArgumentException(
                                        "Cliente sin idCliente: ${doc.id}"
                                    )
                            ),
                            rut = doc.getString("rut") ?: "",
                            razonSocial = doc.getString("razonSocial") ?: "",
                            direccion = doc.getString("direccion") ?: "",
                            comuna = doc.getString("comuna") ?: "",
                            telefono = doc.getString("telefono") ?: "",
                            diaVisita = doc.getString("diaVisita") ?: ""
                        )
                    }

                    // 3. Guardar en SQLite en un hilo secundario
                    Thread {

                        try {

                            clienteDao.guardarTodos(clientes)

                            // 4. Notificar éxito en el hilo principal
                            mainHandler.post {
                                onSuccess(clientes.size)
                            }

                        } catch (e: Exception) {

                            mainHandler.post {
                                onError(e)
                            }
                        }

                    }.start()

                } catch (e: Exception) {
                    onError(e)
                }
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }
}