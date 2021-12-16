package br.uea.transirie.mypay.mypaytemplate2.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.adapters.ClienteAdapter
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente.CadastrarClienteActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente.ManterClientesViewModel
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente.VisualizarClienteActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import kotlinx.android.synthetic.main.fragment_clientes.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClientesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClientesFragment : Fragment() {
    private lateinit var viewModel:ManterClientesViewModel
    private lateinit var clientesCadastrados:MutableList<Cliente>
    private lateinit var clienteAdapter:ClienteAdapter
    private lateinit var gerenteCPF:String
    private lateinit var listaCliente:RecyclerView

    override fun onResume() {
        super.onResume()
        doAsync {
            viewModel = context?.let { AppDatabase.getDatabase(it) }?.let {
                ManterClientesViewModel(it)
            }!!
            //uma lista com o estoque cadastrado é gerada
            clientesCadastrados = viewModel.getAllClientes() as MutableList
            //filtra a lista para exibir somente aquilo cadastrado pelo usuário logado
            clientesCadastrados = clientesCadastrados.filter {
                it.gerenteCPF == gerenteCPF
            } as MutableList
            uiThread {
                // a lista gerada é exibida
                clienteAdapter.swapData(clientesCadastrados)
                if (clientesCadastrados.size == 0){
                    listaCliente.visibility = View.INVISIBLE
                    txtTextoAjudaSelecionar.visibility = View.INVISIBLE
                    txtTextoAjuda.visibility = View.VISIBLE
                }else{
                    listaCliente.visibility = View.VISIBLE
                    txtTextoAjudaSelecionar.visibility = View.VISIBLE
                    txtTextoAjuda.visibility = View.INVISIBLE
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gerenteCPF = context?.let { AppPreferences.getCPFGerente(it) }.toString()
        (activity as AppCompatActivity?)!!.window.statusBarColor = context?.let {
            ContextCompat.getColor(
                it,
                R.color.colorPrimaryVariant
            )
        }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myFragment = inflater.inflate(R.layout.fragment_clientes, container, false)
        val btAdicionar = myFragment.findViewById<ImageView>(R.id.imageView4)
        btAdicionar.setOnClickListener {
            startActivity(Intent(context,CadastrarClienteActivity::class.java))
        }
        listaCliente = myFragment!!.findViewById(R.id.recyclerViewClientes)
        clienteAdapter = ClienteAdapter(context,
        mutableListOf(),
        ::onVisualizarCliente)

        listaCliente.adapter = clienteAdapter
        listaCliente.layoutManager = LinearLayoutManager(context)

        doAsync {
            viewModel = context?.let { AppDatabase.getDatabase(it) }?.let {
                ManterClientesViewModel(it)
            }!!
            //uma lista com o estoque cadastrado é gerada
            clientesCadastrados = viewModel.getAllClientes() as MutableList
            //filtra a lista para exibir somente aquilo cadastrado pelo usuário logado
            clientesCadastrados = clientesCadastrados.filter {
                it.gerenteCPF == gerenteCPF
            } as MutableList
            uiThread {
                // a lista gerada é exibida
                clienteAdapter.swapData(clientesCadastrados)
                if (clientesCadastrados.size == 0){
                    listaCliente.visibility = View.INVISIBLE
                    txtTextoAjudaSelecionar.visibility = View.INVISIBLE
                    txtTextoAjuda.visibility = View.VISIBLE
                }else{
                    listaCliente.visibility = View.VISIBLE
                    txtTextoAjudaSelecionar.visibility = View.VISIBLE
                    txtTextoAjuda.visibility = View.INVISIBLE
                }
            }
        }

        return myFragment
    }
    // quando o nome do cliente é clicado essa função é executada
    fun onVisualizarCliente(cliente: Cliente){
        val intent = Intent(activity, VisualizarClienteActivity::class.java)
        intent.putExtra("cpfCliente", cliente.cpf)
        startActivity(intent)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClientesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClientesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}