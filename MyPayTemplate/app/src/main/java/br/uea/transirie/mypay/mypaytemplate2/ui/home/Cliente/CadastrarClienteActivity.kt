package br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente

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
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import br.uea.transirie.mypay.mypaytemplate2.utils.CPFUtil
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskChangedListener
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskSL
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskStyle
import kotlinx.android.synthetic.main.activity_cadastrar_cliente.*
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception

class CadastrarClienteActivity : AppCompatActivity() {
    lateinit var novoCadastro:Cliente
    private lateinit var gerenteCPF:String
    private val PADRAO_TELEFONE = "+55 (__) 9____-____"
    private val PADRAO_CPF = "___.___.___-__"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_cliente)
        masks()
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.colorPrimaryVariant
        )

        // resgata usuário que está logado
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        gerenteCPF = AppPreferences.getCPFGerente(this)

        //finaliza a activity e volta pra tela anterior
        toolbar3.setNavigationOnClickListener { finish() }

        btCadastrarCliente.setOnClickListener {
            textInputLayoutNomeCliente.error = null
            textInputLayoutCPFCliente.error = null
            textInputLayoutTelefoneCliente.error = null

            // minimiza o teclado
            it.hideKeyboard()

            if (validDados()){
                fazerCadastroCliente()
            }
        }

    }
    private fun masks(){
        val maskPhone = MaskSL(
            value = PADRAO_TELEFONE,
            character = '_',
            style = MaskStyle.NORMAL
        )
        val listener = MaskChangedListener(maskPhone)
        txtTelefoneCliente.addTextChangedListener(listener)
        val maskCPF = MaskSL(
            value = PADRAO_CPF,
            character = '_',
            style = MaskStyle.NORMAL
        )
        val listener2 = MaskChangedListener(maskCPF)
        txtCpfCliente.addTextChangedListener(listener2)
    }
    private fun fazerCadastroCliente(){
        val check = switch1.isChecked
        val pontos:Int?
        if (check){
            pontos = 0
        }else{
            pontos = null
        }
        //preenche o objeto com as informações inseridas para cadastro
        novoCadastro = Cliente(0L,
            gerenteCPF,
            txtNomeCliente.text.toString(),
            txtCpfCliente.text.toString(),
            txtTelefoneCliente.text.toString(),
            txtEnderecoCliente.text.toString(),
            pontos,
            check)

        val db = AppDatabase.getDatabase(this)
        doAsync {
            val viewModel = ManterClientesViewModel(AppDatabase.getDatabase(this@CadastrarClienteActivity))
            val cliente = viewModel.clienteByCPF(txtCpfCliente.text.toString())
            try {
                fazerCadastroCliente(cliente)
            }catch (ex:Exception){
                // insere o objeto no banco de dados
                db.clienteDao().insert(novoCadastro)
                uiThread {
                    val toast = Toast.makeText(this@CadastrarClienteActivity,"Cliente cadastrado com sucesso!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,144)
                    toast.show()
                    finish()
                }
            }
        }
    }
    private fun AnkoAsyncContext<CadastrarClienteActivity>.fazerCadastroCliente(
        cliente:Cliente
    ){
        if (cliente.cpf != null){
            uiThread {
                textInputLayoutCPFCliente.error = "Cliente já cadastrado"
            }
        }
    }
    //verifica se os campos estão preenchidos e se não estiverem apresenta uma mensagem de erro
    private fun validDados():Boolean{
        val nomeCliente = txtNomeCliente.text.toString()
        val CPFCliente = txtCpfCliente.text.toString()
        val telefoneCliente = txtTelefoneCliente.text.toString()
        var x = 0

        if (nomeCliente.isEmpty()){
            textInputLayoutNomeCliente.error = "Campo obrigatório."
            x += 1
        }else if (nomeCliente.matches(Regex(".*\\d.*"))){
            textInputLayoutNomeCliente.error = "Nome inválido"
            x += 1
        }
        if (CPFCliente.isEmpty()){
            textInputLayoutCPFCliente.error = "Campo obrigatório."
            x += 1
        }else if (!CPFUtil.validarCadastroCPF(CPFCliente)){
            textInputLayoutCPFCliente.error = "CPF inválido"
            x += 1
        }
        if (telefoneCliente.isEmpty()){
            textInputLayoutTelefoneCliente.error = "Campo obrigatório."
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