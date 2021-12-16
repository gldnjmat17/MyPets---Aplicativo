package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import kotlinx.android.synthetic.main.activity_pagamento_credito.*
import java.text.DecimalFormat

class PagamentoCreditoActivity : AppCompatActivity() {
    private var df = DecimalFormat("0.00")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagamento_credito)
        preencherDropDown()
        toolbar13.setNavigationOnClickListener { finish() }
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        val descontoResgatar = preference.getString("descontoVenda","0").toString().toFloat()
        val subtotal = preference.getString("subTotalVenda","").toString().toFloat()

        val desconto = (subtotal/100) * descontoResgatar
        val total = subtotal - desconto

        textView30c.text = "Descontos (${df.format(descontoResgatar).replace(".",",")}%)"
        txtSubtotalCredito.text = "R$ ${df.format(subtotal).replace(".",",")}"
        txtDescontoCredito.text = "- R$ ${df.format(desconto).replace(".",",")}"
        txtTotalCredito.text = "R$ ${df.format(total).replace(".",",")}"

        autoCompleteTextView4.setOnDismissListener {
            LayoutInputParcelas.error = null
            if (autoCompleteTextView4.text.toString() == ""){
                LayoutInputParcelas.error = "Seleção requerida"
            }else{
                val parcelas = autoCompleteTextView4.text.toString().split("x")
                val parcelaInt = parcelas[0].toInt()
                val valorParcelado = total/parcelaInt
                val editor = preference.edit()
                editor.putString("parcelas",parcelaInt.toString())
                editor.apply()

                txtParcelas.text = "${parcelaInt}x de R$ ${df.format(valorParcelado).replace(".",",")}"
            }
        }

        btFinalizarCredito.setOnClickListener {
            LayoutInputParcelas.error = null
            if (autoCompleteTextView4.text.toString() == ""){
                LayoutInputParcelas.error = "Seleção requerida"
            }else{
                startActivity(Intent(this,InsiraCartaoActivity::class.java))
            }
        }
    }
    private fun preencherDropDown(){
        val parcelas = resources.getStringArray(R.array.Parcelas)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_meta,parcelas)
        autoCompleteTextView4.setAdapter(arrayAdapter)
    }
}