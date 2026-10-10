package com.example.rutapedidos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        var btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)
        var btnClientes = findViewById<Button>(R.id.btnClientes)
        var btnProductos = findViewById<Button>(R.id.btnProductos)
        var btnPedidos = findViewById<Button>(R.id.btnPedidos)

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        btnClientes.setOnClickListener {
            startActivity(Intent(this, Clientes::class.java))
            finish()
        }

        btnProductos.setOnClickListener {
            startActivity(Intent(this, Productos::class.java))
            finish()
        }

        btnPedidos.setOnClickListener {
            startActivity(Intent(this, Pedidos::class.java))
            finish()
        }
    }

    private fun cerrarSesion() {
        // Cerrar sesión en Firebase
        auth.signOut()

        var intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

}