package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import kotlinx.android.synthetic.main.activity_pagamento_debito.*
import java.text.DecimalFormat

class PagamentoDebitoActivity : AppCompatActivity() {
    private var df = DecimalFormat("0.00")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagamento_debito)
        toolbar13d.setNavigationOnClickListener { finish() }
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        val descontoResgatar = preference.getString("descontoVenda","0").toString().toFloat()
        val subtotal = preference.getString("subTotalVenda","").toString().toFloat()

        val desconto = (subtotal/100) * descontoResgatar
        val total = subtotal - desconto

        textView30d.text = "Descontos (${df.format(descontoResgatar).replace(".",",")}%)"
        txtSubtotalDebito.text = "R$ ${df.format(subtotal).replace(".",",")}"
        txtDescontoDebito.text = "- R$ ${df.format(desconto).replace(".",",")}"
        txtTotalDebito.text = "R$ ${df.format(total).replace(".",",")}"

        btFinalizarDebito.setOnClickListener {
            startActivity(Intent(this,InsiraCartaoActivity::class.java))
        }
    }
}