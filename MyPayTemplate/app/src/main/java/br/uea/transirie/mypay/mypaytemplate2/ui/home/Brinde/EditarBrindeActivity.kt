package br.uea.transirie.mypay.mypaytemplate2.ui.home.Brinde

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Brinde
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import kotlinx.android.synthetic.main.activity_editar_brinde.*
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditarBrindeActivity : AppCompatActivity() {
    var idBrinde:Long = 0L
    lateinit var viewModel: ManterBrindeViewModel
    lateinit var brindeProcurado:Brinde

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_brinde)
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.colorPrimaryVariant
        )

        //finaliza a activity e volta pra tela anterior
        toolbar111.setNavigationOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Deseja sair sem salvar suas alterações?")
            builder.setPositiveButton("SAIR"){dialog,_->
                finish()
                dialog.dismiss()
            }
            builder.setNegativeButton("CANCELAR"){dialog,_-> dialog.cancel()}
            builder.show()
        }

        //resgata o id do brinde que foi selecionado na tela anterior
        val intent = intent
        idBrinde = intent.getIntExtra("id_brinde",0).toLong()
        mostrarDados()

        btEditarBrinde.setOnClickListener {
            //nenhuma msg de erro é emitida inicialmente
            textInputNomeEdit.error = null
            layoutInputQntdBrindeEd.error = null
            textInputPontosEdit.error = null

            //minimiza o teclado
            it.hideKeyboard()
            if (validDados()){
                modificarDados()
            }
        }
    }
    private fun modificarDados(){
        doAsync {
            //modifica as informações cadastradas com aquilo que foi inserido
            brindeProcurado.nome = txtNomeBrindeEdit.text.toString()
            brindeProcurado.quantidade = txtQntdBrindeEd.text.toString().toInt()
            brindeProcurado.pontos = txtPontosBrindeEdit.text.toString().toInt()
            //faz a atualização no banco de dados
            viewModel.updateBrinde(brindeProcurado)
            modificarDados()
        }
    }
    private fun AnkoAsyncContext<EditarBrindeActivity>.modificarDados(){
        uiThread {
            val toast = Toast.makeText(this@EditarBrindeActivity, "Item atualizado com sucesso!", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM,0,32)
            toast.show()
            finish()
        }
    }
    private fun validDados():Boolean{
        val nome = txtNomeBrindeEdit.text.toString()
        val qntd = txtQntdBrindeEd.text.toString()
        val pontos = txtPontosBrindeEdit.text.toString()
        var x = 0

        if (nome.isEmpty()){
            textInputNomeEdit.error = "Campo obrigatório."
            x += 0
        }
        if (qntd.isEmpty()){
            layoutInputQntdBrindeEd.error = "Campo obrigatório."
            x += 0
        }
        if (pontos.isEmpty()){
            textInputPontosEdit.error = "Campo obrigatório."
            x += 0
        }
        if (x!=0){
            return false
        }
        return true
    }
    private fun mostrarDados(){
        doAsync {
            //localiza o produto através do id do brinde
            viewModel = ManterBrindeViewModel(AppDatabase.getDatabase(this@EditarBrindeActivity))
            brindeProcurado = viewModel.brindeByID(idBrinde)
            uiThread {
                //exibe as informações atualmente cadastradas do produto selecionado
                txtNomeBrindeEdit.setText(brindeProcurado.nome)
                txtQntdBrindeEd.setText(brindeProcurado.quantidade.toString())
                txtPontosBrindeEdit.setText(brindeProcurado.pontos.toString())
            }
        }
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