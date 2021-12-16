package br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityEditarEstabelecimentoBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.ui.cadastro.CadastroEstabelecimentoEUsuarioViewModel
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskType
import br.uea.transirie.mypay.mypaytemplate2.utils.MyMask
import br.uea.transirie.mypay.mypaytemplate2.utils.MyValidations
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditarEstabelecimentoActivity : AppCompatActivity() {
    private lateinit var viewModel: CadastroEstabelecimentoEUsuarioViewModel
    private val tag = "EDITAR_ESTAB_E_USU"
    private var usuario: Usuario? = null
    private var estabelecimento: Estabelecimento? = null
    private val myContext = this
    private val alterandoDadosGerente = true
    private val binding by lazy { ActivityEditarEstabelecimentoBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        title = "Editar dados"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = CadastroEstabelecimentoEUsuarioViewModel(AppDatabase.getDatabase(myContext))

        binding.cadastroEstabelecimentoEtTelefone.let {
            it.addTextChangedListener(MyMask(MaskType.Telephone, it))
        }

        usuario = intent.getParcelableExtra(getString(R.string.EXTRA_USUARIO))
        estabelecimento = intent.getParcelableExtra(getString(R.string.EXTRA_ESTABELECIMENTO))

        preencherDadosColaborador()
        binding.cadastroEstabelecimentoBtAvancar.setOnClickListener { checkAndAdvance() }
    }

    private fun preencherDadosColaborador(){
        usuario!!.let {
            with(it){
                val nomeCompleto = nome.split(" ", limit = 2)
                binding.cadastroEstabelecimentoEtEmail.setText(email)
                binding.cadastroEstabelecimentoEtNomeUsuario.setText(nomeCompleto[0])
                binding.cadastroEstabelecimentoEtSobrenomeUsuario.setText(nomeCompleto[1])
                binding.cadastroEstabelecimentoEtTelefone.setText(telefone)
            }
        }
        estabelecimento!!.let {
            with(it){
                binding.cadastroEstabelecimentoEtNome.setText(nomeFantasia)
                binding.cadastroEstabelecimentoEtCNPJ.setText(cpfGerente)
            }
        }
    }

    private fun checkAndAdvance(){
        val myValidations = MyValidations(myContext)

        val isEmailOkay = !myValidations.emailHasErrors(binding.cadastroEstabelecimentoTiEmail)
        val isNomeUsuarioOkay = !myValidations.inputHasEmptyError(binding.cadastroEstabelecimentoTiNomeUsuario)
        val isSobrenomeUsuarioOkay = !myValidations.inputHasEmptyError(binding.cadastroEstabelecimentoTiSobrenomeUsuario)
        val isTelefoneOkay = !myValidations.telefoneHasErrors(binding.cadastroEstabelecimentoTiTelefone)
        val isNomeEstabOkay = !myValidations.inputHasEmptyError(binding.cadastroEstabelecimentoTiNome)

        if (isEmailOkay){
            Log.d(tag, "O email é válido.")

            verificaEmailLocalmente {
                if (isNomeUsuarioOkay && isSobrenomeUsuarioOkay && isTelefoneOkay && isNomeEstabOkay){
                    Log.d(tag, "Todos os dados são válidos.")

                    val email = binding.cadastroEstabelecimentoEtEmail.text.toString()
                    val espacoChar = ' '
                    val nome = binding.cadastroEstabelecimentoEtNomeUsuario.text.toString()
                    val sobrenome = binding.cadastroEstabelecimentoEtSobrenomeUsuario.text.toString()
                    val nomeUsuarioCompleto = nome + espacoChar + sobrenome
                    val telefone = binding.cadastroEstabelecimentoEtTelefone.text.toString()
                    val nomeEstab = binding.cadastroEstabelecimentoEtNome.text.toString()

                    estabelecimento!!.nomeFantasia = nomeEstab
                    usuario!!.email = email
                    usuario!!.nome = nomeUsuarioCompleto
                    usuario!!.telefone = telefone

                    doAsync {
                        viewModel.updateUsuario(usuario!!)
                        viewModel.updateEstab(estabelecimento!!)
                        uiThread {
                            val intent = Intent(myContext, SplashUpdateConcluidoActivity::class.java)
                                .putExtra(getString(R.string.ALTERANDO_DADOS_GERENTE), alterandoDadosGerente)

                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    /**
     * Função que verifica se o email já está em uso localmente.
     *
     * @param callback flag ativada apenas se o email estiver disponível para uso.
     */
    private fun verificaEmailLocalmente(callback: () -> Unit) {
        val email = binding.cadastroEstabelecimentoEtEmail.text.toString()

        doAsync {
            val emailJaEmUso = when (email == usuario!!.email){
                true -> false
                else -> {
                    viewModel.emailJaEmUsoLocalmente(email)
                }
            }

            uiThread {
                if (emailJaEmUso) {
                    val errorMsg =
                        getString(R.string.erro_email_ja_em_uso_tente_outro)
                    binding.cadastroEstabelecimentoTiEmail.error = errorMsg

                    Log.d(tag, errorMsg)
                }
                else callback()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}