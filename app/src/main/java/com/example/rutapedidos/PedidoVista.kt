package com.example.rutapedidos

import android.content.Context
import com.example.rutapedidos.Models.PedidoListado
import com.example.rutapedidos.databinding.VistaPedidoBinding
import java.time.format.DateTimeFormatter

object PedidoVista {
    private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun mostrar(context: Context, binding: VistaPedidoBinding, item: PedidoListado) {
        val pedido = item.pedido
        val sinDatos = context.getString(R.string.pedido_sin_datos)
        binding.tvCabecera.text = context.getString(
            R.string.pedido_cabecera, pedido.idPedido,
            item.cliente?.razonSocial?.takeIf { it.isNotBlank() } ?: sinDatos
        )
        binding.tvRut.text = context.getString(
            R.string.pedido_rut, item.cliente?.rut?.takeIf { it.isNotBlank() } ?: sinDatos
        )
        binding.tvCreador.text = context.getString(
            R.string.pedido_creador, item.emailCreador?.takeIf { it.isNotBlank() } ?: sinDatos
        )
        binding.tvEmision.text = context.getString(
            R.string.pedido_emision, pedido.fechaEmision.format(formatoFecha)
        )
        binding.tvEntrega.text = context.getString(
            R.string.pedido_entrega, pedido.fechaEntrega.format(formatoFecha)
        )
        binding.tvOrdenCompra.text = context.getString(
            R.string.pedido_orden_compra, pedido.ordenCompra.ifBlank { sinDatos }
        )
        binding.tvNeto.text = context.getString(R.string.pedido_neto, pedido.neto)
        binding.tvDescuento.text = context.getString(R.string.pedido_descuento, pedido.descuento)
        binding.tvImpuesto.text = context.getString(R.string.pedido_impuesto, pedido.impuesto)
        binding.tvTotal.text = context.getString(R.string.pedido_total, pedido.total)
        binding.tvSincronizacion.text = context.getString(
            R.string.pedido_sincronizacion,
            context.getString(if (pedido.sincronizado) R.string.pedido_sincronizado else R.string.pedido_pendiente_sincronizar)
        )
    }
}
