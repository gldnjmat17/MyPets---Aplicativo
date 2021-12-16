package br.uea.transirie.mypay.mypaytemplate2.ui.recuperar_senha

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityEnviarEmailBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.utils.MyValidations
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EnviarEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnviarEmailBinding
    private lateinit var viewModel: RecuperarPinViewModel
    private val context = this@EnviarEmailActivity
    private val tag = "RECUPERAR_PIN_1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnviarEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.RP1BtnAvancar.setOnClickListener {
            val myValidations = MyValidations(context)
            val isEmailOkay = !myValidations.emailHasErrors(binding.RP1TiEmail)

            if (isEmailOkay) {
                val email = binding.RP1EtEmail.text.toString()
                binding.progressBar.visibility = View.VISIBLE

                viewModel = ViewModelProvider(context).get(RecuperarPinViewModel::class.java)

                doAsync {
                    val usuario = viewModel.usuarioByEmail(email)

                    uiThread {
                        if (usuario != null)
                            enviaEmail(usuario.email)
                        else {
                            binding.progressBar.visibility = View.GONE
                            binding.RP1TiEmail.error =
                                getString(R.string.recuperar_pin_um_erro_email_nao_cadastrado)
                        }
                    }
                }
            }
        }
    }

    private fun enviaEmail(email: String) {
        Firebase.auth.sendPasswordResetEmail(
            email
        ).addOnCompleteListener { task ->
            binding.progressBar.visibility = View.GONE

            if (task.isSuccessful) {
                Log.d(tag, "Email enviado!")

                val intent = Intent(context, SplashEmailRecuperacaoEnviadoActivity::class.java)
                    .putExtra(getString(R.string.EXTRA_USUARIO_EMAIL), email)
                startActivity(intent)
            } else {
                if (task.exception == null)
                    Log.e(tag, "Erro desconhecido.")
                else {
                    val exceptionMsg = task.exception?.message
                    Log.e(tag, "Erro no envio do email: $exceptionMsg")

                    val toastMsg = when (exceptionMsg) {
                        getString(R.string.erro_conexao_atividade_incomum_eng) ->
                            getString(R.string.erro_conexao_atividade_incomum)
                        getString(R.string.erro_firebase_email_nao_encontrado_eng) ->
                            getString(R.string.erro_firebase_email_nao_encontrado)
                        else -> getString(R.string.erro_envio_email)
                    }
                    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Esta função serve para que a ação de voltar pela seta na barra superior utilize o
     * onBackPressed. Dessa forma, a activity anterior, que será reaberta, não perderá os valores
     * preenchidos nos campos de entrada.
     **/
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}