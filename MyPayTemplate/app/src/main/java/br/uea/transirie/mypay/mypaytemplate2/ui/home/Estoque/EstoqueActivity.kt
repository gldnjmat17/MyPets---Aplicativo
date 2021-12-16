package br.uea.transirie.mypay.mypaytemplate2.ui.home.Estoque

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.adapters.EstoqueAdapter
import br.uea.transirie.mypay.mypaytemplate2.model.Estoque
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda.ManterItemVendaViewModel
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import kotlinx.android.synthetic.main.activity_estoque.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EstoqueActivity : AppCompatActivity() {
    private lateinit var cpfGerente:String
    private lateinit var adapter:EstoqueAdapter
    private lateinit var produtosCadastrados:List<Estoque>
    override fun onResume() {
        super.onResume()
        doAsync {
            val viewModel = ManterEstoqueViewModel(AppDatabase.getDatabase(this@EstoqueActivity))
            produtosCadastrados = viewModel.getAllEstoque() as MutableList
            produtosCadastrados = produtosCadastrados.filter {
                it.gerenteCPF == cpfGerente
            } as MutableList
            uiThread {
                adapter.swapData(produtosCadastrados)
                if (produtosCadastrados.size == 0){
                    recyclerViewProdutos.visibility = View.INVISIBLE
                    txtAjudaEstoque.visibility = View.VISIBLE
                }else{
                    recyclerViewProdutos.visibility = View.VISIBLE
                    txtAjudaEstoque.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item!!.itemId
        if (id == R.id.ic_add){
            startActivity(Intent(this, CadastrarProdutoActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setItemsVisibility(
        menu: Menu, exception: MenuItem,
        visible: Boolean
    ) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            if (item !== exception) item.isVisible = visible
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_estoque, menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.id_search)
        val searchView = searchItem?.actionView as SearchView

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                this@EstoqueActivity.setItemsVisibility(menu, searchItem, false)
                return true
            }
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                this@EstoqueActivity.setItemsVisibility(menu, searchItem, true)
                return true
            }
        })
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estoque)
        //resgata o usuário que está logado
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        cpfGerente = AppPreferences.getCPFGerente(this)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.appBarEstoque)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        adapter = EstoqueAdapter(
            this,
            mutableListOf(),
            ::onEditProduto,
            ::onRemoveProduto
        )
        recyclerViewProdutos.adapter = adapter
        recyclerViewProdutos.layoutManager = LinearLayoutManager(this)
        doAsync {
            val viewModel = ManterEstoqueViewModel(AppDatabase.getDatabase(this@EstoqueActivity))
            produtosCadastrados = viewModel.getAllEstoque() as MutableList
            produtosCadastrados = produtosCadastrados.filter {
                it.gerenteCPF == cpfGerente
            } as MutableList
            uiThread {
                adapter.swapData(produtosCadastrados)
                if (produtosCadastrados.size == 0){
                    recyclerViewProdutos.visibility = View.INVISIBLE
                    txtAjudaEstoque.visibility = View.VISIBLE
                }else{
                    recyclerViewProdutos.visibility = View.VISIBLE
                    txtAjudaEstoque.visibility = View.INVISIBLE
                }
            }
        }
    }
    private fun onEditProduto(produto: Estoque){
        val intent = Intent(this, EditarProdutoActivity::class.java)
        intent.putExtra("codBarras_produto", produto.codigoBarras)
        startActivity(intent)
    }
    private fun onRemoveProduto(produto: Estoque){
        val builder:AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Tem certeza que deseja excluir esse item?")
        builder.setPositiveButton("EXCLUIR"){ dialog, _->
            doAsync {
                val viewModel = ManterEstoqueViewModel(AppDatabase.getDatabase(this@EstoqueActivity))
                viewModel.deletarEstoque(produto)
                val viewModelItemVenda = ManterItemVendaViewModel(AppDatabase.getDatabase(this@EstoqueActivity))
                val itemVenda = viewModelItemVenda.itemVendaByCodBarras(produto.codigoBarras)
                viewModelItemVenda.deletarItemVenda(itemVenda)
                produtosCadastrados = viewModel.getAllEstoque() as MutableList
                produtosCadastrados = produtosCadastrados.filter {
                    it.gerenteCPF == cpfGerente
                } as MutableList
                uiThread {
                    val toast = Toast.makeText(this@EstoqueActivity,"Item excluído com sucesso!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,32)
                    toast.show()
                    adapter.swapData(produtosCadastrados)
                    if (produtosCadastrados.size == 0){
                        recyclerViewProdutos.visibility = View.INVISIBLE
                        txtAjudaEstoque.visibility = View.VISIBLE
                    }else{
                        recyclerViewProdutos.visibility = View.VISIBLE
                        txtAjudaEstoque.visibility = View.INVISIBLE
                    }
                }
                dialog.dismiss()
            }
        }
        builder.setNegativeButton("CANCELAR"){ dialog, _-> dialog.dismiss()}
        builder.show()
    }
    private fun searchDataBase(query: String){
        if (query.isEmpty()){
            adapter.swapData(produtosCadastrados)
        }else{
            val listaFiltrada = produtosCadastrados.filter {
                it.descricao.toUpperCase().contains(query.toUpperCase())
            }
            adapter.swapData(listaFiltrada)
        }
    }
}