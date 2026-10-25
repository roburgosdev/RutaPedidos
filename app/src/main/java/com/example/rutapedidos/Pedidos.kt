package com.example.rutapedidos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rutapedidos.DataAccessObjects.PedidoDao
import com.example.rutapedidos.DataAccessObjects.ClienteDao
import com.example.rutapedidos.DataAccessObjects.UsuarioDao
import com.example.rutapedidos.Interfaces.ListadosFragment
import com.example.rutapedidos.Models.PedidoListado
import com.example.rutapedidos.Fragments.ListaFragment
import com.example.rutapedidos.SyncServices.PedidoSyncService

class Pedidos : AppCompatActivity() , ListadosFragment{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pedidos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var btnVolver = findViewById<Button>(R.id.btnPedidosVolver)
        var btnCrear = findViewById<Button>(R.id.btnPedidosCrear)
        val btnSincronizar = findViewById<Button>(R.id.btnPedidosSincronizar)

        val idUsuario = Sesion.idUsuarioLocal(this)
        if (idUsuario == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        btnVolver.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnCrear.setOnClickListener {
            startActivity(Intent(this, PedidosCrear::class.java))
            finish()
        }

        btnSincronizar.setOnClickListener {
            btnSincronizar.isEnabled = false
            btnSincronizar.setText(R.string.pedidos_sincronizando)
            PedidoSyncService(this).sincronizar(
                onSuccess = { cantidad, pendientes ->
                    if (!isFinishing && !isDestroyed) {
                        btnSincronizar.isEnabled = true
                        btnSincronizar.setText(R.string.pedidos_sincronizar)
                        actualizarLista()
                        Toast.makeText(this, getString(R.string.pedidos_sync_resultado, cantidad, pendientes), Toast.LENGTH_LONG).show()
                    }
                },
                onError = { error ->
                    if (!isFinishing && !isDestroyed) {
                        btnSincronizar.isEnabled = true
                        btnSincronizar.setText(R.string.pedidos_sincronizar)
                        actualizarLista()
                        Toast.makeText(this, getString(R.string.pedidos_sync_error, error.message), Toast.LENGTH_LONG).show()
                    }
                }
            )
        }

    }

    override fun onResume() {
        super.onResume()
        actualizarLista()
    }

    private fun actualizarLista() {
        (supportFragmentManager.findFragmentById(R.id.fragPedidosLista) as? ListaFragment)?.actualizarLista()
    }

    override fun cargaDatos(listView: ListView) {
        val pedidos = PedidoDao(this).obtenerTodos()
        val clientes = ClienteDao(this).obtenerTodos().associateBy { it.idCliente }
        val usuarioDao = UsuarioDao(this)
        val usuarios = pedidos.map { it.idUsuario }.distinct().associateWith {
            usuarioDao.obtenerPorID(it)
        }
        val listado = pedidos.map { pedido ->
            PedidoListado(pedido, clientes[pedido.idCliente], usuarios[pedido.idUsuario]?.email)
        }
        listView.adapter = PedidoAdaptador(this, listado)
        listView.setOnItemClickListener { parent, _, position, _ ->
            val seleccionado = parent.getItemAtPosition(position) as PedidoListado
            startActivity(Intent(this, PedidosMostrar::class.java).apply {
                putExtra("idPedido", seleccionado.pedido.idPedido)
            })
        }
    }
}
