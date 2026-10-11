package com.example.rutapedidos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rutapedidos.DataAccessObjects.ProductoDao
import com.example.rutapedidos.SyncServices.ProductoSyncService

class Productos : AppCompatActivity() {
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

            // Sincronizar productos
            productoSync.sincronizar(
                onSuccess = { cantidad ->
                    Toast.makeText(
                        this,
                        "$cantidad productos sincronizados",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onError = { exception ->
                    Toast.makeText(
                        this,
                        "Error al sincronizar productos: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }
}