package br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityDadosCadastradosAjustesFuncBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.ui.startup.SplashActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DadosCadastradosAjustesFuncActivity : AppCompatActivity() {
    private lateinit var viewModel: EstabelecimentoUsuarioViewModel
    private var usuario: Usuario? = null
    private var estab: Estabelecimento? = null
    private val context = this
    private val binding by lazy { ActivityDadosCadastradosAjustesFuncBinding.inflate(layoutInflater) }
    private var ultimoUpload = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ultimoUpload = AppPreferences.getUltimoUpload(context)

        doAsync {
            viewModel = EstabelecimentoUsuarioViewModel(AppDatabase.getDatabase(context))
        }

        setAppTopBarEvents()
        usuario = intent.getParcelableExtra(getString(R.string.EXTRA_USUARIO))
        estab = intent.getParcelableExtra(getString(R.string.EXTRA_ESTABELECIMENTO))
        val nomeCompleto = usuario!!.nome.split(" ", limit = 2)

        binding.txtCargoColaborador.text = getString(R.string.funcionario)
        binding.txtEmailColaborador.text = usuario!!.email
        binding.txtNomeColaborador.text = nomeCompleto[0]
        binding.txtSobrenomeColaborador.text = nomeCompleto[1]
        binding.txtTelefoneColaborador.text = usuario!!.telefone
        binding.txtEmpresaColaborador.text = estab!!.nomeFantasia
        val strNuvem = "Atualizado na nuvem em: $ultimoUpload"
        binding.txtNuvemAtt.text = strNuvem
        binding.btDeletarConta.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Ao deletar sua conta você estará apagando todos os dados referentes a ela. Tem certeza que quer continuar?")
            builder.setPositiveButton("DELETAR") { _, _ ->
                doAsync {
                    viewModel.deleteUsuario(usuario!!)
                    uiThread {
                        val sharedPreferences = getSharedPreferences(
                            getString(R.string.PREF_USER_DATA),
                            Context.MODE_PRIVATE
                        )
                        val editor = sharedPreferences.edit()
                        editor.putBoolean(getString(R.string.PREF_USER_LOGADO), false)
                        editor.apply()

                        finishAffinity()

                        startActivity(Intent(context, SplashActivity::class.java))
                        Toast.makeText(context, "Conta deletada com sucesso!", Toast.LENGTH_SHORT).show()
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