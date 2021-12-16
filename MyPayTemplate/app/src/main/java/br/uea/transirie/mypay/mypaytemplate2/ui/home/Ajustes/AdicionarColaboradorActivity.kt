package br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityAdicionarColaboradorBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.ui.cadastro.CadastroEstabelecimentoEUsuarioViewModel
import br.uea.transirie.mypay.mypaytemplate2.ui.cadastro.GerarPinActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskType
import br.uea.transirie.mypay.mypaytemplate2.utils.MyMask
import br.uea.transirie.mypay.mypaytemplate2.utils.MyValidations
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AdicionarColaboradorActivity : AppCompatActivity() {
    private lateinit var viewModel: CadastroEstabelecimentoEUsuarioViewModel
    private val myContext = this@AdicionarColaboradorActivity
    private val tag = "CADASTRO_COLABORADOR"
    private var usuario: Usuario? = null
    private val binding by lazy { ActivityAdicionarColaboradorBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        title = getString(R.string.title_add_colaborador)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = CadastroEstabelecimentoEUsuarioViewModel(AppDatabase.getDatabase(myContext))

        binding.cadastroColaboradorEtTelefone.let {
            it.addTextChangedListener(MyMask(MaskType.Telephone, it))
        }

        preencherDropDown()

        binding.cadastroColaboradorBtAvancar.setOnClickListener {
            checkAndAdvance()
        }
    }

    private fun preencherDropDown(){
        val lista = resources.getStringArray(R.array.CargosColab)
        val arrayAdapter = ArrayAdapter(myContext, R.layout.dropdown_model, lista)
        binding.cadastroColaboradorAutoCompleteCargo.setAdapter(arrayAdapter)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    /**
     * Função que verifica se o email já está em uso localmente.
     *
     * @param callback flag ativada apenas se o email estiver disponível para uso.
     */
    private fun verificaEmailLocalmente(callback: () -> Unit) {
        val email = binding.cadastroColaboradorEtEmail.text.toString()

        doAsync {
            val emailJaEmUso = viewModel.emailJaEmUsoLocalmente(email)

            uiThread {
                if (emailJaEmUso) {
                    val errorMsg =
                        getString(R.string.erro_email_ja_em_uso_tente_outro)
                    binding.cadastroColaboradorTiEmail.error = errorMsg

                    Log.d(tag, errorMsg)
                }
                else callback()
            }
        }
    }
    private fun checkAndAdvance(){
        val userPrefs = getSharedPreferences(getString(R.string.PREF_USER_DATA), Context.MODE_PRIVATE)
        val cpfGerente = userPrefs.getString(getString(R.string.PREF_USER_GERENTE_CPF), "").toString()

        val myValidations = MyValidations(myContext)

        val isCargoOkay = !myValidations.inputHasEmptyError(binding.cadastroColaboradorTiCargo)
        val isEmailOkay = !myValidations.emailHasErrors(binding.cadastroColaboradorTiEmail)
        val isNomeUsuarioOkay = !myValidations.inputHasEmptyError(binding.cadastroColaboradorTiNomeUsuario)
        val isSobrenomeUsuarioOkay = !myValidations.inputHasEmptyError(binding.cadastroColaboradorTiSobrenomeUsuario)
        val isTelefoneOkay = !myValidations.telefoneHasErrors(binding.cadastroColaboradorTiTelefone)

        if (isEmailOkay){
            Log.d(tag, "O email é válido.")

            verificaEmailLocalmente {
                if (isCargoOkay && isNomeUsuarioOkay && isSobrenomeUsuarioOkay && isTelefoneOkay){
                    Log.d(tag, "Todos os dados são válidos.")

                    val isGerente = when(binding.cadastroColaboradorAutoCompleteCargo.text.toString()){
                        "Funcionário"-> false
                        else -> true
                    }
                    val email = binding.cadastroColaboradorEtEmail.text.toString()
                    val espacoChar = ' '
                    val nome = binding.cadastroColaboradorEtNomeUsuario.text.toString()
                    val sobrenome = binding.cadastroColaboradorEtSobrenomeUsuario.text.toString()
                    val nomeUsuarioCompleto = nome + espacoChar + sobrenome
                    val telefone = binding.cadastroColaboradorEtTelefone.text.toString()

                    usuario = Usuario(
                        email = email,
                        nome = nomeUsuarioCompleto,
                        telefone = telefone,
                        isGerente = isGerente,
                        cpfGerente = cpfGerente
                    )
                    val intent = Intent(myContext, GerarPinActivity::class.java)
                        .putExtra(getString(R.string.EXTRA_USUARIO), usuario)
                        .putExtra(
                            getString(R.string.EXTRA_TOPBAR_TITLE),
                            getString(R.string.title_add_colaborador)
                        )

                    startActivity(intent)
                }
            }
        }
    }
}