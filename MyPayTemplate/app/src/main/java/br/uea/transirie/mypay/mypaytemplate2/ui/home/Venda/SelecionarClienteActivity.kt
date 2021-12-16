package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.adapters.SelecionarClienteAdapter
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente.CadastrarClienteActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente.ManterClientesViewModel
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskChangedListener
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskSL
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskStyle
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_selecionar_cliente.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception


class SelecionarClienteActivity : AppCompatActivity() {
    private lateinit var cpfGerente:String
    private lateinit var adapter:SelecionarClienteAdapter
    private lateinit var clientes:List<Cliente>
    private lateinit var clienteEscolhido:Cliente
    private val PADRAO_CPF = "___.___.___-__"
    override fun onResume() {
        doAsync {
            val viewModel = ManterClientesViewModel(AppDatabase.getDatabase(this@SelecionarClienteActivity))
            clientes = viewModel.getAllClientes().filter {
                it.gerenteCPF == cpfGerente
            } as MutableList
            uiThread {
                adapter.swapData(clientes)
            }
        }
        super.onResume()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selecionar_cliente)
        mask()
        //resgata o usuário que está logado
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        cpfGerente = AppPreferences.getCPFGerente(this)

        toolbar450.setNavigationOnClickListener { finish() }
        adapter = SelecionarClienteAdapter(
            this,
            mutableListOf(),
            ::onSelecionar
        )
        recyclerViewSelecionar.adapter = adapter
        recyclerViewSelecionar.layoutManager = LinearLayoutManager(this)
        doAsync {
            val viewModel = ManterClientesViewModel(AppDatabase.getDatabase(this@SelecionarClienteActivity))
            clientes = viewModel.getAllClientes().filter {
                it.gerenteCPF == cpfGerente
            } as MutableList
            uiThread {
                adapter.swapData(clientes)
            }
        }
        val cpfProcurar = findViewById<EditText>(R.id.txtCpfSelecionarCliente)
        cpfProcurar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchDataBase(s.toString())
            }
        })
        btAdicionarCliente.setOnClickListener {
            startActivity(Intent(this,CadastrarClienteActivity::class.java))
        }
        btSelecionar.setOnClickListener {
            try {
                val cliente = clienteEscolhido
                val clienteJson = Gson().toJson(clienteEscolhido)
                val sharedEditor = preference.edit()
                sharedEditor.putString("clienteEscolhido",clienteJson)
                sharedEditor.apply()
                startActivity(Intent(this,CarrinhoVendaActivity::class.java))
                finish()
            }catch (ex:Exception){
                val toast = Toast.makeText(this,"Selecione um cliente",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM,0,200)
                toast.show()
            }
        }
    }
    private fun mask(){
        val maskCPF = MaskSL(
            value = PADRAO_CPF,
            character = '_',
            style = MaskStyle.NORMAL
        )
        val listener = MaskChangedListener(maskCPF)
        txtCpfSelecionarCliente.addTextChangedListener(listener)
    }
    private fun searchDataBase(query:String){
        if (query.isEmpty()){
            adapter.swapData(clientes)
        }else{
            val listaFiltrada = clientes.filter {
                it.cpf.toUpperCase().contains(query.toUpperCase())
            }
            adapter.swapData(listaFiltrada)
            if (listaFiltrada.isEmpty()){
                txtAjudaSelecionar.visibility = View.VISIBLE
            }else{
                txtAjudaSelecionar.visibility = View.INVISIBLE
            }
        }
    }
    private fun onSelecionar(cliente:Cliente){
        try {
            clienteEscolhido.escolhido = false
        }catch (ex:Exception){}
        clienteEscolhido = cliente
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