package com.example.rutapedidos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.rutapedidos.Models.PedidoListado
import com.example.rutapedidos.databinding.VistaPedidoBinding

class PedidoAdaptador(context: Context, pedidos: List<PedidoListado>) :
    ArrayAdapter<PedidoListado>(context, R.layout.vista_pedido, pedidos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            VistaPedidoBinding.inflate(LayoutInflater.from(context), parent, false).also {
                it.root.tag = it
            }
        } else {
            convertView.tag as VistaPedidoBinding
        }

        val item = requireNotNull(getItem(position))
        PedidoVista.mostrar(context, binding, item)
        return binding.root
    }
}
