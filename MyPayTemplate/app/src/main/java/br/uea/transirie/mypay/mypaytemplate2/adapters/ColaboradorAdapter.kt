package br.uea.transirie.mypay.mypaytemplate2.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario

class ColaboradorAdapter(
    private val colaboradores: List<Usuario>,
    private val listenerItem: OnItemClickListener
): RecyclerView.Adapter<ColaboradorAdapter.ViewHolder>() {
    private val logtag = "COLABORADOR_ADAPTER"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.usuario_colaborador, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(colaboradores[position]) {
            val limiteLen = 26
            var (primeiroNome, sobreNome) = nome.split(" ", limit = 2)

            if (nome.length > limiteLen) {
                val lenAbrev = 1; val dot = '.'
                primeiroNome = primeiroNome.take(lenAbrev) + dot
                Log.i(logtag, "Colaborador $nome teve o primeiro nome abreviado.")
            }

            holder.nomeColaborador.text = "$primeiroNome $sobreNome"

            var cargo = "Funcionário"
            val num = position + 1
            var setaVisibility = View.VISIBLE

            if (isGerente) {
                cargo = "Gerente"
                setaVisibility = View.INVISIBLE
            }

            holder.cargoColaborador.text = "$cargo $num"
            holder.setaVisualizarColab.visibility = setaVisibility
        }
    }

    override fun getItemCount(): Int {
        return  colaboradores.size
    }

    inner class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView),
            View.OnClickListener{
        val nomeColaborador: TextView = itemView.findViewById(R.id.usuarioNomeTxt)
        val cargoColaborador: TextView = itemView.findViewById(R.id.usuarioCargoTxt)
        val setaVisualizarColab: ImageView = itemView.findViewById(R.id.setaVisualizarColab)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                val item = colaboradores [position]
                listenerItem.itemClick(item)
            }
        }
    }
    interface OnItemClickListener{
        fun itemClick(item: Usuario)
    }
}