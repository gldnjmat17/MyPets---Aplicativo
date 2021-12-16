package br.uea.transirie.mypay.mypaytemplate2.ui.home.Brinde

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.adapters.BrindeAdapter
import br.uea.transirie.mypay.mypaytemplate2.model.Brinde
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import kotlinx.android.synthetic.main.activity_brinde.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class BrindeActivity : AppCompatActivity() {
    private lateinit var gerenteCPF:String
    private lateinit var adapter:BrindeAdapter
    private lateinit var brindesCadastrados:List<Brinde>
    override fun onResume() {
        super.onResume()
        doAsync {
            val viewModel = ManterBrindeViewModel(AppDatabase.getDatabase(this@BrindeActivity))
            brindesCadastrados = viewModel.getAllBrinde() as MutableList
            brindesCadastrados = brindesCadastrados.filter {
                it.gerenteCPF == gerenteCPF
            } as MutableList
            uiThread {
                adapter.swapData(brindesCadastrados)
                if (brindesCadastrados.size == 0){
                    recyclerViewBrinde.visibility = View.INVISIBLE
                    txtAjudaBrinde.visibility = View.VISIBLE
                }else{
                    recyclerViewBrinde.visibility = View.VISIBLE
                    txtAjudaBrinde.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item!!.itemId
        if (id == R.id.ic_add){
            startActivity(Intent(this,CadastrarBrindeActivity::class.java))
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
        inflater.inflate(R.menu.menu_estoque,menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.id_search)
        val searchView = searchItem?.actionView as SearchView

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                this@BrindeActivity.setItemsVisibility(menu, searchItem, false)
                return true
            }
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                this@BrindeActivity.setItemsVisibility(menu, searchItem, true)
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
                    searchDatabase(query)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchDatabase(query)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brinde)
        gerenteCPF = AppPreferences.getCPFGerente(this)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarB)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        adapter = BrindeAdapter(
            this,
            mutableListOf(),
            ::onEditBrinde,
            ::onRemoveBrinde
        )
        recyclerViewBrinde.adapter = adapter
        recyclerViewBrinde.layoutManager = LinearLayoutManager(this)
        doAsync {
            val viewModel = ManterBrindeViewModel(AppDatabase.getDatabase(this@BrindeActivity))
            brindesCadastrados = viewModel.getAllBrinde() as MutableList
            brindesCadastrados = brindesCadastrados.filter {
                it.gerenteCPF == gerenteCPF
            } as MutableList
            uiThread {
                adapter.swapData(brindesCadastrados)
                if (brindesCadastrados.size == 0){
                    recyclerViewBrinde.visibility = View.INVISIBLE
                    txtAjudaBrinde.visibility = View.VISIBLE
                }else{
                    recyclerViewBrinde.visibility = View.VISIBLE
                    txtAjudaBrinde.visibility = View.INVISIBLE
                }
            }
        }
    }
    private fun onEditBrinde(brinde:Brinde){
        val intent = Intent(this,EditarBrindeActivity::class.java)
        intent.putExtra("id_brinde",brinde.id.toInt())
        startActivity(intent)
    }
    private fun onRemoveBrinde(brinde:Brinde){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Tem certeza que deseja excluir esse brinde?")
        builder.setPositiveButton("EXCLUIR"){dialog,_->
            doAsync {
                val viewModel = ManterBrindeViewModel(AppDatabase.getDatabase(this@BrindeActivity))
                viewModel.deletarBrinde(brinde)
                brindesCadastrados = viewModel.getAllBrinde() as MutableList
                brindesCadastrados = brindesCadastrados.filter {
                    it.gerenteCPF == gerenteCPF
                } as MutableList
                uiThread {
                    val toast = Toast.makeText(this@BrindeActivity,"Item excluído com sucesso!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,32)
                    toast.show()
                    adapter.swapData(brindesCadastrados)
                    if (brindesCadastrados.size == 0){
                        recyclerViewBrinde.visibility = View.INVISIBLE
                        txtAjudaBrinde.visibility = View.VISIBLE
                    }else{
                        recyclerViewBrinde.visibility = View.VISIBLE
                        txtAjudaBrinde.visibility = View.INVISIBLE
                    }
                }
                dialog.dismiss()
            }
        }
        builder.setNegativeButton("CANCELAR"){ dialog, _-> dialog.dismiss()}
        builder.show()
    }
    private fun searchDatabase(query:String){
        if (query.isEmpty()){
            adapter.swapData(brindesCadastrados)
        }else{
            val listaFiltrada = brindesCadastrados.filter {
                it.nome.toUpperCase().contains(query.toUpperCase())
            }
            adapter.swapData(listaFiltrada)
        }
    }
}