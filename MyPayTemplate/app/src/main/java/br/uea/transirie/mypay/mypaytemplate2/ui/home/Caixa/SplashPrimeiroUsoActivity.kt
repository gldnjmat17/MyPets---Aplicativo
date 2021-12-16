package br.uea.transirie.mypay.mypaytemplate2.ui.home.Caixa

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivitySplashPrimeiroUsoDiaBinding
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_VERIF_ABRIR_CAIXA
import br.uea.transirie.mypay.mypaytemplate2.ui.home.HomeFuncionarioActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.home.HomeGerenteActivity
import com.google.gson.Gson
import java.time.LocalDate

class SplashPrimeiroUsoActivity : AppCompatActivity() {
    private var isGerente: Boolean = false
    private lateinit var usuario:String
    private lateinit var preference: SharedPreferences
    private val binding by lazy { ActivitySplashPrimeiroUsoDiaBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val dataHoje = LocalDate.now()
        // resgata usuário que está logado
        preference = getSharedPreferences(PREF_VERIF_ABRIR_CAIXA, MODE_PRIVATE)
        usuario = preference.getString(getString(R.string.PREF_USER_GERENTE_CPF), "").toString()
        isGerente = preference.getBoolean(getString(R.string.PREF_USER_IS_GERENTE), false)
        //val edit = preference.edit()

        val statusAbrirCaixa= Gson().fromJson(preference.getString(usuario, Gson().toJson(AbrirCaixaStatus(status = false, data = LocalDate.now(), valor =0f))),
            AbrirCaixaStatus::class.java)

        Log.i("statusAbrir", statusAbrirCaixa.status.toString())

        if (statusAbrirCaixa.data.dayOfMonth == dataHoje.dayOfMonth &&
            statusAbrirCaixa.data.monthValue == dataHoje.monthValue &&
            statusAbrirCaixa.data.year == dataHoje.year &&
            statusAbrirCaixa.status &&
            isGerente){
            startActivity(Intent(this, HomeGerenteActivity::class.java))
            finish()
        }else if (isGerente){
            startActivity(Intent(this, AbrirCaixaActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this, HomeFuncionarioActivity::class.java))
            finish()
        }
    }
}