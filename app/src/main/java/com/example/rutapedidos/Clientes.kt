package com.example.rutapedidos

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
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
import com.example.rutapedidos.Models.Cliente
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

            // Sincronizar clientes
            clienteSync.sincronizar(
                onSuccess = { cantidad ->
                    var fragment = supportFragmentManager.findFragmentById(R.id.fragClientesLista) as? ListaFragment
                    fragment?.actualizarLista()

                    Toast.makeText(
                        this,
                        "$cantidad clientes sincronizados",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onError = { exception ->
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
        var clienteDao = ClienteDao(this)
        var listado = clienteDao.obtenerTodos()
        var listadoFormateado = formatearSalida(listado)

        var arrayAdapter: ArrayAdapter<*>
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listadoFormateado)
        listView.adapter = arrayAdapter
    }

    fun formatearSalida(clientes: List<Cliente>) : List<String> {
        var lista = clientes.map { cliente ->
            "ID: ${cliente.idCliente}\n" +
            "RUT: ${cliente.rut}\n" +
            "Razón social: ${cliente.razonSocial}\n" +
            "Dirección: ${cliente.direccion}\n" +
            "Comuna: ${cliente.comuna}\n" +
            "Telefono: ${cliente.telefono}\n" +
            "Día visita: ${cliente.diaVisita}\n"
        }
        return lista
    }

}