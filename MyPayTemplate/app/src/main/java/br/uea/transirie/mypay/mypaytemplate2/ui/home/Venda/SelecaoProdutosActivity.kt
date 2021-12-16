package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.adapters.SelecaoProdutoAdapter
import br.uea.transirie.mypay.mypaytemplate2.model.Estoque
import br.uea.transirie.mypay.mypaytemplate2.model.ItemVenda
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Estoque.ManterEstoqueViewModel
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import kotlinx.android.synthetic.main.activity_selecao_produtos.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.DecimalFormat

class SelecaoProdutosActivity : AppCompatActivity() {
    private lateinit var cpfGerente:String
    private lateinit var adapter:SelecaoProdutoAdapter
    private lateinit var cadastros:List<ItemVenda>
    private lateinit var produtos:MutableList<Estoque>
    private var subTotal:Float = 0f
    private var df = DecimalFormat("0.00")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_procurar,menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.id_search_brinde)
        val searchView = searchItem?.actionView as SearchView

        val editText = searchView.findViewById<EditText>(R.id.search_src_text)
        editText.setTextColor(ContextCompat.getColor(this, R.color.white))
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.search_color))
        editText.hint = "Pesquisar..."
        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    searchDataBase(query)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchDataBase(query)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
    private fun searchDataBase(query:String){
        if (query.isEmpty()){
            adapter.swapData(cadastros)
        }else{
            val listaProduto = produtos.filter {
                it.descricao.toUpperCase().contains(query.toUpperCase())
            }
            var x = 0
            val tamanho = listaProduto.size
            val listaItem:MutableList<ItemVenda> = mutableListOf()
            while (x<tamanho){
                val item = cadastros.filter {
                    it.codigoBarras == listaProduto[x].codigoBarras
                }
                listaItem.add(item[0])
                adapter.swapData(listaItem)
                x +=1
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selecao_produtos)

        //resgata o usuário que está logado
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        cpfGerente = AppPreferences.getCPFGerente(this)

        val toast = Toast.makeText(this@SelecaoProdutosActivity,"Adicione produtos ao carrinho", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM,0,170)
        btCarrinho.setOnClickListener {
            toast.cancel()
            doAsync {
                val viewModel = ManterItemVendaViewModel(AppDatabase.getDatabase(this@SelecaoProdutosActivity))
                val lista = viewModel.getAllItemVenda().filter {
                    it.gerenteCPF == cpfGerente && it.quantidade > 0
                }
                uiThread {
                    if (lista.isEmpty()){
                        toast.show()
                    }else{
                        val clienteJson = preference.getString("clienteEscolhido","")
                        if (clienteJson.isNullOrEmpty()){
                            val builder:AlertDialog.Builder = AlertDialog.Builder(this@SelecaoProdutosActivity)
                            builder.setMessage("Você gostaria de vincular essa compra a um cliente?")
                            builder.setPositiveButton("VINCULAR"){dialog,_->
                                startActivity(Intent(this@SelecaoProdutosActivity,SelecionarClienteActivity::class.java))
                                dialog.dismiss()
                            }
                            builder.setNegativeButton("NÃO"){dialog,_->
                                startActivity(Intent(this@SelecaoProdutosActivity,CarrinhoVendaActivity::class.java))
                                dialog.dismiss()
                            }
                            builder.show()
                        }else{
                            startActivity(Intent(this@SelecaoProdutosActivity,CarrinhoVendaActivity::class.java))
                        }
                    }
                }
            }
        }
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarS)
        setSupportActionBar(toolbar)
        toolbarS.setNavigationOnClickListener {
            doAsync {
                val viewModelItem = ManterItemVendaViewModel(AppDatabase.getDatabase(this@SelecaoProdutosActivity))
                cadastros = viewModelItem.getAllItemVenda().filter {
                    it.gerenteCPF == cpfGerente
                } as MutableList
                cadastros.forEach { itemVenda ->
                    itemVenda.quantidade = 0
                    itemVenda.subTotalItem = 0f
                    viewModelItem.updateItemVenda(itemVenda)
                }
                uiThread {
                    val sharedEditor = preference.edit()
                    sharedEditor.putString("descontoVenda","0")
                    sharedEditor.putString("clienteEscolhido","")
                    sharedEditor.apply()
                }
            }
            finish() }
        doAsync {
            val viewModelItem = ManterItemVendaViewModel(AppDatabase.getDatabase(this@SelecaoProdutosActivity))
            cadastros = viewModelItem.getAllItemVenda().filter {
                it.gerenteCPF == cpfGerente
            } as MutableList
            val viewModelProduto = ManterEstoqueViewModel(AppDatabase.getDatabase(this@SelecaoProdutosActivity))
            produtos = viewModelProduto.getAllEstoque().filter {
                it.gerenteCPF == cpfGerente
            } as MutableList
            gerarTotal()
            uiThread {
                adapter = SelecaoProdutoAdapter(
                    this@SelecaoProdutosActivity,
                    mutableListOf(),
                    produtos,
                    ::onMais,
                    ::onMenos
                )
                recyclerViewSelecao.adapter = adapter
                recyclerViewSelecao.layoutManager = LinearLayoutManager(this@SelecaoProdutosActivity)
                adapter.swapData(cadastros)
            }
        }
    }
    private fun onMenos(item:ItemVenda){
        item.quantidade = item.quantidade - 1
        val produto = produtos.filter {
            it.codigoBarras == item.codigoBarras
        }[0]
        item.subTotalItem = item.quantidade * produto.valorVenda
        doAsync {
            val viewModelItemVenda = ManterItemVendaViewModel(AppDatabase.getDatabase(this@SelecaoProdutosActivity))
            viewModelItemVenda.updateItemVenda(item)
            gerarTotal()
        }
    }
    private fun onMais(item: ItemVenda){
        item.quantidade = item.quantidade + 1
        val produto = produtos.filter {
            it.codigoBarras == item.codigoBarras
        }[0]
        item.subTotalItem = item.quantidade*produto.valorVenda
        doAsync {
            val viewModelItemVenda = ManterItemVendaViewModel(AppDatabase.getDatabase(this@SelecaoProdutosActivity))
            viewModelItemVenda.updateItemVenda(item)
            gerarTotal()
        }
    }
    private fun gerarTotal(){
        doAsync {
            val viewModel = ManterItemVendaViewModel(AppDatabase.getDatabase(this@SelecaoProdutosActivity))
            val lista = viewModel.getAllItemVenda().filter {
                it.gerenteCPF == cpfGerente && it.quantidade > 0
            }
            val quantidade = lista.size
            var x = 0
            subTotal = 0f
            while (x < lista.size){
                subTotal += lista[x].subTotalItem
                x += 1
            }
            uiThread {
                if (lista.isEmpty()){
                    val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
                    val sharedEditor = preference.edit()
                    sharedEditor.putString("descontoVenda","0")
                    sharedEditor.putString("clienteEscolhido","")
                    sharedEditor.apply()
                }
                txtSubtotal.text = "R$ ${df.format(subTotal).replace(".",",")}"
                if (quantidade == 1){
                    txtIntes.text = "$quantidade item adicionado"
                }else{
                    txtIntes.text = "$quantidade itens adicionados"
                }
            }
        }
    }
}