package br.uea.transirie.mypay.mypaytemplate2.ui.home.Estoque

import android.annotation.SuppressLint
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
import br.uea.transirie.mypay.mypaytemplate2.model.Estoque
import br.uea.transirie.mypay.mypaytemplate2.model.ItemVenda
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskBrMonetaryValue
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_cadastrar_produto.*
import kotlinx.android.synthetic.main.activity_editar_cliente.*
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception

class CadastrarProdutoActivity : AppCompatActivity() {
    private lateinit var novoCadastro:Estoque
    private lateinit var gerenteCPF:String
    private lateinit var itemVenda:ItemVenda
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_produto)
        configuraCampoPreco(txtValorInicialProduto)
        configuraCampoPreco(txtValorProdVenda)
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.colorPrimaryVariant
        )

        gerenteCPF = AppPreferences.getCPFGerente(this)

        //finaliza a activity e volta pra tela anterior
        toolbar5.setNavigationOnClickListener { finish() }

        btCadastrarProduto.setOnClickListener {
            //nenhum erro é apresentado inicialmente
            layoutNomeProduto.error = null
            layoutMarcaProduto.error = null
            layoutValorProduto.error = null
            layoutValorProdVenda.error = null
            layoutCodBarras.error = null
            layoutQuantProd.error = null

            // minimiza o teclado
            it.hideKeyboard()
            if (validDados()){
                fazerCadastroProduto()
            }
        }
    }
    private fun fazerCadastroProduto(){
        val valorInicial = txtValorInicialProduto.text.toString()
            .replace(".","").replace(",",".").toFloat()
        val valorVenda = txtValorProdVenda.text.toString()
            .replace(".","").replace(",",".").toFloat()

        //preenche o objeto com as informações inseridas para cadastro
        novoCadastro = Estoque(0L,
            gerenteCPF,
            txtNomeProduto.text.toString(),
            txtMarcaProduto.text.toString(),
            valorInicial,
            valorVenda,
            txtCodBarras.text.toString(),
            txtQuantProd.text.toString().toInt())

        itemVenda = ItemVenda(0L,
            gerenteCPF,
            txtCodBarras.text.toString(),
            0,
            0f)

        val db = AppDatabase.getDatabase(this)
        doAsync {
            val viewModel = ManterEstoqueViewModel(AppDatabase.getDatabase(this@CadastrarProdutoActivity))
            val produto = viewModel.estoqueByCodBarras(txtCodBarras.text.toString())
            try {
                fazerCadastroProduto(produto)
            }catch (ex:Exception){
                // insere o objeto no banco de dados
                db.estoqueDao().insert(novoCadastro)
                db.itemVendaDao().insert(itemVenda)
                uiThread {
                    val toast = Toast.makeText(this@CadastrarProdutoActivity,"Produto adicionado com sucesso!!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,32)
                    toast.show()
                    finish()
                }
            }
        }
    }
    private fun AnkoAsyncContext<CadastrarProdutoActivity>.fazerCadastroProduto(
        produto:Estoque
    ){
        if(produto.codigoBarras != null){
            uiThread {
                layoutCodBarras.error = "Produto já cadastrado"
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

    //verifica se os campos estão preenchidos e se não estiverem apresenta uma mensagem de erro
    private fun validDados():Boolean{
        val descricao =txtNomeProduto.text.toString()
        val marca = txtMarcaProduto.text.toString()
        val valorInicial = txtValorInicialProduto.text.toString()
        val valorVenda = txtValorProdVenda.text.toString()
        val codBarras = txtCodBarras.text.toString()
        val qntd = txtQuantProd.text.toString()
        var x = 0
        if (descricao.isEmpty()){
            layoutNomeProduto.error = "Campo obrigatório."
            x += 1
        }
        if (marca.isEmpty()){
            layoutMarcaProduto.error = "Campo obrigatório."
            x += 1
        }
        if (valorInicial.isEmpty()){
            layoutValorProduto.error = "Campo obrigatório."
            x += 1
        }
        if (valorVenda.isEmpty()){
            layoutValorProdVenda.error = "Campo obrigatório."
            x += 1
        }
        if (codBarras.isEmpty()){
            layoutCodBarras.error = "Campo obrigatório."
            x += 1
        }
        if (qntd.isEmpty()){
            layoutQuantProd.error = "Campo obrigatório."
            x += 1
        }
        if (x != 0){
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