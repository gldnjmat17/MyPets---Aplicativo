package br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.adapters.BrindeDisponivelAdapter
import br.uea.transirie.mypay.mypaytemplate2.model.Brinde
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Brinde.ManterBrindeViewModel
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_resgatar_brinde.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ResgatarBrindeActivity : AppCompatActivity() {
    private lateinit var gerenteCPF:String
    private lateinit var cliente:Cliente
    private lateinit var adapter:BrindeDisponivelAdapter
    private lateinit var brindesCadastrados:List<Brinde>
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_procurar,menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.id_search_brinde)
        val searchView = searchItem?.actionView as SearchView

        val editText = searchView.findViewById<EditText>(R.id.search_src_text)
        editText.setTextColor(ContextCompat.getColor(this, R.color.white))
        searchView.queryHint = ""
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
        setContentView(R.layout.activity_resgatar_brinde)

        val intent = intent
        val clienteJson = intent.getStringExtra("clienteBrinde")
        val clienteResgatar = Gson().fromJson(clienteJson,Cliente::class.java)
        cliente = clienteResgatar

        //resgata o usuário que está logado
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        gerenteCPF = AppPreferences.getCPFGerente(this)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarR)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        adapter = BrindeDisponivelAdapter(
            this,
            mutableListOf(),
            ::onResgatarBrinde,
            cliente
        )
        recyclerViewResgatar.adapter = adapter
        recyclerViewResgatar.layoutManager = LinearLayoutManager(this)
        doAsync {
            val viewModel = ManterBrindeViewModel(AppDatabase.getDatabase(this@ResgatarBrindeActivity))
            brindesCadastrados = viewModel.getAllBrinde() as MutableList
            brindesCadastrados = brindesCadastrados.filter {
                it.gerenteCPF == gerenteCPF && it.quantidade > 0
            } as MutableList
            uiThread {
                txtPontos.text = cliente.pontos.toString()
                adapter.swapData(brindesCadastrados)
            }
        }
    }
    private fun onResgatarBrinde(brinde:Brinde){
        val builder:AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Ao resgatar um brinde seus pontos acumulados sofrerão uma redução. Tem certeza que quer continuar?")
        builder.setPositiveButton("RESGATAR"){dialog,_->
            doAsync {
                val viewModelBrinde = ManterBrindeViewModel(AppDatabase.getDatabase(this@ResgatarBrindeActivity))
                brinde.quantidade = brinde.quantidade - 1
                viewModelBrinde.updateBrinde(brinde)
                val viewModelCliente = ManterClientesViewModel(AppDatabase.getDatabase(this@ResgatarBrindeActivity))
                cliente.pontos = cliente.pontos.toString().toInt() - brinde.pontos
                viewModelCliente.updateCliente(cliente)
                adapter = BrindeDisponivelAdapter(
                    this@ResgatarBrindeActivity,
                    mutableListOf(),
                    ::onResgatarBrinde,
                    cliente
                )
                val viewModel = ManterBrindeViewModel(AppDatabase.getDatabase(this@ResgatarBrindeActivity))
                brindesCadastrados = viewModel.getAllBrinde() as MutableList
                brindesCadastrados = brindesCadastrados.filter {
                    it.gerenteCPF == gerenteCPF && it.quantidade > 0
                } as MutableList
                uiThread {
                    val toast = Toast.makeText(this@ResgatarBrindeActivity,"Brinde resgatado com sucesso!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,32)
                    toast.show()
                    recyclerViewResgatar.adapter = adapter
                    txtPontos.text = cliente.pontos.toString()
                    adapter.swapData(brindesCadastrados)
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("CANCELAR"){dialog,_-> dialog.cancel()}
        builder.show()
    }
    private fun searchDataBase(query:String){
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