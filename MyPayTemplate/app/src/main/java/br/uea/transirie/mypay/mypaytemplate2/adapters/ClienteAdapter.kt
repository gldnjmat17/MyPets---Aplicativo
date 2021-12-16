package br.uea.transirie.mypay.mypaytemplate2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.uea.transirie.mypay.mypaytemplate2.databinding.ClienteCadastradosBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import kotlin.reflect.KFunction1

class ClienteAdapter(
    private val context: Context?,
    private var listaClientes:MutableList<Cliente>,
    private val itemVisualizarCallback: KFunction1<Cliente, Unit>
): RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(val binding: ClienteCadastradosBinding):RecyclerView.ViewHolder(binding.root){
        //variáveis recebem os componentes do ViewHolder
        val txtNomeCliente:TextView = binding.txtNomeClienteView
        val visualizar: CardView = binding.cvCliente
        val txtPontos:TextView = binding.txtPontosView
    }

    //gera e exibe a lista de clientes cadastrados
    fun swapData(novaListaClientes:List<Cliente>){
        listaClientes.clear()
        listaClientes.addAll(novaListaClientes)
        notifyDataSetChanged()
    }
    //cria ViewHolders quando necessário
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val binding = ClienteCadastradosBinding.inflate(LayoutInflater.from(context),parent,false)
        return ClienteViewHolder(binding)
    }

    //preenche os dados e define as ações do ViewHolder
    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = listaClientes[position]

        val  tvNome = cliente.nome
        holder.txtNomeCliente.text = tvNome

        val tvPontos = cliente.pontos.toString()
        if (cliente.sistema){
            holder.txtPontos.text = "Pontuação: $tvPontos"
        }else{
            holder.txtPontos.text = "Pontuação: Não participante"
        }

        holder.visualizar.setOnClickListener {
            itemVisualizarCallback(cliente)
        }
    }
    //a quantidade de itens é definida pelo tamanho da lista
    override fun getItemCount(): Int {
        return  listaClientes.size
    }
}