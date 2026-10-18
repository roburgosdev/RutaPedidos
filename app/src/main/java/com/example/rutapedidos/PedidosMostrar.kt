package com.example.rutapedidos

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rutapedidos.DataAccessObjects.DetallePedidoDao
import com.example.rutapedidos.DataAccessObjects.PedidoDao
import com.example.rutapedidos.Interfaces.ListadosFragment
import com.example.rutapedidos.Models.DetallePedido
import com.example.rutapedidos.Models.Pedido
import org.w3c.dom.Text

class PedidosMostrar : AppCompatActivity(), ListadosFragment{

    // Se lee del Intent la primera vez que se usa
    private val idPedido: Int by lazy { intent.getIntExtra("idPedido", 0) }
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
        //var idPedido = intent.getIntExtra("idPedido", 0)
        var btnAgregarProducto = findViewById<Button>(R.id.btnPedidosMostrarAgregar)

        btnVolver.setOnClickListener {
            startActivity(Intent(this, Pedidos::class.java))
            finish()
        }

        btnAgregarProducto.setOnClickListener {
            var intent = Intent(this, PedidosAgregarProducto::class.java)
            intent.putExtra("idPedido", idPedido)
            startActivity(intent)
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
                    "ID Cliente: ${pedido.idCliente}\n" +
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
        if (idPedido != 0) {
            var detallePedidoDao = DetallePedidoDao(this)
            var listado = detallePedidoDao.obtenerTodosPorId(idPedido)
            var listadoFormateado = formatearSalida(listado)

            var arrayAdapter: ArrayAdapter<*>
            arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listadoFormateado)
            listView.adapter = arrayAdapter
        }
    }

    fun formatearSalida(detalle_pedidos: List<DetallePedido>) : List<String> {
        var lista = detalle_pedidos.map { detalle_pedido ->
            "ID Detalle: ${detalle_pedido.idDetalle}\n" +
                    "ID Pedido: ${detalle_pedido.idPedido}\n" +
                    "ID Producto: ${detalle_pedido.idProducto}\n" +
                    "Precio unitario: ${detalle_pedido.precioUnitario}\n" +
                    "Cantidad: ${detalle_pedido.cantidad}\n" +
                    "Neto: ${detalle_pedido.neto}\n" +
                    "Porcentaje descuento: ${detalle_pedido.porcentajeDescuento}\n" +
                    "Descuento: ${detalle_pedido.descuento}\n" +
                    "Impuesto: ${detalle_pedido.impuesto}\n" +
                    "Total: ${detalle_pedido.total}\n"
        }
        return lista
    }
}