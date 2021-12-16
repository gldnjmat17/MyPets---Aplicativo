package br.uea.transirie.mypay.mypaytemplate2.ui.recuperar_senha

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityRecuperarPinBinding
import br.uea.transirie.mypay.mypaytemplate2.ui.startup.LoginActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class RecuperarPinActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRecuperarPinBinding.inflate(layoutInflater) }
    private lateinit var viewModel: RecuperarPinViewModel
    private val context = this@RecuperarPinActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val email = intent.getStringExtra(getString(R.string.EXTRA_USUARIO_EMAIL)).toString()

        viewModel = ViewModelProvider(context).get(RecuperarPinViewModel::class.java)

        doAsync {
            val pin = viewModel.pinByEmail(email)

            uiThread {
                binding.RP3TxtPin.text = pin
            }
        }

        binding.RP3BtnConcluir.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.memorize_seu_pin))
            builder.setMessage(getString(R.string.memorize_seu_pin_txt2))
            builder.setPositiveButton(getString(R.string.concluir)) { dialog, _ ->
                dialog.cancel()

                val intent = Intent(context, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            builder.setNegativeButton(getString(R.string.voltar)) { dialog, _ -> dialog.cancel() }
            builder.show()
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