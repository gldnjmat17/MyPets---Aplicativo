package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import kotlinx.android.synthetic.main.activity_modo_de_pagamento.*

class ModoDePagamentoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modo_de_pagamento)
        toolbar12.setNavigationOnClickListener { finish() }
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        val sharedEditor = preference.edit()
        btDebito.setOnClickListener {
            sharedEditor.putString("pagamento","debito")
            sharedEditor.apply()
            startActivity(Intent(this,PagamentoDebitoActivity::class.java))
        }
        btDinheiro.setOnClickListener {
            sharedEditor.putString("pagamento","dinheiro")
            sharedEditor.apply()
            startActivity(Intent(this,PagamentoDinheiroActivity::class.java))
        }
        btCredito.setOnClickListener {
            sharedEditor.putString("pagamento","credito")
            sharedEditor.apply()
            startActivity(Intent(this,PagamentoCreditoActivity::class.java))
        }
    }
}