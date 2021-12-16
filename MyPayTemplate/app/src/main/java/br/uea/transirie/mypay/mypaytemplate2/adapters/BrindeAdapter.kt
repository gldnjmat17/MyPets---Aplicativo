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
import br.uea.transirie.mypay.mypaytemplate2.databinding.BrindesCadastradosBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Brinde
import kotlin.reflect.KFunction1

class BrindeAdapter(
    private val context: Context?,
    private var listaBrinde:MutableList<Brinde>,
    private val itemEditCallback: KFunction1<Brinde, Unit>,
    private val itemDeleteCallback:((Brinde) -> Unit)
):RecyclerView.Adapter<BrindeAdapter.BrindeViewHolder>() {
    class BrindeViewHolder(val binding:BrindesCadastradosBinding):RecyclerView.ViewHolder(binding.root){
        //variáveis recebem os componentes do ViewHolder
        val txtNomeBrinde:TextView = binding.txtNomeBrindeView
        val txtPontos:TextView = binding.txtMetaBrindeView
        val txtQntd:TextView = binding.txtQntdBrindeView
        val txtOpcoes:ImageView = binding.imageView6
    }
    //gera e exibe a lista de brindes cadastrados
    fun swapData(novaListaBrinde:List<Brinde>){
        listaBrinde.clear()
        listaBrinde.addAll(novaListaBrinde)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrindeViewHolder {
        val binding = BrindesCadastradosBinding.inflate(LayoutInflater.from(context),parent,false)
        return BrindeViewHolder(binding)
    }

    //preenche os dados e define as ações do ViewHolder
    override fun onBindViewHolder(holder: BrindeViewHolder, position: Int) {
        val brinde = listaBrinde[position]

        val tvNome = brinde.nome
        holder.txtNomeBrinde.text = tvNome

        val tvPontos = brinde.pontos.toString()
        holder.txtPontos.text = "${tvPontos} pontos"

        val tvQntd = brinde.quantidade.toString()
        holder.txtQntd.text = "$tvQntd unidades"

        holder.txtOpcoes.setOnClickListener {
            val popupMenu = PopupMenu(context,it)
            val inflater: MenuInflater = popupMenu.menuInflater
            inflater.inflate(R.menu.editar_excluir_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menu_editar_cliente ->{
                        itemEditCallback(brinde)
                        true
                    }
                    R.id.menu_excluir_cliente ->{
                        itemDeleteCallback(brinde)
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
        return listaBrinde.size
    }
}