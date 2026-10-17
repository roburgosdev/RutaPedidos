package com.example.rutapedidos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rutapedidos.DataAccessObjects.UsuarioDao
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            Sesion.idUsuarioLocal(this)
            abrirPantallaPrincipal()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // CUENTA DEMO PARA REVISION
        // Usuario: demo@rutapedidos.cl
        // Contrasena: 123456
        var correo = findViewById<EditText>(R.id.txtCorreo)
        var contrasena = findViewById<EditText>(R.id.txtContrasena)

        var btnIngresar = findViewById<Button>(R.id.btnLogin)

        btnIngresar.setOnClickListener {
            var email = correo.text.toString().trim()
            var password = contrasena.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Completa todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            btnIngresar.isEnabled = false

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    btnIngresar.isEnabled = true

                    if (task.isSuccessful) {
                        Sesion.idUsuarioLocal(this)
                        abrirPantallaPrincipal()
                    } else {
                        Toast.makeText(
                            this,
                            "No se pudo iniciar sesión",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun abrirPantallaPrincipal() {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}