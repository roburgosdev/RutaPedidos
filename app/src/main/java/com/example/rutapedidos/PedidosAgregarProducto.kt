package com.example.rutapedidos

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.rutapedidos.DataAccessObjects.DetallePedidoDao
import com.example.rutapedidos.DataAccessObjects.ProductoDao
import com.example.rutapedidos.Models.DetallePedido
import com.example.rutapedidos.Models.Producto

class PedidosAgregarProducto : AppCompatActivity() {

    private val idPedido: Int by lazy { intent.getIntExtra("idPedido", 0) }
    private lateinit var spProductos: Spinner
    private lateinit var cantidad: EditText
    private lateinit var porcentaje_descuento: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pedidos_agregar_producto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var btnVolver = findViewById<Button>(R.id.btnPedidosAgregarProductoVolver)
        spProductos = findViewById(R.id.spProductos)
        cantidad = findViewById(R.id.txtPedidosAgregarProductoCantidad)
        porcentaje_descuento = findViewById(R.id.txtPedidosAgregarProductoPorcentajeDescuento)
        var btnLimpiar = findViewById<Button>(R.id.btnPedidosAgregarProductoLimpiar)
        var btnGuardar = findViewById<Button>(R.id.btnPedidosAgregarProductoGuardar)


        btnVolver.setOnClickListener {
            var intent = Intent(this, PedidosMostrar::class.java)
            intent.putExtra("idPedido", idPedido)
            startActivity(intent)
            finish()
        }

        btnLimpiar.setOnClickListener {
            limpiarCampos()
        }

        btnGuardar.setOnClickListener {
            guardar()

        }

        cargaSpinnerProductos()
    }


    fun cargaSpinnerProductos() {
        var productoDao = ProductoDao(this)
        var listado = productoDao.obtenerTodos()
        var listadoFormateado = formatearSalida(listado)

        var arrayAdapter: ArrayAdapter<*>
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listadoFormateado)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spProductos.adapter = arrayAdapter
    }

    fun formatearSalida(productos: List<Producto>) : List<String> {
        var lista = productos.map { producto ->
            "${producto.idProducto} - ${producto.nombre} - ${producto.precio} - ${producto.stock}"
        }
        return lista
    }

    fun limpiarCampos(){
        spProductos.setSelection(0)
        cantidad.text.clear()
        porcentaje_descuento.text.clear()
    }

    fun guardar() {
        try {
            var item = spProductos.selectedItem.toString()
            var idProducto = item.split(" - ")[0].toInt()

            var idPedido = idPedido

            var productoDao = ProductoDao(this)
            var producto = productoDao.obtenerPorID(idProducto)

            var cantidad = cantidad.text.toString().toInt()
            var precioUnitario = if (producto != null) producto.precio else 0
            var porcentajeDescuento = porcentaje_descuento.text.toString().toDouble()

            var descuento = (cantidad * precioUnitario * (porcentajeDescuento / 100)).toInt()
            var neto = cantidad * precioUnitario
            var iva = 0.19
            var impuesto = ((neto - descuento) * iva).toInt()
            var total = neto - descuento + impuesto


            var detallePedido = DetallePedido(
                idDetalle = 0,
                idPedido = idPedido,
                idProducto = idProducto,
                cantidad = cantidad,
                precioUnitario = precioUnitario,
                porcentajeDescuento = porcentajeDescuento,
                descuento = descuento,
                neto = neto,
                impuesto = impuesto,
                total = total,
            )

            var detallePedidoDao = DetallePedidoDao(this)
            var idDetallePedidoCreado = detallePedidoDao.guardarUno(detallePedido).toInt()

            Toast.makeText(
                this,
                "Producto agregado a pedido correctamente",
                Toast.LENGTH_SHORT
            ).show()
            limpiarCampos()

            var intent = Intent(this, PedidosMostrar::class.java)
            intent.putExtra("idPedido", idPedido)
            startActivity(intent)
            finish()

        } catch (e: Exception) {

            Log.e("PEDIDOS_ERROR", "Error al guardar producto", e)

            Toast.makeText(
                this,
                "Error: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}