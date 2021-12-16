package br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityAjustesFuncionarioBinding
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.ui.home.SobreAplicativoActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.startup.SplashActivity
import kotlinx.android.synthetic.main.activity_ajustes_funcionario.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AjustesFuncionarioActivity : AppCompatActivity() {
    private lateinit var viewModel: EstabelecimentoUsuarioViewModel
    private val context = this
    private lateinit var userPrefs: SharedPreferences
    private var cpfGerente: String? = null
    private var pinUsuario: Int? = null
    private val binding by lazy { ActivityAjustesFuncionarioBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        title = "Ajustes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userPrefs = getSharedPreferences(getString(R.string.PREF_USER_DATA), Context.MODE_PRIVATE)
        cpfGerente = userPrefs.getString(getString(R.string.PREF_USER_GERENTE_CPF), "")
        pinUsuario = userPrefs.getInt(getString(R.string.PREF_PIN), 0)
        preencherTelaEListeners()
    }

    private fun preencherTelaEListeners(){
        doAsync {
            viewModel = EstabelecimentoUsuarioViewModel(AppDatabase.getDatabase(context))
            val estab = viewModel.estabelecimentoByCPFGerente(cpfGerente!!)
            val usuario = viewModel.usuarioByPin(pinUsuario!!)
            uiThread {
                binding.tvNomeEstabelecimentoAjustes.text = estab!!.nomeFantasia
                binding.tvFuncionarioAjustes.text = usuario.nome
                binding.cvDadosCadastradosAjustes.setOnClickListener {
                    val intent = Intent(context, DadosCadastradosAjustesFuncActivity::class.java)
                        .putExtra(getString(R.string.EXTRA_USUARIO), usuario)
                        .putExtra(getString(R.string.EXTRA_ESTABELECIMENTO), estab)
                    startActivity(intent)
                }
                binding.cvSobreAppAjustes.setOnClickListener {
                    startActivity(Intent(context, SobreAplicativoActivity::class.java))
                }
                cvPinAjustes.setOnClickListener {
                    startActivity(Intent(context, AlterarPinActivity::class.java))
                }
                binding.cvEncerrarSessaoAjustes.setOnClickListener {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setTitle("Sair do aplicativo")
                    builder.setMessage("Você tem certeza que deseja encerrar sua sessão?")
                    builder.setPositiveButton("SAIR"){dialog,_->
                        val sharedPreferences = getSharedPreferences(
                            getString(R.string.PREF_USER_DATA),
                            Context.MODE_PRIVATE
                        )
                        val editor = sharedPreferences.edit()
                        editor.putBoolean(getString(R.string.PREF_USER_LOGADO), false)
                        editor.apply()

                        finishAffinity()

                        startActivity(Intent(context, SplashActivity::class.java))
                    }
                    builder.setNegativeButton("CANCELAR"){dialog,_-> dialog.cancel()}
                    builder.show()
                }
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}