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
import com.example.rutapedidos.DataAccessObjects.ClienteDao
import com.example.rutapedidos.Fragments.ListaFragment
import com.example.rutapedidos.Interfaces.ListadosFragment
import com.example.rutapedidos.SyncServices.ClienteSyncService


class Clientes : AppCompatActivity(), ListadosFragment {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_clientes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var btnVolver = findViewById<Button>(R.id.btnClientesVolver)
        var btnSincronizar = findViewById<Button>(R.id.btnClientesSincronizar)

        btnVolver.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnSincronizar.setOnClickListener {
            var clienteDao = ClienteDao(this)
            var clienteSync = ClienteSyncService(clienteDao)

            btnSincronizar.isEnabled = false
            btnSincronizar.setText(R.string.pedidos_sincronizando)

            // Sincronizar clientes
            clienteSync.sincronizar(
                onSuccess = { cantidad ->
                    btnSincronizar.isEnabled = true
                    btnSincronizar.setText(R.string.pedidos_sincronizar)

                    var fragment = supportFragmentManager.findFragmentById(R.id.fragClientesLista) as? ListaFragment
                    fragment?.actualizarLista()

                    Toast.makeText(
                        this,
                        "$cantidad clientes sincronizados",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onError = { exception ->
                    btnSincronizar.isEnabled = true
                    btnSincronizar.setText(R.string.pedidos_sincronizar)

                    Toast.makeText(
                        this,
                        "Error al sincronizar clientes: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

    }

    override fun cargaDatos(listView: ListView) {
        val clientes = ClienteDao(this).obtenerTodos()
        listView.adapter = ClienteAdaptador(this, clientes)
    }

}
