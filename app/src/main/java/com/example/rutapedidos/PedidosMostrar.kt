package com.example.rutapedidos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rutapedidos.DataAccessObjects.ClienteDao
import com.example.rutapedidos.DataAccessObjects.DetallePedidoDao
import com.example.rutapedidos.DataAccessObjects.PedidoDao
import com.example.rutapedidos.DataAccessObjects.ProductoDao
import com.example.rutapedidos.DataAccessObjects.UsuarioDao
import com.example.rutapedidos.Models.PedidoListado
import com.example.rutapedidos.Models.DetallePedido
import com.example.rutapedidos.databinding.ActivityPedidosMostrarBinding
import com.example.rutapedidos.databinding.CabeceraPedidoBinding
import com.example.rutapedidos.databinding.DialogEditarDetalleBinding
import com.example.rutapedidos.databinding.DialogEditarPedidoBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

class PedidosMostrar : AppCompatActivity() {
    private val idPedido: Int by lazy { intent.getIntExtra("idPedido", 0) }
    private lateinit var binding: ActivityPedidosMostrarBinding
    private lateinit var cabecera: CabeceraPedidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPedidosMostrarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cabecera = CabeceraPedidoBinding.inflate(layoutInflater, binding.lvDetallePedido, false)
        binding.lvDetallePedido.addHeaderView(cabecera.root, null, false)
        cabecera.infoPedido.root.apply {
            isFocusable = true
            contentDescription = getString(R.string.pedido_editar_accesibilidad)
            setBackgroundResource(android.R.drawable.list_selector_background)
            setOnClickListener { editarPedido() }
        }
        binding.lvDetallePedido.setOnItemClickListener { parent, _, position, _ ->
            val detalle = parent.getItemAtPosition(position) as? DetallePedido
                ?: return@setOnItemClickListener
            editarDetalle(detalle)
        }
        binding.btnPedidosMostrarVolver.setOnClickListener {
            volverAPedidos()
        }
        binding.btnPedidosMostrarAgregar.setOnClickListener {
            startActivity(Intent(this, PedidosAgregarProducto::class.java).apply {
                putExtra("idPedido", idPedido)
            })
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarPedido()
    }

    private fun cargarPedido() {
        val pedido = PedidoDao(this).obtenerUno(idPedido)
        if (pedido == null) {
            Toast.makeText(this, R.string.pedido_no_encontrado, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val cliente = ClienteDao(this).obtenerTodos().firstOrNull { it.idCliente == pedido.idCliente }
        val usuario = UsuarioDao(this).obtenerPorID(pedido.idUsuario)
        PedidoVista.mostrar(this, cabecera.infoPedido, PedidoListado(pedido, cliente, usuario?.email))

        val detalles = DetallePedidoDao(this).obtenerTodosPorId(idPedido)
        cabecera.tvDetalleTitulo.setText(
            if (detalles.isEmpty()) R.string.pedido_sin_productos else R.string.pedido_productos
        )
        val productos = ProductoDao(this).obtenerTodos().associateBy { it.idProducto }
        binding.lvDetallePedido.adapter = DetallePedidoAdaptador(this, detalles, productos)
    }

    private fun volverAPedidos() {
        startActivity(Intent(this, Pedidos::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        })
        finish()
    }

    private fun editarPedido() {
        val dao = PedidoDao(this)
        val pedido = dao.obtenerUno(idPedido)
        if (pedido == null) {
            Toast.makeText(this, R.string.pedido_no_encontrado, Toast.LENGTH_SHORT).show()
            volverAPedidos()
            return
        }
        val formulario = DialogEditarPedidoBinding.inflate(layoutInflater)
        val formato = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)
        formulario.txtFechaEntrega.setText(pedido.fechaEntrega.format(formato))
        formulario.txtOrdenCompra.setText(pedido.ordenCompra)
        val dialogo = AlertDialog.Builder(this)
            .setTitle(getString(R.string.pedido_editar_titulo, idPedido))
            .setView(formulario.root)
            .setPositiveButton(R.string.detalle_guardar, null)
            .setNegativeButton(R.string.detalle_cancelar, null)
            .setNeutralButton(R.string.pedido_eliminar, null)
            .create()
        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val fechaEntrega = try {
                    LocalDate.parse(formulario.txtFechaEntrega.text.toString().trim(), formato)
                } catch (e: DateTimeParseException) {
                    formulario.txtFechaEntrega.error = getString(R.string.pedido_fecha_invalida)
                    formulario.txtFechaEntrega.requestFocus()
                    return@setOnClickListener
                }
                try {
                    dao.actualizarEntregaYOrdenCompra(
                        idPedido, fechaEntrega, formulario.txtOrdenCompra.text.toString()
                    )
                    dialogo.dismiss()
                    cargarPedido()
                    Toast.makeText(this, R.string.pedido_actualizado, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.detalle_error, e.message), Toast.LENGTH_LONG).show()
                }
            }
            dialogo.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(R.string.pedido_eliminar)
                    .setMessage(getString(R.string.pedido_confirmar_eliminar, idPedido))
                    .setNegativeButton(R.string.detalle_cancelar, null)
                    .setPositiveButton(R.string.pedido_eliminar) { _, _ ->
                        try {
                            dao.eliminarConDetalles(idPedido)
                            dialogo.dismiss()
                            Toast.makeText(this, R.string.pedido_eliminado, Toast.LENGTH_SHORT).show()
                            volverAPedidos()
                        } catch (e: Exception) {
                            Toast.makeText(this, getString(R.string.detalle_error, e.message), Toast.LENGTH_LONG).show()
                        }
                    }.show()
            }
        }
        dialogo.show()
    }

    private fun editarDetalle(detalle: DetallePedido) {
        val formulario = DialogEditarDetalleBinding.inflate(layoutInflater)
        formulario.txtCantidad.setText(detalle.cantidad.toString())
        formulario.txtDescuento.setText(detalle.porcentajeDescuento.toString())
        val producto = ProductoDao(this).obtenerPorID(detalle.idProducto)
        val titulo = getString(R.string.producto_cabecera,
            producto?.nombre ?: getString(R.string.pedido_sin_datos),
            producto?.codigo ?: getString(R.string.pedido_sin_datos))
        val dialogo = AlertDialog.Builder(this)
            .setTitle(titulo)
            .setView(formulario.root)
            .setPositiveButton(R.string.detalle_guardar, null)
            .setNegativeButton(R.string.detalle_cancelar, null)
            .setNeutralButton(R.string.detalle_eliminar, null)
            .create()
        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val cantidad = formulario.txtCantidad.text.toString().toIntOrNull()
                if (cantidad == null || cantidad <= 0) {
                    formulario.txtCantidad.error = getString(R.string.detalle_cantidad_invalida)
                    return@setOnClickListener
                }
                val descuento = formulario.txtDescuento.text.toString().replace(',', '.').toDoubleOrNull()
                if (descuento == null || !descuento.isFinite() || descuento !in 0.0..100.0) {
                    formulario.txtDescuento.error = getString(R.string.detalle_descuento_invalido)
                    return@setOnClickListener
                }
                try {
                    DetallePedidoDao(this).actualizar(idPedido, detalle.idDetalle, cantidad, descuento)
                    dialogo.dismiss()
                    cargarPedido()
                    Toast.makeText(this, R.string.detalle_guardado, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.detalle_error, e.message), Toast.LENGTH_LONG).show()
                }
            }
            dialogo.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle(R.string.detalle_eliminar)
                    .setMessage(R.string.detalle_confirmar_eliminar)
                    .setNegativeButton(R.string.detalle_cancelar, null)
                    .setPositiveButton(R.string.detalle_eliminar) { _, _ ->
                        try {
                            DetallePedidoDao(this).eliminar(idPedido, detalle.idDetalle)
                            dialogo.dismiss()
                            cargarPedido()
                            Toast.makeText(this, R.string.detalle_eliminado, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this, getString(R.string.detalle_error, e.message), Toast.LENGTH_LONG).show()
                        }
                    }.show()
            }
        }
        dialogo.show()
    }
}
