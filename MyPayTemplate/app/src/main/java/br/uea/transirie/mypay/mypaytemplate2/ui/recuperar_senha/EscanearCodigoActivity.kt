package br.uea.transirie.mypay.mypaytemplate2.ui.recuperar_senha

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityEscanearCodigoBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class EscanearCodigoActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEscanearCodigoBinding.inflate(layoutInflater) }
    private val context = this@EscanearCodigoActivity
    private val tag = "RECUPERAR_PIN_2"
    private var usuarioEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        usuarioEmail = intent.getStringExtra(getString(R.string.EXTRA_USUARIO_EMAIL)).toString()

        binding.btnSendAgain.setOnClickListener {
            Firebase.auth.sendPasswordResetEmail(
                usuarioEmail
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Email enviado!", Toast.LENGTH_SHORT).show()
                }
                else {
                    if (task.exception == null) {
                        Log.e(tag, "Erro desconhecido.")
                    } else {
                        val exceptionMsg = task.exception?.message
                        Log.e(tag, "Erro no envio do email: $exceptionMsg")

                        val toastMsg = when (exceptionMsg) {
                            getString(R.string.erro_conexao_atividade_incomum_eng) ->
                                getString(R.string.erro_conexao_atividade_incomum)
                            else -> getString(R.string.erro_envio_email)
                        }
                        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnScan.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val result: IntentResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if (result.contents == null) {
                Toast.makeText(context, "Leitura cancelada", Toast.LENGTH_LONG).show()
            } else {
                val code = result.contents
                Firebase.auth.verifyPasswordResetCode(
                    code
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        nextActivity()
                    } else {
                        Toast.makeText(context, "Código inválido ou expirado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun nextActivity() {
        val intent = Intent(context, RecuperarPinActivity::class.java)
            .putExtra(getString(R.string.EXTRA_USUARIO_EMAIL), usuarioEmail)
        startActivity(intent)
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