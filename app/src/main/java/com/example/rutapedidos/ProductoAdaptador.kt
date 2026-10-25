package com.example.rutapedidos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.rutapedidos.Models.Producto
import com.example.rutapedidos.databinding.VistaProductoBinding

class ProductoAdaptador(context: Context, productos: List<Producto>) :
    ArrayAdapter<Producto>(context, R.layout.vista_producto, productos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            VistaProductoBinding.inflate(LayoutInflater.from(context), parent, false).also {
                it.root.tag = it
            }
        } else {
            convertView.tag as VistaProductoBinding
        }

        val producto = requireNotNull(getItem(position))
        binding.tvCabecera.text = context.getString(
            R.string.producto_cabecera, producto.nombre, producto.codigo
        )
        binding.tvPrecio.text = context.getString(
            R.string.producto_precio, producto.precio, producto.unidadMedida
        )
        binding.tvStock.text = context.getString(R.string.producto_stock, producto.stock)
        return binding.root
    }
}
