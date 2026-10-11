package com.example.rutapedidos.SyncServices

import android.os.Handler
import android.os.Looper
import com.example.rutapedidos.Models.Producto
import com.example.rutapedidos.DataAccessObjects.ProductoDao
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class ProductoSyncService(private var productoDao: ProductoDao) {

    private var firestore = FirebaseFirestore.getInstance()

    private var mainHandler = Handler(Looper.getMainLooper())

    fun sincronizar(onSuccess: (Int) -> Unit, onError: (Exception) -> Unit) {

        // 1. Obtener productos desde Cloud Firestore
        firestore.collection("productos")
            .get(Source.SERVER)
            .addOnSuccessListener { snapshot ->

                try {

                    // 2. Convertir documentos a objetos Producto
                    var productos = snapshot.documents.map { doc ->

                        Producto(
                            idProducto = Math.toIntExact(
                                doc.getLong("idProducto")
                                    ?: throw IllegalArgumentException(
                                        "Producto sin idProducto: ${doc.id}"
                                    )
                            ),
                            codigo = doc.getString("codigo") ?: "",
                            nombre = doc.getString("nombre") ?: "",
                            unidadMedida =
                                doc.getString("unidadMedida") ?: "",
                            precio = Math.toIntExact(
                                doc.getLong("precio") ?: 0L
                            ),
                            stock = Math.toIntExact(
                                doc.getLong("stock") ?: 0L
                            )
                        )
                    }

                    // 3. Guardar productos en SQLite
                    Thread {

                        try {

                            productoDao.guardarTodos(productos)

                            // 4. Notificar éxito
                            mainHandler.post {
                                onSuccess(productos.size)
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
