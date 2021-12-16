package br.uea.transirie.mypay.mypaytemplate2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario

class CargoAdapter(
    var listaColaboradores: MutableList<List<Usuario>>,
    val context: Context,
    private val listenerItem: ColaboradorAdapter.OnItemClickListener
):RecyclerView.Adapter<CargoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cargo_colaborador, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cargo.text = when(listaColaboradores[position][0].isGerente){
            true -> "Gerentes:"
            else -> "Funcionários:"
        }
        val adapter = ColaboradorAdapter(listaColaboradores[position], listenerItem)
        holder.recyclerView.layoutManager = LinearLayoutManager(context)
        holder.recyclerView.adapter = adapter
    }

    override fun getItemCount():Int {
        return listaColaboradores.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val cargo:TextView = itemView.findViewById(R.id.usuarioCargoLabelTxt)
        val recyclerView:RecyclerView = itemView.findViewById(R.id.itemColaboradores_recyclerView)
    }
}