package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.*
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente.ManterClientesViewModel
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Estoque.ManterEstoqueViewModel
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Meta.ManterMetaViewModel
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_insira_senha.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class InsiraSenhaActivity : AppCompatActivity() {
    private var total = 0f
    private lateinit var usuario:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insira_senha)

        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        usuario = preference.getString("nome_usuario", "").toString()
        val descontoResgatar = preference.getString("descontoVenda","0").toString().toFloat()
        val subtotal = preference.getString("subTotalVenda","").toString().toFloat()

        val desconto = (subtotal/100) * descontoResgatar
        total = subtotal - desconto

        btConcluir.setOnClickListener {
            if (txtInsiraSenha.text.toString() == "1234"){
                startActivity(Intent(this,PagamentoRecusadoActivity::class.java))
                finish()
            }else if (txtInsiraSenha.text.toString().isNotEmpty()) {
                finalizarVenda()
            } else {
                LayoutInputInsiraSenha.error = "Insira senha"
            }
        }
    }
    fun finalizarVenda(){
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)

        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a")
        val currentDateTimeString = LocalDateTime.now().format(dtf)

        val gerenteCPF = AppPreferences.getCPFGerente(this)

        val clienteJson = preference.getString("clienteEscolhido","")
        val modoPagamento = preference.getString("pagamento","")

        doAsync {
            val intent = Intent(this@InsiraSenhaActivity,PagamentoRealizadoActivity::class.java)

            val clienteResgatar = Gson().fromJson(clienteJson, Cliente::class.java)
            var clienteCpf = ""
            if (clienteJson.isNullOrEmpty()){
                clienteCpf = ""
            }else{
                clienteCpf = clienteResgatar.cpf
            }

            //resgata lista item venda
            val viewModelItemVenda = ManterItemVendaViewModel(AppDatabase.getDatabase(this@InsiraSenhaActivity))
            var itemVendaLista = viewModelItemVenda.getAllItemVenda().filter {
                it.gerenteCPF == gerenteCPF && it.quantidade > 0
            }

            val db = AppDatabase.getDatabase(this@InsiraSenhaActivity)

            val sharedEditor = preference.edit()
            //zerar desconto
            sharedEditor.putString("descontoVenda","0")
            //tirar cliente
            sharedEditor.putString("clienteEscolhido","")

            var valorInicial = 0f
            //zera a quantidade dos itens venda, calcula o valor inicial e atualiza o estoque
            val viewModelEstoque = ManterEstoqueViewModel(AppDatabase.getDatabase(this@InsiraSenhaActivity))
            var listaProduto:List<Estoque>
            var x = 0
            while (x<itemVendaLista.size){
                //atualiza estoque
                val itemVenda = itemVendaLista[x]
                listaProduto = viewModelEstoque.getAllEstoque().filter {
                    it.codigoBarras == itemVenda.codigoBarras && it.gerenteCPF == gerenteCPF
                }
                listaProduto[0].quantidade = listaProduto[0].quantidade - itemVenda.quantidade
                viewModelEstoque.updateEstoque(listaProduto[0])

                //calcula valor inicial
                valorInicial += listaProduto[0].valorInicial * itemVenda.quantidade

                //zera quantidade
                itemVenda.quantidade = 0
                viewModelItemVenda.updateItemVenda(itemVenda)
                x += 1
            }
            //atualiza meta do usuário
            val viewModelMeta = ManterMetaViewModel(AppDatabase.getDatabase(this@InsiraSenhaActivity))
            val listaMeta = viewModelMeta.getAllMeta().filter {
                it.gerenteCPF == gerenteCPF
            }
            if (listaMeta.isNotEmpty()){
                listaMeta[0].progresso = listaMeta[0].progresso + total
                viewModelMeta.updateMeta(listaMeta[0])
                if (listaMeta[0].progresso >= listaMeta[0].valor){
                    sharedEditor.putString("verificaMeta",listaMeta[0].nome)
                    val excedente = (listaMeta[0].progresso - listaMeta[0].valor)
                    sharedEditor.putFloat("excedenteMeta",excedente)
                }
            }
            //atualiza sistema de brinde
            if(clienteCpf.isNotEmpty()){
                val viewModelCliente = ManterClientesViewModel(AppDatabase.getDatabase(this@InsiraSenhaActivity))
                val cliente = viewModelCliente.clienteByCPF(clienteCpf)
                if (cliente.sistema){
                    cliente.pontos = cliente.pontos.toString().toInt() + total.toInt()
                    viewModelCliente.updateCliente(cliente)
                }
            }
            //inserir venda
            val novoCadastro = Venda(0L,
                gerenteCPF,
                clienteCpf,
                valorInicial,
                total)
            db.vendaDao().insert(novoCadastro)

            //inserir pagamento
            val dataLista = currentDateTimeString.split(" ")
            val dataJson = Gson().toJson(
                LocalDate.parse(dataLista[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            )
            val horaJson = Gson().toJson(
                LocalTime.parse(dataLista[1]+" "+dataLista[2], DateTimeFormatter.ofPattern("hh:mm:ss a"))
            )
            var modoPagamentoObj: TipoPagamento = TipoPagamento.PIX
            when(modoPagamento){
                "credito" ->{
                    modoPagamentoObj = TipoPagamento.CREDITO
                }
                "debito" ->{
                    modoPagamentoObj = TipoPagamento.DEBITO
                }
                "dinheiro" ->{
                    modoPagamentoObj = TipoPagamento.DINHEIRO
                }
            }
            val novoPagamento = Pagamento(0L,
                gerenteCPF,
                clienteCpf,
                Gson().toJson(novoCadastro),
                total,
                dataJson,
                horaJson,
                modoPagamentoObj)
            db.pagamentoDao().insert(novoPagamento)
            //limpar modo de pagamento
            sharedEditor.putString("pagamento","")
            uiThread {
                sharedEditor.apply()
                startActivity(intent)
                finishAffinity()
            }
        }
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