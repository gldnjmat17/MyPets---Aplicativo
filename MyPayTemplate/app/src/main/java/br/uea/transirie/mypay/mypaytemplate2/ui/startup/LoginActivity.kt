package br.uea.transirie.mypay.mypaytemplate2.ui.startup

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityLoginBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_VERIF_ABRIR_CAIXA
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Caixa.SplashPrimeiroUsoActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.recuperar_senha.EnviarEmailActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.MyValidations
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Activity responsável por fazer o login do usuário.
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var viewModelLogin: LoginViewModel
    private val myContext = this@LoginActivity
    private val loginTag = "LOGIN"
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        title = getString(R.string.login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        doAsync {
            viewModelLogin = LoginViewModel(AppDatabase.getDatabase(myContext))
        }

        //Botão para validar login,
        binding.loginBtnEntrar.setOnClickListener {
            validarLoginLocal()
        }

        binding.txtEsquecerSenha.setOnClickListener {
            startActivity(Intent(this, EnviarEmailActivity::class.java))
        }
    }

    /**
     * Nessa função, verificamos os dados de login fornecidos pelo usuário e então realizamos login.
     *
     * A validação dos dados é feita por duas funções, do arquivo de validações da pasta utils, que
     * verificam erros comuns no campo de email e de senha. Caso haja algum erro comum, será exibida
     * uma mensagem abaixo do campo que contém o problema.
     *
     * Caso não hajam erros comuns nos campos, é realizada uma busca no banco para verificar se há
     * algum estabelecimento cadastrado com esse email e senha. Caso haja, realizamos login. Caso
     * contrário, exibimos o popup com o erro "Email ou senha incorretos."
     */
    private fun validarLoginLocal(){
        val isPinOkay = !MyValidations(myContext).pinHasErrors(binding.loginTxtPin)

        if (isPinOkay) {
            Log.i(loginTag, "Pin válido.")
            val pin = binding.loginTxtPin.editText?.text.toString().toInt()

            doAsync {
                val usuario = viewModelLogin.usuarioByPin(pin * 100)

                uiThread {
                    if (usuario != null){
                        Log.d(loginTag, "Usuário ${usuario.nome} encontrado!")
                        makeLogin(usuario)
                    } else {
                        exibeErroPopup()
                    }
                }
            }
        } else {
            Log.i(loginTag, "Pin inválido")
        }
    }

    private fun exibeErroPopup(msg: String? = getString(R.string.pin_incorreto)) {
        val builder = AlertDialog.Builder(myContext)
        builder.setTitle("Erro ao realizar login")
        builder.setMessage(msg)
        builder.create()
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun makeLogin(usuario: Usuario) {

        val userPrefs = getSharedPreferences(getString(R.string.PREF_USER_DATA), Context.MODE_PRIVATE)
        val editor = userPrefs.edit()
        editor.putBoolean(getString(R.string.PREF_USER_LOGADO), true)
        editor.putString(getString(R.string.PREF_USER_GERENTE_CPF), usuario.cpfGerente)
        editor.putBoolean(getString(R.string.PREF_USER_IS_GERENTE), usuario.isGerente)
        editor.putInt(getString(R.string.PREF_PIN), usuario.pin)
        editor.apply()

        val sharedPrefAbrirCaixa=getSharedPreferences(PREF_VERIF_ABRIR_CAIXA, MODE_PRIVATE)
        val sharedEdit=sharedPrefAbrirCaixa.edit()
        sharedEdit.putBoolean(getString(R.string.PREF_USER_IS_GERENTE), usuario.isGerente)
        sharedEdit.putString(getString(R.string.PREF_USER_GERENTE_CPF), usuario.cpfGerente)
        sharedEdit.apply()

        Log.i(loginTag, "Login bem sucedido!")

        startActivity(Intent(myContext, SplashPrimeiroUsoActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}