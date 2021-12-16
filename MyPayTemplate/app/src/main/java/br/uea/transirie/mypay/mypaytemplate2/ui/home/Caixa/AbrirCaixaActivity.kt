package br.uea.transirie.mypay.mypaytemplate2.ui.home.Caixa

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Pagamento
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_VERIF_ABRIR_CAIXA
import br.uea.transirie.mypay.mypaytemplate2.ui.home.HomeGerenteActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import br.uea.transirie.mypay.mypaytemplate2.utils.Mascara
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_abrir_caixa.*
import java.time.LocalDate

class AbrirCaixaActivity : AppCompatActivity() {
    private lateinit var gerenteCpf:String
    private lateinit var preference: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abrir_caixa)

        preference = getSharedPreferences(PREF_VERIF_ABRIR_CAIXA, MODE_PRIVATE)
        gerenteCpf = AppPreferences.getCPFGerente(this)

        txtCaixaInicialAbrirCaixa.doAfterTextChanged { text ->
            val newText = Mascara.formatMoney(text.toString())
            if (newText != text.toString()) {
                txtCaixaInicialAbrirCaixa.setText(newText)
                txtCaixaInicialAbrirCaixa.setSelection(txtCaixaInicialAbrirCaixa.text.toString().lastIndex + 1)
            }
            btAbrirCaixa.isEnabled = text.toString().isNotBlank()
        }
        btAbrirCaixa.setOnClickListener {
            if(txtCaixaInicialAbrirCaixa.text.isNullOrBlank()){
                textInputLayoutCaixa.error="Campo obrigatório"
            }else{
                val caixaInicial=txtCaixaInicialAbrirCaixa.text.toString().replace(",", ".").toFloat()
                val sharedEditor = preference.edit()
                sharedEditor.putString(gerenteCpf, Gson().toJson(AbrirCaixaStatus(status = true, data = LocalDate.now(), valor = caixaInicial)))
                sharedEditor.apply()
                Pagamento.caixaInicial.valorCaixaInicial=caixaInicial
                startActivity(Intent(this, HomeGerenteActivity::class.java))
                finish()
            }
        }
    }
    // função para quando o foco dos editTexts mudarem, o teclado se recolher
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}