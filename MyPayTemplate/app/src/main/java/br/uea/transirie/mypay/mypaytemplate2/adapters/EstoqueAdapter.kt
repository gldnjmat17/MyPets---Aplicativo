package br.uea.transirie.mypay.mypaytemplate2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ProdutosCadastradosBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import br.uea.transirie.mypay.mypaytemplate2.model.Estoque
import kotlin.reflect.KFunction1

class EstoqueAdapter(
    private val context: Context?,
    private var listaEstoque:MutableList<Estoque>,
    private val itemEditCallback: KFunction1<Estoque, Unit>,
    private val itemDeleteCallback: ((Estoque) -> Unit)
):RecyclerView.Adapter<EstoqueAdapter.EstoqueViewHolder>() {
    class EstoqueViewHolder(val binding:ProdutosCadastradosBinding):RecyclerView.ViewHolder(binding.root){
        //variáveis recebem os componentes do ViewHolder
        val txtDescricaoProduto:TextView = binding.txtDescricaoProdView
        val txtValorVenda:TextView = binding.txtValorProdView
        val txtEstoque:TextView = binding.txtQtndProdView
        val btEditarExcluir:ImageView = binding.imageView15
    }
    //gera e exibe a lista de estoque cadastrado
    fun swapData(novaListaEstoque:List<Estoque>){
        listaEstoque.clear()
        listaEstoque.addAll(novaListaEstoque)
        notifyDataSetChanged()
    }

    //cria ViewHolders quando necessário
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstoqueViewHolder {
        val binding = ProdutosCadastradosBinding.inflate(LayoutInflater.from(context),parent,false)
        return EstoqueViewHolder(binding)
    }

    //preenche os dados e define as ações do ViewHolder
    override fun onBindViewHolder(holder: EstoqueViewHolder, position: Int) {
        val estoque = listaEstoque[position]

        val tvDescricao = estoque.descricao
        holder.txtDescricaoProduto.text = tvDescricao

        val tvQntd = estoque.quantidade
        holder.txtEstoque.text = "Em estoque: $tvQntd"

        val tvValorVenda = estoque.valorVenda
        holder.txtValorVenda.text = "R$ ${tvValorVenda.toString().replace(".",",")}"

        holder.btEditarExcluir.setOnClickListener {
            val popupMenu = PopupMenu(context,it)
            val inflater:MenuInflater = popupMenu.menuInflater
            inflater.inflate(R.menu.editar_excluir_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menu_editar_cliente ->{
                        itemEditCallback(estoque)
                        true
                    }
                    R.id.menu_excluir_cliente->{
                        itemDeleteCallback(estoque)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    //a quantidade de itens é definida pelo tamanho da lista
    override fun getItemCount(): Int {
        return listaEstoque.size
    }
}