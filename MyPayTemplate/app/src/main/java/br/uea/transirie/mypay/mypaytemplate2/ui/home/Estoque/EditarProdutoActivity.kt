package br.uea.transirie.mypay.mypaytemplate2.ui.home.Estoque

import android.annotation.SuppressLint
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
import br.uea.transirie.mypay.mypaytemplate2.model.Estoque
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskBrMonetaryValue
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_editar_cliente.*
import kotlinx.android.synthetic.main.activity_editar_produto.*
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditarProdutoActivity : AppCompatActivity() {
    private lateinit var codBarrasProduto:String
    private lateinit var viewModel: ManterEstoqueViewModel
    private lateinit var produtoProcurado:Estoque
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_produto)
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.colorPrimaryVariant
        )

        //finaliza a activity e volta pra tela anterior
        toolbar554.setNavigationOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("Deseja sair sem salvar suas alterações?")
            builder.setPositiveButton("SAIR"){dialog,_->
                finish()
                dialog.dismiss()
            }
            builder.setNegativeButton("CANCELAR"){dialog,_-> dialog.cancel()}
            builder.show()
        }

        //resgata o código de barras do produto que foi selecionado na tela anterior
        val intent = intent
        codBarrasProduto = intent.getStringExtra("codBarras_produto").toString()
        mostrarDados()

        btEditarProduto.setOnClickListener {
            //nenhuma msg de erro é emitida inicialmente
            layoutNomeProdutoEditar.error = null
            layoutMarcaProdutoEdit.error =null
            layoutValorProdutoEditar.error = null
            layoutValorProdVendaEditar.error = null
            layoutQuantProdEditar.error = null

            //minimiza o teclado
            it.hideKeyboard()

            if (validDados()){
                modificarDados()
            }
        }
    }
    private fun mostrarDados(){
        doAsync {
            //localiza o produto através do código de barras
            viewModel = ManterEstoqueViewModel(AppDatabase.getDatabase(this@EditarProdutoActivity))
            produtoProcurado = viewModel.estoqueByCodBarras(codBarrasProduto)
            uiThread {
                txtCodBarrasEdit.inputType = InputType.TYPE_NULL
                txtCodBarrasEdit.setText(produtoProcurado.codigoBarras)
                configuraCampoPreco(txtValorInicialProdutoEditar)
                configuraCampoPreco(txtValorProdVendaEditar)
                //exibe as informações atualmente cadastradas do produto selecionado
                txtNomeProdutoEditar.setText(produtoProcurado.descricao)
                var inicial = ""
                if (produtoProcurado.valorInicial.toString().last().toString() == "0"){
                    inicial = produtoProcurado.valorInicial.toString() + "0"
                }else{
                    inicial = produtoProcurado.valorInicial.toString()
                }
                txtValorInicialProdutoEditar.setText(inicial)

                var venda = ""
                if (produtoProcurado.valorVenda.toString().last().toString() == "0"){
                    venda = produtoProcurado.valorVenda.toString() + "0"
                }else{
                    venda = produtoProcurado.valorVenda.toString()
                }
                txtValorProdVendaEditar.setText(venda)
                txtQuantProdEditar.setText(produtoProcurado.quantidade.toString())
                txtMarcaProdutoEdit.setText(produtoProcurado.marca)
            }
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
    private fun modificarDados(){
        doAsync {
            val valorInicial = txtValorInicialProdutoEditar.text.toString()
                .replace(".","").replace(",",".").toFloat()
            val valorVenda = txtValorProdVendaEditar.text.toString()
                .replace(".","").replace(",",".").toFloat()

            //modifica as informações cadastradas com aquilo que foi inserido
            produtoProcurado.descricao = txtNomeProdutoEditar.text.toString()
            produtoProcurado.valorInicial = valorInicial
            produtoProcurado.valorVenda = valorVenda
            produtoProcurado.quantidade = txtQuantProdEditar.text.toString().toInt()
            produtoProcurado.marca = txtMarcaProdutoEdit.text.toString()

            //faz a atualização no banco de dados
            viewModel.updateEstoque(produtoProcurado)
            modificarDados()
        }
    }
    private fun AnkoAsyncContext<EditarProdutoActivity>.modificarDados(){
        uiThread {
            val toast = Toast.makeText(this@EditarProdutoActivity, "Item atualizado com sucesso!",Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM,0,32)
            toast.show()
            finish()
        }
    }
    //verifica se os campos estão preenchidos e se não estiverem mostra msgs de erro
    private fun validDados():Boolean{
        val descricao = txtNomeProdutoEditar.text.toString()
        val marca = txtMarcaProdutoEdit.text.toString()
        val valorInicial = txtValorInicialProdutoEditar.text.toString()
        val valorVenda = txtValorProdVendaEditar.text.toString()
        val quantidade = txtQuantProdEditar.text.toString()
        var x = 0

        if (descricao.isEmpty()){
            layoutNomeProdutoEditar.error = "Campo obrigatório."
            x += 1
        }
        if (marca.isEmpty()){
            layoutMarcaProdutoEdit.error = "Campo obrigatório."
            x += 1
        }
        if (valorInicial.isEmpty()){
            layoutValorProdutoEditar.error = "Campo obrigatório."
            x += 1
        }
        if (valorVenda.isEmpty()){
            layoutValorProdVendaEditar.error = "Campo obrigatório."
            x += 1
        }
        if (quantidade.isEmpty()){
            layoutQuantProdEditar.error = "Campo obrigatório."
            x += 1
        }
        if (x!= 0){
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