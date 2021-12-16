package br.uea.transirie.mypay.mypaytemplate2.ui.recuperar_senha

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import br.uea.transirie.mypay.mypaytemplate2.R

class SplashEmailRecuperacaoEnviadoActivity : AppCompatActivity() {
    val context = this@SplashEmailRecuperacaoEnviadoActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_email_recuperacao_enviado)

        supportActionBar?.hide()

        val email = intent.getStringExtra(getString(R.string.EXTRA_USUARIO_EMAIL)).toString()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(context, EscanearCodigoActivity::class.java)
                .putExtra(getString(R.string.EXTRA_USUARIO_EMAIL), email)
            startActivity(intent)

            finish()
        }, 2000)
    }

}