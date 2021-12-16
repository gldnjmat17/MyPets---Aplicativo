package br.uea.transirie.mypay.mypaytemplate2.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.uea.transirie.mypay.mypaytemplate2.databinding.ProdutoSelecionarBinding
import br.uea.transirie.mypay.mypaytemplate2.databinding.ProdutosCadastradosBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Estoque
import br.uea.transirie.mypay.mypaytemplate2.model.ItemVenda
import kotlin.reflect.KFunction1

class SelecaoProdutoAdapter(
    private val context:Context?,
    private var listaItem:MutableList<ItemVenda>,
    private var listaProduto:MutableList<Estoque>,
    private val itemMaisCallback: KFunction1<ItemVenda,Unit>,
    private val itemMenosCallback: KFunction1<ItemVenda,Unit>
):RecyclerView.Adapter<SelecaoProdutoAdapter.SelecaoProdutoViewHolder>() {
    class SelecaoProdutoViewHolder(val binding:ProdutoSelecionarBinding):RecyclerView.ViewHolder(binding.root){
        val botao:Button = binding.btEspaco
        val txtQntd:TextView = binding.txtQntdItem
        val btMenos:ImageView = binding.btMenos
        val btMais: ImageView = binding.btMais
        val txtNome:TextView = binding.txtNomeItem
        val txtMarca:TextView = binding.txtMarcaItem
        val txtValor:TextView = binding.txtValorItem
        val txtEstoque:TextView = binding.txtEstoqueItem
    }
    fun swapData(novaLista:List<ItemVenda>){
        listaItem.clear()
        listaItem.addAll(novaLista)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelecaoProdutoViewHolder {
        val binding = ProdutoSelecionarBinding.inflate(LayoutInflater.from(context),parent,false)
        return SelecaoProdutoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelecaoProdutoViewHolder, position: Int) {
        val item = listaItem[position]
        val lista = listaProduto
        val produto = lista.filter {
            it.codigoBarras == item.codigoBarras
        }[0]

        holder.botao.isClickable = false
        var quantidade = item.quantidade
        holder.txtQntd.text = quantidade.toString()
        holder.txtNome.text = produto.descricao
        holder.txtMarca.text = produto.marca
        holder.txtValor.text = "R$ ${produto.valorVenda.toString().replace(".",",")}"
        holder.txtEstoque.text = "Em estoque: ${produto.quantidade}"

        if (quantidade>0){
            holder.btMenos.setColorFilter(Color.parseColor("#3B688C"))
        }else{
            holder.btMenos.setColorFilter(Color.parseColor("#c6c6c6"))
        }
        if (quantidade==produto.quantidade){
            holder.btMais.setColorFilter(Color.parseColor("#c6c6c6"))
        }else{
            holder.btMais.setColorFilter(Color.parseColor("#3B688C"))
        }
        holder.btMais.setOnClickListener {
            if (quantidade<produto.quantidade){
                holder.btMenos.setColorFilter(Color.parseColor("#3B688C"))
                itemMaisCallback(item)
                quantidade += 1
                holder.txtQntd.text = quantidade.toString()
                if (quantidade == produto.quantidade){
                    holder.btMais.setColorFilter(Color.parseColor("#c6c6c6"))
                }
            }
        }
        holder.btMenos.setOnClickListener {
            if (quantidade > 0){
                itemMenosCallback(item)
                quantidade -= 1
                holder.txtQntd.text = quantidade.toString()
                if (quantidade == 0){
                    holder.btMenos.setColorFilter(Color.parseColor("#c6c6c6"))
                }
                if (quantidade<produto.quantidade){
                    holder.btMais.setColorFilter(Color.parseColor("#3B688C"))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listaItem.size
    }
}