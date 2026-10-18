package com.example.rutapedidos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rutapedidos.DataAccessObjects.PedidoDao
import com.example.rutapedidos.Interfaces.ListadosFragment
import org.w3c.dom.Text

class PedidosMostrar : AppCompatActivity(), ListadosFragment{
    lateinit var infoPedido: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pedidos_mostrar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var btnVolver = findViewById<Button>(R.id.btnPedidosMostrarVolver)
        infoPedido = findViewById(R.id.txvInfoPedido)
        var idPedido = intent.getIntExtra("idPedido", 0)

        btnVolver.setOnClickListener {
            startActivity(Intent(this, Pedidos::class.java))
            finish()
        }

        if(idPedido != 0){
            cargarDatosPedido(idPedido)
        }
    }

    fun cargarDatosPedido(idPedido: Int) {
        var pedidoDao = PedidoDao(this)
        var pedido = pedidoDao.obtenerUno(idPedido)
        if (pedido != null) {
            infoPedido.text = "ID Pedido: ${pedido.idPedido}\n" +
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

    }

    override fun cargaDatos(listView: ListView) {
        TODO("Not yet implemented")
    }
}