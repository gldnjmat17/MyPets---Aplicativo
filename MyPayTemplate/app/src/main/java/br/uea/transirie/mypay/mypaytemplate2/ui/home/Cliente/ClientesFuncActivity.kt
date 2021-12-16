package br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.adapters.ClienteAdapter
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityClientesFuncBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import kotlinx.android.synthetic.main.fragment_clientes.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ClientesFuncActivity : AppCompatActivity() {
    private lateinit var viewModel:ManterClientesViewModel
    private lateinit var clientesCadastrados:MutableList<Cliente>
    private lateinit var clienteAdapter: ClienteAdapter
    private lateinit var gerenteCPF:String

    private val binding by lazy { ActivityClientesFuncBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        gerenteCPF = AppPreferences.getCPFGerente(this)

        binding.imageView4.setOnClickListener {
            startActivity(Intent(this,CadastrarClienteActivity::class.java))
        }
        binding.toolbar5.setNavigationOnClickListener {
            finish()
        }
        clienteAdapter = ClienteAdapter(this,
            mutableListOf(),
            ::onVisualizarCliente)

        binding.recyclerViewClientes.adapter = clienteAdapter
        binding.recyclerViewClientes.layoutManager = LinearLayoutManager(this)

        doAsync {
            viewModel = ManterClientesViewModel(AppDatabase.getDatabase(this@ClientesFuncActivity))
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
                    binding.recyclerViewClientes.visibility = View.INVISIBLE
                    txtTextoAjudaSelecionar.visibility = View.INVISIBLE
                    txtTextoAjuda.visibility = View.VISIBLE
                }else{
                    binding.recyclerViewClientes.visibility = View.VISIBLE
                    txtTextoAjudaSelecionar.visibility = View.VISIBLE
                    txtTextoAjuda.visibility = View.INVISIBLE
                }
            }
        }
    }
    // quando o nome do cliente é clicado essa função é executada
    fun onVisualizarCliente(cliente: Cliente){
        val intent = Intent(this, VisualizarClienteActivity::class.java)
        intent.putExtra("cpfCliente", cliente.cpf)
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        doAsync {
            viewModel = ManterClientesViewModel(AppDatabase.getDatabase(this@ClientesFuncActivity))
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
                    binding.recyclerViewClientes.visibility = View.INVISIBLE
                    txtTextoAjudaSelecionar.visibility = View.INVISIBLE
                    txtTextoAjuda.visibility = View.VISIBLE
                }else{
                    binding.recyclerViewClientes.visibility = View.VISIBLE
                    txtTextoAjudaSelecionar.visibility = View.VISIBLE
                    txtTextoAjuda.visibility = View.INVISIBLE
                }
            }
        }
    }
}