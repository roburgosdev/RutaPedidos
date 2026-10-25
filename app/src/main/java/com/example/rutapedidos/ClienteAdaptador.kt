package com.example.rutapedidos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.rutapedidos.Models.Cliente
import com.example.rutapedidos.databinding.VistaClienteBinding

class ClienteAdaptador(context: Context, clientes: List<Cliente>) :
    ArrayAdapter<Cliente>(context, R.layout.vista_cliente, clientes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            VistaClienteBinding.inflate(LayoutInflater.from(context), parent, false).also {
                it.root.tag = it
            }
        } else {
            convertView.tag as VistaClienteBinding
        }

        val cliente = requireNotNull(getItem(position))
        binding.tvCabecera.text = context.getString(
            R.string.cliente_cabecera, cliente.razonSocial, cliente.rut
        )
        binding.tvDireccion.text = context.getString(
            R.string.cliente_direccion, cliente.direccion, cliente.comuna
        )
        binding.tvContacto.text = context.getString(
            R.string.cliente_contacto, cliente.telefono, cliente.diaVisita
        )
        return binding.root
    }
}
