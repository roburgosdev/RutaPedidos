package com.example.rutapedidos.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rutapedidos.R
import com.example.rutapedidos.Interfaces.ListadosFragment
import com.example.rutapedidos.databinding.FragmentListaBinding

class ListaFragment : Fragment() {

    private var listener: ListadosFragment? = null
    private lateinit var binding : FragmentListaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = FragmentListaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener?.cargaDatos(binding.lvDatos)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ListadosFragment) {
            listener = context
        }
    }
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun actualizarLista() {
            listener?.cargaDatos(binding.lvDatos)
    }
}