package br.uea.transirie.mypay.mypaytemplate2.ui.cadastro

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityGerarPinBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes.ColaboradoresActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import br.uea.transirie.mypay.mypaytemplate2.utils.MyPinGenerator
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class GerarPinActivity : AppCompatActivity() {
    private val myContext = this@GerarPinActivity
    private lateinit var viewModel: CadastroEstabelecimentoEUsuarioViewModel
    private val binding by lazy { ActivityGerarPinBinding.inflate(layoutInflater) }
    private var estabelecimento: Estabelecimento? = null
    private var usuario: Usuario? = null
    private var topbarTitle: String? = "Cadastro"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        topbarTitle = intent.getStringExtra(getString(R.string.EXTRA_TOPBAR_TITLE))
        title = topbarTitle

        usuario = intent.getParcelableExtra(getString(R.string.EXTRA_USUARIO))
        viewModel = CadastroEstabelecimentoEUsuarioViewModel(AppDatabase.getDatabase(myContext))

        when (topbarTitle) {
            getString(R.string.title_cadastro) ->
                estabelecimento = intent.getParcelableExtra(getString(R.string.EXTRA_ESTABELECIMENTO))
            getString(R.string.title_add_colaborador) ->
                binding.txtGerarPin.text = getString(R.string.gerar_pin_colbaorador)
        }

        /**
         * Ao clicar em GERAR PIN, o app gera um novo PIN, exibe uma tela de carregamento e,
         * após 2 segundos, exibe o PIN na tela.
         */
        binding.btGerarPin.setOnClickListener {
            doAsync {
                val pin = MyPinGenerator(AppDatabase.getDatabase(myContext)).newPin()

                uiThread {
                    usuario?.pin = pin

                    startForResult.launch(Intent(myContext, TelaCarregamentoActivity::class.java))
                }
            }
        }

        /**
         * Ao clicar no botão CONCLUIR, o app exibe uma caixa de diálogo para que o usuário confirme
         * a ação. Caso confirme, o app salva os dados do usuário e passa à tela seguinte.
         */
        binding.btConcluirPin.setOnClickListener {
            val builder = AlertDialog.Builder(myContext)
            builder.setTitle(getString(R.string.memorize_seu_pin))
            builder.setMessage(getString(R.string.memorize_seu_pin_txt2))
            builder.setPositiveButton(getString(R.string.concluir)) { _, _ ->
                salvaDados()

                val intent = when (topbarTitle) {
                    getString(R.string.title_add_colaborador) -> {
                        Intent(myContext, ColaboradoresActivity::class.java)
                            .putExtra(getString(R.string.EXTRA_COLABORADOR_ADICIONADO), true)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                    else -> {
                        Intent(myContext, SplashCadastroConcluidoActivity::class.java)
                            .putExtra(getString(R.string.EXTRA_TOPBAR_TITLE), topbarTitle)
                            .putExtra(getString(R.string.EXTRA_USUARIO_IS_GERENTE), usuario!!.isGerente)
                    }
                }

                startActivity(intent)
            }
            builder.setNegativeButton(getString(R.string.voltar)) { dialog, _ -> dialog.cancel() }
            builder.show()
        }
    }

    /**
     * Esta var é utilizada para decidir se, após o fim da tela de carregamento, exibimos o pin
     * gerado. Pode acontecer da tela ser finalizada através do botão de voltar e, nesse caso, não
     * exibimos o pin gerado pois ele deve ser exibido apenas se a tela finalizar naturalmente (após
     * os 2 segundos de carregamento).
     */
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                binding.viewPreGeracaoPin.visibility = View.INVISIBLE
                binding.viewPosGeracaoPin.visibility = View.VISIBLE

                val pinString = usuario?.pin.toString()
                binding.txtPinNumero1.text = pinString[0].toString()
                binding.txtPinNumero2.text = pinString[1].toString()
                binding.txtPinNumero3.text = pinString[2].toString()
                binding.txtPinNumero4.text = pinString[3].toString()
            }
        }
    }

    /**
     * Nessa função, analisamos qual o título da topbar para decidir quais dado precisamos salvar.
     **/
    private fun salvaDados() {
        when (topbarTitle) {
            getString(R.string.title_cadastro) -> cadastroInicial()
            getString(R.string.title_alterar_pin) -> alteraPin()
            getString(R.string.title_add_colaborador) -> addColaborador()
        }
    }

    private fun cadastroInicial() {
        doAsync {
            viewModel.saveEstabelecimento(estabelecimento!!)
            viewModel.saveUsuario(usuario!!)

            uiThread {
                AppPreferences.putCPF(estabelecimento!!.cpfGerente, myContext)
            }
        }
    }

    private fun alteraPin() {
        doAsync {
            viewModel.updateUsuario(usuario!!)

            uiThread {
                AppPreferences.putPin(usuario!!.pin, myContext)
            }
        }
    }

    private fun addColaborador() {
        doAsync {
            viewModel.saveUsuario(usuario!!)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}