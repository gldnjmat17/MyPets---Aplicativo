package br.uea.transirie.mypay.mypaytemplate2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.uea.transirie.mypay.mypaytemplate2.databinding.ProdutoCarrinhoBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Estoque
import br.uea.transirie.mypay.mypaytemplate2.model.ItemVenda
import java.text.DecimalFormat

class CarrinhoAdapter(
    private val context: Context?,
    private var listaItem:MutableList<ItemVenda>,
    private var listaProduto:MutableList<Estoque>
):RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {
    class CarrinhoViewHolder(val binding:ProdutoCarrinhoBinding):RecyclerView.ViewHolder(binding.root){
        val nome:TextView = binding.txtNomeCarrinho
        val marca:TextView = binding.txtMarcaCarrinho
        val subtotal:TextView = binding.txtSubtotalCarrinho
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrinhoViewHolder {
        val binding = ProdutoCarrinhoBinding.inflate(LayoutInflater.from(context),parent,false)
        return CarrinhoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarrinhoViewHolder, position: Int) {
        val df = DecimalFormat("0.00")
        val item = listaItem[position]
        val lista = listaProduto
        val produto = lista.filter {
            it.codigoBarras == item.codigoBarras
        }[0]

        holder.nome.text = produto.descricao
        holder.marca.text = produto.marca
        holder.subtotal.text = "R$ ${df.format(item.subTotalItem).replace(".",",")}"
    }

    override fun getItemCount(): Int {
        return listaItem.size
    }
}