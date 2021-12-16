package br.uea.transirie.mypay.mypaytemplate2.adapters

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.uea.transirie.mypay.mypaytemplate2.databinding.BrindeDisponivelBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Brinde
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import kotlin.reflect.KFunction1

class BrindeDisponivelAdapter(
    private val context: Context?,
    private var listaBrinde:MutableList<Brinde>,
    private val itemResgatarCallback: KFunction1<Brinde,Unit>,
    private val cliente: Cliente
):RecyclerView.Adapter<BrindeDisponivelAdapter.BrindeDisponivelViewHolder>() {
    class BrindeDisponivelViewHolder(val binding:BrindeDisponivelBinding):RecyclerView.ViewHolder(binding.root){
        val nome:TextView = binding.txtNomeBrinde
        val pontosNecessarios:TextView = binding.txtPontosNecessarios
        val pontosNecessariosBarra:TextView = binding.txtPontosNecessariosBarra
        val resgatar:TextView = binding.txtResgatarBrinde
        val progressBar:ProgressBar = binding.progressBarBrinde
    }
    fun swapData(novaLista: List<Brinde>){
        listaBrinde.clear()
        listaBrinde.addAll(novaLista)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrindeDisponivelViewHolder {
        val binding = BrindeDisponivelBinding.inflate(LayoutInflater.from(context),parent,false)
        return BrindeDisponivelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrindeDisponivelViewHolder, position: Int) {
        val brinde = listaBrinde[position]

        var verifica = false
        holder.nome.text = brinde.nome
        holder.pontosNecessarios.text = brinde.pontos.toString()
        holder.pontosNecessariosBarra.text = "${cliente.pontos.toString()}/${brinde.pontos}"
        holder.progressBar.max = brinde.pontos *100

        val progresso = cliente.pontos.toString().toInt()
        ObjectAnimator.ofInt(holder.progressBar,"progress",progresso*100)
            .setDuration(2000)
            .start()

        if (progresso>=brinde.pontos){
            holder.resgatar.setTextColor(Color.parseColor("#3B688C"))
            verifica = true
        }else{
            holder.resgatar.setTextColor(Color.parseColor("#AFACAC"))
        }

        holder.resgatar.setOnClickListener {
            when(verifica){
                true ->{
                    itemResgatarCallback(brinde)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listaBrinde.size
    }
}