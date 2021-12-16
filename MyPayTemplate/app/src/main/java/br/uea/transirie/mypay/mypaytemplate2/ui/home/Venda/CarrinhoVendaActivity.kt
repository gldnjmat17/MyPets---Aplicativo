package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.adapters.CarrinhoAdapter
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import br.uea.transirie.mypay.mypaytemplate2.model.Estoque
import br.uea.transirie.mypay.mypaytemplate2.model.ItemVenda
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Estoque.ManterEstoqueViewModel
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_carrinho_venda.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.DecimalFormat

class CarrinhoVendaActivity : AppCompatActivity() {
    private lateinit var cpfGerente:String
    private lateinit var adapter: CarrinhoAdapter
    private lateinit var cadastros:MutableList<ItemVenda>
    private lateinit var produtos:MutableList<Estoque>
    private var subTotal:Float = 0f
    private var df = DecimalFormat("0.00")
    override fun onResume() {
        gerarValores()
        super.onResume()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrinho_venda)
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        cpfGerente = AppPreferences.getCPFGerente(this)
        val clienteJson = preference.getString("clienteEscolhido","")
        if (clienteJson.isNullOrEmpty()){
            txtCliente.visibility = View.GONE
        }else{
            val cliente = Gson().fromJson(clienteJson,Cliente::class.java)
            txtCliente.text = "Cliente: ${cliente.nome}"
        }
        toolbar79.setNavigationOnClickListener { finish() }
        doAsync {
            val viewModelItem = ManterItemVendaViewModel(AppDatabase.getDatabase(this@CarrinhoVendaActivity))
            cadastros = viewModelItem.getAllItemVenda().filter {
                it.gerenteCPF == cpfGerente && it.quantidade > 0
            } as MutableList
            var x = 0
            subTotal = 0f
            while (x<cadastros.size){
                subTotal += cadastros[x].subTotalItem
                x+=1
            }
            val viewModelProduto = ManterEstoqueViewModel(AppDatabase.getDatabase(this@CarrinhoVendaActivity))
            produtos = viewModelProduto.getAllEstoque().filter {
                it.gerenteCPF == cpfGerente
            } as MutableList
            uiThread {
                val sharedEditor = preference.edit()
                sharedEditor.putString("subTotalVenda",subTotal.toString())
                sharedEditor.apply()
                adapter = CarrinhoAdapter(
                    this@CarrinhoVendaActivity,
                    cadastros,
                    produtos
                )
                recyclerViewCarrinhoVenda.adapter = adapter
                recyclerViewCarrinhoVenda.layoutManager = LinearLayoutManager(this@CarrinhoVendaActivity)
                gerarValores()
            }
        }
        btInfoCarrinho.setOnClickListener {
            val builder:AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("O sistema automaticamente converterá o valor da compra em pontos. Sendo assim, um real equivale a um ponto")
            builder.setPositiveButton("OK"){dialog,_->
                dialog.dismiss()
            }
            builder.show()
        }
        btAvancar.setOnClickListener {
            startActivity(Intent(this,ModoDePagamentoActivity::class.java))
        }
        btDesconto.setOnClickListener {
            when(btDesconto.text){
                "INSERIR DESCONTO (%)"->{
                    startActivity(Intent(this,DescontoActivity::class.java))
                }
                "REMOVER DESCONTO (%)"->{
                    val sharedEditor = preference.edit()
                    sharedEditor.putString("descontoVenda","0")
                    sharedEditor.apply()
                    btDesconto.text = "INSERIR DESCONTO (%)"
                    gerarValores()
                }
            }
        }
    }
    private fun gerarValores(){
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        val descontoResgatar = preference.getString("descontoVenda","0").toString().toFloat()

        Log.d("porcentagem",descontoResgatar.toString())
        val desconto = (subTotal/100) * descontoResgatar
        Log.d("desconto", desconto.toString())
        val total = subTotal - desconto

        txtDescontoView.text = "Descontos (${df.format(descontoResgatar).replace(".",",")}%)"
        txtSubtotalCarrinhoView.text = "R$ ${df.format(subTotal).replace(".",",")}"
        txtDescontoCarrinhoView.text = "- R$ ${df.format(desconto).replace(".",",")}"
        txtTotalCarrinhoView.text = "R$ ${df.format(total).replace(".",",")}"

        if (df.format(subTotal) != df.format(total)){
            btDesconto.text = "REMOVER DESCONTO (%)"
        }else{
            btDesconto.text = "INSERIR DESCONTO (%)"
        }
    }
}