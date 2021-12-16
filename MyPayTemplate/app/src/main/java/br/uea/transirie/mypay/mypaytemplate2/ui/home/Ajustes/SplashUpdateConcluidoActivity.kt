package br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SplashUpdateConcluidoActivity : AppCompatActivity() {
    private lateinit var viewModel: EstabelecimentoUsuarioViewModel
    private var pinUsuario: Int? = null
    private val context = this
    private var usuario: Usuario? = null
    private lateinit var userPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_update_concluido)

        val alterandoDadosGerente = intent.getBooleanExtra(getString(R.string.ALTERANDO_DADOS_GERENTE), false)
        supportActionBar?.hide()
        userPrefs = getSharedPreferences(getString(R.string.PREF_USER_DATA), Context.MODE_PRIVATE)
        pinUsuario = userPrefs.getInt(getString(R.string.PREF_PIN), 0)
        doAsync {
            viewModel = EstabelecimentoUsuarioViewModel(AppDatabase.getDatabase(context))
            usuario = viewModel.usuarioByPin(pinUsuario!!)
            uiThread {
                var intent = Intent(context, ColaboradoresActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                if (!usuario!!.isGerente){
                    intent = Intent(context, AjustesFuncionarioActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }else if(alterandoDadosGerente){
                    intent = Intent(context, DadosCadastradosGerenteActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                /** Conta 2s antes de passar a próxima tela **/
                val tempo = 2000L

                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(intent)
                }, tempo)
            }
        }
    }
}