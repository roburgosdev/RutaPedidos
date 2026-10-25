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
import java.time.format.DateTimeFormatter

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
        try {
            var item = spClientes.selectedItem.toString()
            var idCliente = item.split(" - ")[0].toInt()

            var idUsuario = Sesion.idUsuarioLocal(this)?.toInt()
                ?: throw Exception("No hay usuario en sesión")

            var usuarioDao = UsuarioDao(this)
            var usuario = usuarioDao.obtenerPorID(idUsuario)
                ?: throw Exception("Usuario no encontrado")

            var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            var fechaEmision = LocalDate.now()
            var fechaEntregaPedido = LocalDate.parse(
                fechaEntrega.text.toString(),
                formatter
            )

            var pedido = Pedido(
                idPedido = 0,
                idCliente = idCliente,
                idUsuario = idUsuario,
                fireBaseUID = usuario.fireBaseUID,
                fechaEmision = fechaEmision,
                fechaEntrega = fechaEntregaPedido,
                ordenCompra = ordenCompra.text.toString(),
                neto = 0,
                descuento = 0,
                impuesto = 0,
                total = 0,
                sincronizado = false
            )

            var pedidoDao = PedidoDao(this)
            var idPedidoCreado = pedidoDao.guardarUno(pedido).toInt()

            Toast.makeText(
                this,
                "Pedido creado correctamente",
                Toast.LENGTH_SHORT
            ).show()
            limpiarCampos()

            var intent = Intent(this, PedidosMostrar::class.java)
            intent.putExtra("idPedido", idPedidoCreado)
            startActivity(intent)
            finish()

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Error: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun limpiarCampos(){
        spClientes.setSelection(0)
        fechaEntrega.text.clear()
        ordenCompra.text.clear()
    }

}