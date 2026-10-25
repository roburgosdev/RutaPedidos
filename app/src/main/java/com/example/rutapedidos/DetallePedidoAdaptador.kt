package com.example.rutapedidos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.rutapedidos.Models.DetallePedido
import com.example.rutapedidos.Models.Producto
import com.example.rutapedidos.databinding.VistaDetallePedidoBinding

class DetallePedidoAdaptador(
    context: Context,
    detalles: List<DetallePedido>,
    private val productos: Map<Int, Producto>
) : ArrayAdapter<DetallePedido>(context, R.layout.vista_detalle_pedido, detalles) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            VistaDetallePedidoBinding.inflate(LayoutInflater.from(context), parent, false).also {
                it.root.tag = it
            }
        } else {
            convertView.tag as VistaDetallePedidoBinding
        }

        val detalle = requireNotNull(getItem(position))
        val producto = productos[detalle.idProducto]
        val sinDatos = context.getString(R.string.pedido_sin_datos)
        binding.tvCabecera.text = context.getString(
            R.string.producto_cabecera,
            producto?.nombre?.takeIf { it.isNotBlank() } ?: sinDatos,
            producto?.codigo?.takeIf { it.isNotBlank() } ?: sinDatos
        )
        binding.tvCantidad.text = context.getString(R.string.detalle_cantidad, detalle.cantidad)
        binding.tvPrecioUnitario.text = context.getString(R.string.detalle_precio_unitario, detalle.precioUnitario)
        binding.tvDescuento.text = context.getString(R.string.pedido_descuento, detalle.descuento)
        binding.tvNeto.text = context.getString(R.string.pedido_neto, detalle.neto)
        binding.tvImpuesto.text = context.getString(R.string.pedido_impuesto, detalle.impuesto)
        binding.tvTotal.text = context.getString(R.string.pedido_total, detalle.total)
        return binding.root
    }
}
