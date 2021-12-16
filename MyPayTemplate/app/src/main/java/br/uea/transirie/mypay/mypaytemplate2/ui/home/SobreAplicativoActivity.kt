package br.uea.transirie.mypay.mypaytemplate2.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.uea.transirie.mypay.mypaytemplate2.R
import kotlinx.android.synthetic.main.activity_sobre_aplicativo.*

class SobreAplicativoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre_aplicativo)

        toolbarSobre.setNavigationOnClickListener { finish() }
    }
}