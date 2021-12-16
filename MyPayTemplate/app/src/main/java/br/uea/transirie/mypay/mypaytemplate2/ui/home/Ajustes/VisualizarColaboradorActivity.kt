package br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityVisualizarColaboradorBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class VisualizarColaboradorActivity : AppCompatActivity() {
    private lateinit var viewModel: EstabelecimentoUsuarioViewModel
    private val context = this
    var usuario: Usuario? = null
    private val binding by lazy { ActivityVisualizarColaboradorBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setAppTopBarEvents()
        usuario = intent.getParcelableExtra(getString(R.string.EXTRA_USUARIO))
        val nomeCompleto = usuario!!.nome.split(" ", limit = 2)

        binding.txtCargoColaborador.text = getString(R.string.funcionario)
        binding.txtEmailColaborador.text = usuario!!.email
        binding.txtNomeColaborador.text = nomeCompleto[0]
        binding.txtSobrenomeColaborador.text = nomeCompleto[1]
        binding.txtTelefoneColaborador.text = usuario!!.telefone
        doAsync {
            viewModel = EstabelecimentoUsuarioViewModel(AppDatabase.getDatabase(context))
            val estab = viewModel.estabelecimentoByCPFGerente(usuario!!.cpfGerente)
            uiThread {
                binding.txtEmpresaColaborador.text = estab!!.nomeFantasia
            }
        }
        binding.btDeletarColaborador.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Tem certeza que quer deletar o colaborador?")
            builder.setPositiveButton("DELETAR"){dialog,_->
                doAsync {
                    viewModel.deleteUsuario(usuario!!)
                    uiThread {
                        val intent = Intent(context, ColaboradoresActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        Toast.makeText(context, "Colaborador deletado com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton("CANCELAR"){dialog,_-> dialog.cancel()}
            builder.show()
        }
    }
    private fun setAppTopBarEvents(){
        binding.listaColabTopAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.listaColabTopAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.editar_dados ->{
                    startActivity(
                        Intent(context, EditarColaboradorActivity::class.java)
                            .putExtra(getString(R.string.EXTRA_USUARIO), usuario)
                    )
                    true
                }
                else -> false
            }
        }
    }
}