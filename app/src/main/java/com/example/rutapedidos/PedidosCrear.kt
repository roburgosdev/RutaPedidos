package com.example.rutapedidos

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rutapedidos.DataAccessObjects.ClienteDao
import com.example.rutapedidos.DataAccessObjects.PedidoDao
import com.example.rutapedidos.DataAccessObjects.UsuarioDao
import com.example.rutapedidos.Models.Cliente
import com.example.rutapedidos.Models.Pedido
import java.time.LocalDate
import java.util.Date

class PedidosCrear : AppCompatActivity() {
    private lateinit var spClientes: Spinner
    private lateinit var fechaEntrega: EditText
    private lateinit var ordenCompra: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pedidos_crear)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var btnVolver = findViewById<Button>(R.id.btnPedidosCrearVolver)
        spClientes = findViewById(R.id.spClientes)
        fechaEntrega = findViewById(R.id.txtPedidosCrearFechaEntrega)
        ordenCompra = findViewById(R.id.txtPedidosCrearOrdenCompra)
        var btnLimpiar = findViewById<Button>(R.id.btnPedidosCrearLimpiar)
        var btnGuardar = findViewById<Button>(R.id.btnPedidosCrearGuardar)

        btnLimpiar.setOnClickListener {
            limpiarCampos()
        }

        btnVolver.setOnClickListener {
            startActivity(Intent(this, Pedidos::class.java))
            finish()
        }

        btnGuardar.setOnClickListener {
            guardar()
        }

        cargaSpinnerClientes(spClientes)
    }

    fun cargaSpinnerClientes(spinner: Spinner){
        var clienteDao = ClienteDao(this)
        var listado = clienteDao.obtenerTodos()
        var listadoFormateado = formatearSalida(listado)

        var arrayAdapter: ArrayAdapter<*>
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listadoFormateado)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
    }

    fun formatearSalida(clientes: List<Cliente>) : List<String> {
        var lista = clientes.map { cliente ->
            "${cliente.idCliente} - ${cliente.razonSocial} - ${cliente.direccion} - ${cliente.comuna}"
        }
        return lista
    }

    fun guardar(){

        var item = spClientes.selectedItem.toString()
        var splitted = item.split(" - ")
        var id_cliente = splitted[0].toInt()
        var id_usuario = Sesion.idUsuarioLocal(this)
        var usuarioDao = UsuarioDao(this)
        var usuario = usuarioDao.obtenerPorID(id_usuario!!.toInt())
        var firebase_uid = usuario!!.fireBaseUID
        var fecha_emision = LocalDate.now()
        var fecha_entrega = LocalDate.parse(fechaEntrega.text.toString())
        var orden_compra = ordenCompra.text.toString()

        var pedido = Pedido(
            idPedido = 1,
            idCliente = id_cliente,
            idUsuario = id_usuario.toInt(),
            fireBaseUID = firebase_uid,
            fechaEmision = fecha_emision,
            fechaEntrega = fecha_entrega,
            ordenCompra = orden_compra,
            neto = 0,
            descuento = 0,
            impuesto = 0,
            total = 0,
            estado = "pendiente",
            sincronizado = false
        )

        var pedidoDao = PedidoDao(this)
        pedidoDao.guardarUno(pedido)
        Toast.makeText(this, "Pedido Creado", Toast.LENGTH_SHORT).show()
    }

    fun limpiarCampos(){
        spClientes.setSelection(0)
        fechaEntrega.text.clear()
        ordenCompra.text.clear()
    }

}