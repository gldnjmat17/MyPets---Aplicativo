package br.uea.transirie.mypay.mypaytemplate2.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Brinde.BrindeActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Caixa.CaixaActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Estoque.EstoqueActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda.ManterItemVendaViewModel
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda.SelecaoProdutosActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import com.google.android.material.button.MaterialButton
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**
 * Esse fragmento faz parte da Activity "Home"
 * A barra de navegação inferior vai determinar qual fragmento será carregado
 */
class HomeFragment : Fragment() {
    private lateinit var gerenteCPF:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myFragment = inflater.inflate(R.layout.fragment_home, container, false)
        val btEstoque = myFragment.findViewById<MaterialButton>(R.id.btVisualizarEstoque)
        btEstoque.setOnClickListener {
            startActivity(Intent(context,EstoqueActivity::class.java))
        }
        val btBrindes = myFragment.findViewById<MaterialButton>(R.id.btBrindes)
        btBrindes.setOnClickListener {
            startActivity(Intent(context,BrindeActivity::class.java))
        }
        val btVendas = myFragment.findViewById<MaterialButton>(R.id.btVenda)
        val toast = Toast.makeText(context,"Nenhum produto cadastrado para a realização da venda",Toast.LENGTH_SHORT)
        btVendas.setOnClickListener {
            toast.cancel()
            doAsync {
                val viewModel = context?.let { it1 -> AppDatabase.getDatabase(it1)
                }?.let { it2 -> ManterItemVendaViewModel(it2) }
                val itensVenda = viewModel?.getAllItemVenda()?.filter { it.gerenteCPF == gerenteCPF }
                uiThread {
                    if (itensVenda!!.isEmpty()){
                        toast.show()
                    }else{
                        startActivity(Intent(context,SelecaoProdutosActivity::class.java))
                    }
                }
            }
        }
        val btHistorico = myFragment.findViewById<MaterialButton>(R.id.btHistorico)
        btHistorico.setOnClickListener {
            startActivity(Intent(context,CaixaActivity::class.java))
        }
        return myFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gerenteCPF = context?.let { AppPreferences.getCPFGerente(it) }.toString()
        (activity as AppCompatActivity?)!!.window.statusBarColor = context?.let {
            ContextCompat.getColor(
                it,
                R.color.colorPrimaryVariant
            )
        }!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle("")
    }
}