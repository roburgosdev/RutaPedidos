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
import com.example.rutapedidos.DataAccessObjects.ProductoDao
import com.example.rutapedidos.Fragments.ListaFragment
import com.example.rutapedidos.Interfaces.ListadosFragment
import com.example.rutapedidos.Models.Cliente
import com.example.rutapedidos.Models.Producto
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

            // Sincronizar productos
            productoSync.sincronizar(
                onSuccess = { cantidad ->
                    var fragment = supportFragmentManager.findFragmentById(R.id.fragProductosLista) as? ListaFragment
                    fragment?.actualizarLista()

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

    override fun cargaDatos(listView: ListView) {
        var productoDao = ProductoDao(this)
        var listado = productoDao.obtenerTodos()
        var listadoFormateado = formatearSalida(listado)

        var arrayAdapter: ArrayAdapter<*>
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listadoFormateado)
        listView.adapter = arrayAdapter
    }

    fun formatearSalida(productos: List<Producto>) : List<String> {
        var lista = productos.map { producto ->
            "ID: ${producto.idProducto}\n" +
            "Código: ${producto.codigo}\n" +
            "Nombre: ${producto.nombre}\n" +
            "Unidad de medida: ${producto.unidadMedida}\n" +
            "Precio: $${producto.precio}\n" +
            "Stock: ${producto.stock}\n"
        }
        return lista
    }
}