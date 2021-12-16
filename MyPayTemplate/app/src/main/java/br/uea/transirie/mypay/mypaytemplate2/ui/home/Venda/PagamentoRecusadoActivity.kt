package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.uea.transirie.mypay.mypaytemplate2.R
import kotlinx.android.synthetic.main.activity_pagamento_recusado.*

class PagamentoRecusadoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagamento_recusado)

        btVoltarPag.setOnClickListener { finish() }
    }
}