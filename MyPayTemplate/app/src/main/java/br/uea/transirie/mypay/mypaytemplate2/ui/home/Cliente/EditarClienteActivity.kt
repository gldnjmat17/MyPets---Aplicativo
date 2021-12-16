package br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskChangedListener
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskSL
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskStyle
import kotlinx.android.synthetic.main.activity_cadastrar_cliente.*
import kotlinx.android.synthetic.main.activity_editar_cliente.*
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditarClienteActivity : AppCompatActivity() {
    lateinit var cpfCliente:String
    lateinit var viewModel: ManterClientesViewModel
    private val PADRAO_TELEFONE = "+55 (__) 9____-____"
    lateinit var clienteProcurado:Cliente
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_cliente)
        masks()
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.colorPrimaryVariant
        )

        //finaliza a activity e volta pra tela anterior
        toolbar9.setNavigationOnClickListener {
            val builder:AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Deseja sair sem salvar suas alterações?")
            builder.setPositiveButton("SAIR"){dialog,_->
                finish()
                dialog.dismiss()
            }
            builder.setNegativeButton("CANCELAR"){dialog,_-> dialog.cancel()}
            builder.show()
        }

        //resgata o cpf do cliente que foi selecionado na tela anterior
        val intent = intent
        cpfCliente = intent.getStringExtra("cpf_cliente").toString()
        mostrarDados()

        btEditarCliente.setOnClickListener {
            //nenhuma msg de erro é emitida inicialmente
            layoutNomeClienteEditar.error = null
            layoutTelefoneClienteEditar.error = null

            //minimiza o teclado
            it.hideKeyboard()

            if (validDados()){
                modificarDados()
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
        txtTeloneClienteEditar.addTextChangedListener(listener)
    }
    private fun mostrarDados(){
        doAsync {
            //localiza o cliente através do cpf
            viewModel = ManterClientesViewModel(AppDatabase.getDatabase(this@EditarClienteActivity))
            clienteProcurado = viewModel.clienteByCPF(cpfCliente)
            uiThread {
                //exibe as informações atualmente cadastradas do cliente selecionado
                txtCpfEditarCliente.setText(clienteProcurado.cpf)
                txtCpfEditarCliente.inputType = InputType.TYPE_NULL
                txtNomeClienteEditar.setText(clienteProcurado.nome)
                txtTeloneClienteEditar.setText(clienteProcurado.telefone)
                txtEnderecoClienteEditar.setText(clienteProcurado.endereco)
                if (clienteProcurado.sistema){
                    switch2.isChecked = true
                }
            }
        }
    }
    private fun modificarDados(){
        doAsync {
            val check = switch2.isChecked
            val pontos:Int?
            if (clienteProcurado.sistema){
                if (check){
                    pontos = clienteProcurado.pontos
                }else{
                    pontos = null
                }
            }else{
                if (check){
                    pontos = 0
                }else{
                    pontos = null
                }
            }
            //modifica as informações cadastradas com aquilo que foi inserido
            clienteProcurado.nome = txtNomeClienteEditar.text.toString()
            clienteProcurado.telefone = txtTeloneClienteEditar.text.toString()
            clienteProcurado.endereco = txtEnderecoClienteEditar.text.toString()
            clienteProcurado.pontos = pontos
            clienteProcurado.sistema = check
            //faz a atualização no banco de daods
            viewModel.updateCliente(clienteProcurado)
            modificarDados()
        }
    }
    private fun AnkoAsyncContext<EditarClienteActivity>.modificarDados(){
        uiThread {
            val toast = Toast.makeText(this@EditarClienteActivity,"Cliente atualizado com sucesso!",Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM,0,32)
            toast.show()
            finish()
        }
    }
    //verifica se os campos estão preenchidos e se não estiverem mostra msgs de erro
    private fun validDados():Boolean{
        val nome = txtNomeClienteEditar.text.toString()
        val telefone = txtTeloneClienteEditar.text.toString()
        var x = 0

        if (nome.isEmpty()){
            layoutNomeClienteEditar.error = "Campo obrigatório."
            x += 1
        }else if (nome.matches(Regex(".*\\d.*"))){
            layoutNomeClienteEditar.error = "Nome inválido"
            x += 1
        }
        if (telefone.isEmpty()){
            layoutTelefoneClienteEditar.error = "Campo obrigatório."
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