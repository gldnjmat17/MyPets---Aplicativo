package br.uea.transirie.mypay.mypaytemplate2.ui.cadastro

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityTelaCarregamentoBinding
import java.lang.Exception

class TelaCarregamentoActivity : AppCompatActivity() {
    private val myContext = this
    private val binding by lazy { ActivityTelaCarregamentoBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        try {
            supportActionBar!!.hide()
        }catch (ex: Exception){Log.d("TELA_CARREGAMENTO", "Não foi possível esconder a SupportActionBar")}

        binding.gerandoPinIvRoda.animation =
            AnimationUtils.loadAnimation(myContext, R.anim.rotate_carregamento)

        /** Conta 2s antes de passar a próxima tela **/
        val tempo = 2000L
        Handler(Looper.getMainLooper()).postDelayed({
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }, tempo)
    }
}