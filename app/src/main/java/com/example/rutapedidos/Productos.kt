package com.example.rutapedidos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rutapedidos.DataAccessObjects.ProductoDao
import com.example.rutapedidos.Fragments.ListaFragment
import com.example.rutapedidos.Interfaces.ListadosFragment
import com.example.rutapedidos.SyncServices.ProductoSyncService

class Productos : AppCompatActivity(), ListadosFragment {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_productos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var btnVolver = findViewById<Button>(R.id.btnProductosVolver)
        var btnSincronizar = findViewById<Button>(R.id.btnProductosSincronizar)

        btnVolver.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnSincronizar.setOnClickListener {
            var productoDao = ProductoDao(this)
            var productoSync = ProductoSyncService(productoDao)

            btnSincronizar.isEnabled = false
            btnSincronizar.setText(R.string.pedidos_sincronizando)

            // Sincronizar productos
            productoSync.sincronizar(
                onSuccess = { cantidad ->
                    btnSincronizar.isEnabled = true
                    btnSincronizar.setText(R.string.pedidos_sincronizar)

                    var fragment = supportFragmentManager.findFragmentById(R.id.fragProductosLista) as? ListaFragment
                    fragment?.actualizarLista()

                    Toast.makeText(
                        this,
                        "$cantidad productos sincronizados",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onError = { exception ->
                    btnSincronizar.isEnabled = true
                    btnSincronizar.setText(R.string.pedidos_sincronizar)

                    Toast.makeText(
                        this,
                        "Error al sincronizar productos: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    override fun cargaDatos(listView: ListView) {
        val productos = ProductoDao(this).obtenerTodos()
        listView.adapter = ProductoAdaptador(this, productos)
    }
}
