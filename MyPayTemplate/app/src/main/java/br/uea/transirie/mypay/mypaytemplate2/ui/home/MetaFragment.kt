package br.uea.transirie.mypay.mypaytemplate2.ui.home

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Meta
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Meta.CadastrarMetaActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Meta.EditarMetaActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Meta.ManterMetaViewModel
import br.uea.transirie.mypay.mypaytemplate2.utils.CPFUtil
import com.google.android.material.button.MaterialButton
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.DecimalFormat

/**
 * Esse fragmento faz parte da Activity "Home"
 * A barra de navegação inferior vai determinar qual fragmento será carregado
 */
class MetaFragment : Fragment() {
    private lateinit var gerenteCPF:String
    private lateinit var viewModel: ManterMetaViewModel
    private lateinit var metaCadastrada: List<Meta>
    private lateinit var btMeta:TextView
    private lateinit var layoutSemMeta:ConstraintLayout
    private lateinit var layoutComMeta:ConstraintLayout
    private lateinit var layoutMetaCompletada:ConstraintLayout
    private lateinit var txtNomeMeta:TextView
    private lateinit var txtDataMeta:TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtProgresso:TextView
    private lateinit var btNovaMeta:MaterialButton
    private lateinit var editarExcluir:ImageView
    private lateinit var meta:Meta
    private var df = DecimalFormat("0.00")
    override fun onResume() {
        preencherTela()
        super.onResume()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = (activity as AppCompatActivity?)!!.getSharedPreferences(PREF_DATA_NAME, AppCompatActivity.MODE_PRIVATE)
        gerenteCPF = preferences.getString("nome_usuario","").toString()
        (activity as AppCompatActivity?)!!.window.statusBarColor = context?.let {
            ContextCompat.getColor(
                it,
                R.color.colorPrimaryVariant
            )
        }!!
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myFragment = inflater.inflate(R.layout.fragment_meta,container,false)
        btMeta = myFragment.findViewById(R.id.txtAdicionarMeta)
        btMeta.setOnClickListener {
            startActivity(Intent(context,CadastrarMetaActivity::class.java))
        }
        layoutSemMeta = myFragment.findViewById(R.id.layoutSemMeta)
        layoutComMeta = myFragment.findViewById(R.id.layoutComMeta)
        layoutMetaCompletada = myFragment.findViewById(R.id.layoutMetaCopletada)
        txtNomeMeta = myFragment.findViewById(R.id.txtNomeMetaViewCad)
        txtDataMeta = myFragment.findViewById(R.id.txtDataMeta)
        progressBar = myFragment.findViewById(R.id.progressBarMeta)
        txtProgresso = myFragment.findViewById(R.id.txtProgresso)
        btNovaMeta = myFragment.findViewById(R.id.btNovaMeta)
        editarExcluir = myFragment.findViewById(R.id.imageView11)

        editarExcluir.setOnClickListener {
            val popup = PopupMenu(context,it)
            val inflate: MenuInflater = popup.menuInflater
            inflate.inflate(R.menu.editar_excluir_menu,popup.menu)
            popup.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menu_excluir_cliente ->{
                        val builder: AlertDialog.Builder? = context?.let { it1 ->
                            AlertDialog.Builder(
                                it1
                            )
                        }
                        builder!!.setMessage("Tem certeza que deseja excluir sua meta atual?")
                        builder!!.setPositiveButton("EXCLUIR"){dialog,_->
                            doAsync {
                                viewModel = context?.let { AppDatabase.getDatabase(it) }?.let { ManterMetaViewModel(it) }!!
                                metaCadastrada = viewModel.getAllMeta().filter {
                                    it.gerenteCPF == gerenteCPF
                                }
                                meta = metaCadastrada[0]
                                viewModel.deletarMeta(meta)
                                uiThread {
                                    val toast = Toast.makeText(context,"Meta excluída com sucesso!",Toast.LENGTH_SHORT)
                                    toast.setGravity(Gravity.BOTTOM,0,144)
                                    toast.show()
                                    dialog.dismiss()
                                    preencherTela()
                                }
                            }
                        }
                        builder!!.setNegativeButton("CANCELAR"){dialog,_-> dialog.cancel()}
                        builder!!.show()
                        true
                    }
                    R.id.menu_editar_cliente ->{
                        doAsync {
                            viewModel = context?.let { AppDatabase.getDatabase(it) }?.let { ManterMetaViewModel(it) }!!
                            metaCadastrada = viewModel.getAllMeta().filter {
                                it.gerenteCPF == gerenteCPF
                            }
                            uiThread {
                                meta = metaCadastrada[0]
                                val intent = Intent(context,EditarMetaActivity::class.java)
                                intent.putExtra("id_meta",meta.id.toInt())
                                startActivity(intent)
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
        btNovaMeta.setOnClickListener {
            startActivity(Intent(context,CadastrarMetaActivity::class.java))
        }

        preencherTela()
        return myFragment
    }
    fun preencherTela(){
        layoutSemMeta.visibility = View.INVISIBLE
        layoutComMeta.visibility = View.INVISIBLE
        layoutMetaCompletada.visibility = View.INVISIBLE

        doAsync {
            viewModel = context?.let { AppDatabase.getDatabase(it) }?.let { ManterMetaViewModel(it) }!!
            metaCadastrada = viewModel.getAllMeta().filter {
                it.gerenteCPF == gerenteCPF
            }
            uiThread {
                if (metaCadastrada.isEmpty()){
                    layoutSemMeta.visibility = View.VISIBLE
                }else{
                    layoutComMeta.visibility = View.VISIBLE
                    txtNomeMeta.text = metaCadastrada[0].nome
                    txtDataMeta.text = metaCadastrada[0].data
                    progressBar.max = metaCadastrada[0].valor.toInt() *100
                    ObjectAnimator.ofInt(progressBar,"progress",metaCadastrada[0].progresso.toInt()*100)
                        .setDuration(2000)
                        .start()
                    txtProgresso.text = "R$ ${df.format(metaCadastrada[0].progresso).replace(".",",")}/R$ ${df.format(metaCadastrada[0].valor).replace(".",",")}"
                    if (metaCadastrada[0].progresso >= metaCadastrada[0].valor){
                        layoutMetaCompletada.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}