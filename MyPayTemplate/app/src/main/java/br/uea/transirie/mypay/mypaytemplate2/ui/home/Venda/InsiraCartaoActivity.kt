package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import br.uea.transirie.mypay.mypaytemplate2.R

class InsiraCartaoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insira_cartao)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,InsiraSenhaActivity::class.java))
            finish()
        }, 4000)
    }
}