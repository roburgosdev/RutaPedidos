package com.example.rutapedidos

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rutapedidos.DataAccessObjects.PedidoDao
import com.example.rutapedidos.DataAccessObjects.ProductoDao
import com.example.rutapedidos.Interfaces.ListadosFragment
import com.example.rutapedidos.Models.Pedido
import com.example.rutapedidos.Models.Producto

class Pedidos : AppCompatActivity() , ListadosFragment{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pedidos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var btnVolver = findViewById<Button>(R.id.btnPedidosVolver)
        var btnCrear = findViewById<Button>(R.id.btnPedidosCrear)

        val idUsuario = Sesion.idUsuarioLocal(this)
        if (idUsuario == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        btnVolver.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnCrear.setOnClickListener {
            startActivity(Intent(this, PedidosCrear::class.java))
            finish()
        }

    }

    override fun cargaDatos(listView: ListView) {
        var pedidoDao = PedidoDao(this)
        var listado = pedidoDao.obtenerTodos()
        var listadoFormateado = formatearSalida(listado)

        var arrayAdapter: ArrayAdapter<*>
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listadoFormateado)
        listView.adapter = arrayAdapter
    }

    fun formatearSalida(pedidos: List<Pedido>) : List<String> {
        var lista = pedidos.map { pedido ->
            "ID Pedido: ${pedido.idPedido}\n" +
                    "ID CLiente: ${pedido.idCliente}\n" +
                    "ID Usuario: ${pedido.idUsuario}\n" +
                    "Firebase UID: ${pedido.fireBaseUID}\n" +
                    "Fecha Emision: ${pedido.fechaEmision}\n" +
                    "Fecha Entrega: ${pedido.fechaEntrega}\n" +
                    "Orden de Compra: ${pedido.ordenCompra}\n" +
                    "Neto: ${pedido.neto}\n" +
                    "Descuento: ${pedido.descuento}\n" +
                    "Impuesto: ${pedido.impuesto}\n" +
                    "Total: ${pedido.total}\n" +
                    "Estado: ${pedido.estado}\n" +
                    "Sincronizado: ${if (pedido.sincronizado) "Si" else "No"}\n"
        }
        return lista
    }
}