package br.uea.transirie.mypay.mypaytemplate2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.uea.transirie.mypay.mypaytemplate2.databinding.SelecionarClienteBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.reflect.KFunction1

class SelecionarClienteAdapter(
    private val context:Context?,
    private var lista:MutableList<Cliente>,
    private val clienteSelecionarCallback: KFunction1<Cliente,Unit>
):RecyclerView.Adapter<SelecionarClienteAdapter.SelecionarClienteViewHolder>() {
    var lastCheckedPosition: Int? = null
    class SelecionarClienteViewHolder(val binding:SelecionarClienteBinding):RecyclerView.ViewHolder(binding.root){
        val nome:TextView = binding.txtNomeSelecionar
        val cpf:TextView = binding.txtCpfSelecionar
        val radioButton:RadioButton = binding.radioButtonCliente
    }
    fun swapData(novaLista:List<Cliente>){
        lista.clear()
        lista.addAll(novaLista)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelecionarClienteViewHolder {
        val binding = SelecionarClienteBinding.inflate(LayoutInflater.from(context),parent,false)
        return SelecionarClienteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelecionarClienteViewHolder, position: Int) {
        val cliente = lista[position]

        holder.nome.text = cliente.nome
        holder.cpf.text = "***.***." + cliente.cpf.substring(8)
        holder.radioButton.isChecked = cliente.escolhido
        holder.radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            doAsync {
                if (isChecked){
                    lastCheckedPosition = position
                    uiThread {
                        clienteSelecionarCallback(cliente)
                        lista[position].escolhido = isChecked
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}