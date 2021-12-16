package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskBrMonetaryValue
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_inserir_desconto.*
import java.text.DecimalFormat

class DescontoActivity : AppCompatActivity() {
    private var df = DecimalFormat("0.00")
    private var porcentagem = 0f
    private var desconto = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inserir_desconto)
        configuraCampoPreco(txtPorcentagemDesconto)
        toolbarD.setNavigationOnClickListener { finish() }

        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        val subtotal = preference.getString("subTotalVenda","").toString().toFloat()

        txtValorSubTotal.text = "R$ ${df.format(subtotal).replace(".",",")}"
        txtValotTotal.text = "R$ ${df.format(subtotal).replace(".",",")}"

        btInserirDesconto.setOnClickListener {
            porcentagem = txtPorcentagemDesconto.text.toString().replace(".","").replace(",",".").toFloat()
            desconto = subtotal/100 * porcentagem
            val total = subtotal - desconto

            textView51.text = "Descontos (${df.format(porcentagem).replace(".",",")}%)"
            textView57.text = "- R$ ${df.format(desconto).replace(".",",")}"
            txtValotTotal.text = "R$ ${df.format(total).replace(".",",")}"
            it.hideKeyboard()
            txtPorcentagemDesconto.inputType = InputType.TYPE_NULL
        }
        btCancelar.setOnClickListener {
            porcentagem = 0f
            desconto = 0f

            textView51.text = "Descontos (${df.format(porcentagem).replace(".",",")}%)"
            textView57.text = "- R$ ${df.format(desconto).replace(".",",")}"
            txtValotTotal.text = "R$ ${df.format(subtotal).replace(".",",")}"
            txtPorcentagemDesconto.inputType = InputType.TYPE_CLASS_NUMBER
            txtPorcentagemDesconto.setText("000")
        }
        btAplicarDesconto.setOnClickListener {
            val shareEditor = preference.edit()
            shareEditor.putString("descontoVenda",porcentagem.toString())
            shareEditor.apply()
            finish()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun configuraCampoPreco(preco: TextInputEditText?) {
        preco?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && preco.text.toString().isEmpty()) {
                preco.setText("0,00")
            } else if (!hasFocus && preco.text.toString() == "0,00") {
                preco.setText("")
            }
        }
        preco?.addTextChangedListener(MaskBrMonetaryValue.mask(preco))
    }
    // função para quando o foco dos editTexts mudarem, o teclado se recolher
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}