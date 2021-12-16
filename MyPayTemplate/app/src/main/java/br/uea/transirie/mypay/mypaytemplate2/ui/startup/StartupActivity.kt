package br.uea.transirie.mypay.mypaytemplate2.ui.startup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityStartupBinding
import br.uea.transirie.mypay.mypaytemplate2.ui.cadastro.CadastroPassoUnicoActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import kotlinx.android.synthetic.main.activity_startup.*

/**
 * Activity que abre após o "splash" apenas se usuário não estiver logado.
 */
class StartupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartupBinding
    private val myContext: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listeners()
    }

    override fun onResume() {
        super.onResume()
        preencherTela()
    }

    private fun preencherTela(){
        val cnpjEstabelecimento = AppPreferences.getCPFGerente(myContext)
        val desconhecido = getString(R.string.cpf_desconhecido)

        if (cnpjEstabelecimento == desconhecido) {
            with (binding) {
                entrada_btnLoginCad.visibility = View.GONE
                entradaBtnCadastro.visibility = View.VISIBLE
            }
        }
    }

    private fun listeners(){
        /** realizar Cadastro **/
        entrada_btnCadastro.setOnClickListener {
            startActivity(Intent(myContext, CadastroPassoUnicoActivity::class.java))
        }

        /** realizar Login com cadastro local **/
        entrada_btnLoginCad.setOnClickListener {
            startActivity(Intent(myContext, LoginActivity::class.java))
        }
    }
}