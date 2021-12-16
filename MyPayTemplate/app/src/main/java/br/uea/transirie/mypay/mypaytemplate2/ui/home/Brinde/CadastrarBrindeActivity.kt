package br.uea.transirie.mypay.mypaytemplate2.ui.home.Brinde

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Brinde
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import kotlinx.android.synthetic.main.activity_cadastrar_brinde.*
import org.jetbrains.anko.doAsync

class CadastrarBrindeActivity : AppCompatActivity() {
    lateinit var novoCadastro: Brinde
    private lateinit var gerenteCPF:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_brinde)
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.colorPrimaryVariant
        )

        gerenteCPF = AppPreferences.getCPFGerente(this)

        //finaliza a activity e volta pra tela anterior
        toolbar76.setNavigationOnClickListener { finish() }

        btCadastrarBrinde.setOnClickListener {
            textInputNomeCad.error = null
            layoutInputQntdBrinde.error = null
            textInputPontosCad.error = null

            it.hideKeyboard()
            if (validDados()){
                fazerCadastroBrinde()
            }
        }
    }
    private fun fazerCadastroBrinde(){
        //preenche o objeto com as informações inseridas para cadastro
        novoCadastro = Brinde(0L,
            gerenteCPF,
            txtNomeBrindeCad.text.toString(),
            txtQntdBrinde.text.toString().toInt(),
            txtPontosBrindeCad.text.toString().toInt())

        val db = AppDatabase.getDatabase(this)
        doAsync {
            //insere o objeto no banco de dados
            db.brindeDao().insert(novoCadastro)
        }
        val toast = Toast.makeText(this, "Brinde cadastrado com sucesso!", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM,0,32)
        toast.show()
        finish()
    }

    //verifica se os campos estão preenchidos e se não estiverem apresenta uma mensagem de erro
    private fun validDados():Boolean{
        val nome = txtNomeBrindeCad.text.toString()
        val qntd = txtQntdBrinde.text.toString()
        val pontos = txtPontosBrindeCad.text.toString()
        var x = 0

        if (nome.isEmpty()){
            textInputNomeCad.error = "Campo obrigatório."
            x += 1
        }
        if (qntd.isEmpty()){
            layoutInputQntdBrinde.error = "Campo obrigatório."
            x += 1
        }
        if (pontos.isEmpty()){
            textInputPontosCad.error = "Campo obrigatório."
            x += 1
        }
        if (x!=0){
            return false
        }
        return true
    }
    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
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