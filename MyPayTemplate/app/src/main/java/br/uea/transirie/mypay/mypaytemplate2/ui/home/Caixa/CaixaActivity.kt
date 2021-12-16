package br.uea.transirie.mypay.mypaytemplate2.ui.home.Caixa

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityCaixaBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Pagamento
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_VERIF_ABRIR_CAIXA
import br.uea.transirie.mypay.mypaytemplate2.ui.home.HistoricoVendas.HistoricoVendasActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.DecimalFormat
import java.time.LocalDate

class CaixaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCaixaBinding
    private lateinit var caixaViewModel: CaixaViewModel
    private lateinit var dataHoje: LocalDate
    private var df = DecimalFormat("00.00")
    private lateinit var gerenteCPF:String
    private lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaixaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataHoje = LocalDate.now()
        gerenteCPF = AppPreferences.getCPFGerente(this)
    }

    override fun onResume() {
        super.onResume()
        listeners()
        calcularGanhosDespesas()
        preencherTela()
    }
    private fun listeners(){
        binding.toolbar14.setNavigationOnClickListener {
            finish()
        }
        binding.btHistoricoVendas.setOnClickListener {
            startActivity(Intent(this,HistoricoVendasActivity::class.java))
        }
        binding.btMovimentarCaixa.setOnClickListener {
            startActivity(Intent(this,MovimentarCaixaActivity::class.java))
        }
        binding.btFecharCaixa.setOnClickListener {
            startActivity(Intent(this,FecharCaixaActivity::class.java))
        }
    }
    private fun preencherTela(){
        var mes = dataHoje.monthValue.toString()
        if (mes.length < 2){
            mes = "0$mes"
        }
        binding.data.text = "${dataHoje.dayOfMonth}/$mes/${dataHoje.year}"
    }
    private fun calcularGanhosDespesas(){
        doAsync {
            caixaViewModel = CaixaViewModel(AppDatabase.getDatabase(this@CaixaActivity))
            caixaViewModel.getRecebidosDia(LocalDate.now(),gerenteCPF)
            caixaViewModel.getDespesasDia(LocalDate.now(),gerenteCPF)
            uiThread {
                binding.recebimentosDinheiro.text = "R$ ${df.format(caixaViewModel.getPagamentoDinheiro()).replace(".",",")}"
                binding.recebimentosCartao.text = "R$ ${df.format(caixaViewModel.getPagamentoCartao()).replace(".",",")}"
                binding.despesasDinheiro.text = "R$ ${df.format(caixaViewModel.getDespesaDinheiro()).replace(".",",")}"
                binding.despesasCartao.text = "R$ ${df.format(caixaViewModel.getDespesaCartao()).replace(".",",")}"
                val dataHoje = LocalDate.now()
                // resgata usuário que está logado
                preference = getSharedPreferences(PREF_VERIF_ABRIR_CAIXA, MODE_PRIVATE)
                val statusAbrirCaixa= Gson().fromJson(preference.getString(gerenteCPF, Gson().toJson(AbrirCaixaStatus(status = false, data = LocalDate.now(), valor =0f))),
                    AbrirCaixaStatus::class.java)
                val valFormat=String.format("%.2f", statusAbrirCaixa.valor)
                binding.txtCaixaInicial.text="R$ $valFormat"
                //val caixaInitFormat=Pagamento.caixaInicial.valorCaixaInicial.toString().replace(".", ",")
                //binding.txtCaixaInicial.text="R$ ${caixaInitFormat}"
                binding.totalParcial.text = "R$ ${df.format(caixaViewModel.getTotal() + statusAbrirCaixa.valor).replace(".",",")}"
                Pagamento.dataHoje.dataHoje = LocalDate.now()
                Pagamento.totalClientes.totalCliente = caixaViewModel.getClientes()
                Pagamento.dinheiroRecebimento.valorDinheiro = caixaViewModel.getPagamentoDinheiro().toFloat()
                Pagamento.creditoRecebimento.valorCredito = caixaViewModel.getPagamentoCredito().toFloat()
                Pagamento.debitoRecebimento.valorDebito = caixaViewModel.getPagamentoDebito().toFloat()
                Pagamento.despesasCartao.valorDespesasCartaoDebito = caixaViewModel.getDespesaDebito().toFloat()
                Pagamento.despesasCartao.valorDespesasCartaoCredito = caixaViewModel.getDespesaCredito().toFloat()
                Pagamento.despesasDinheiro.valorDespesasDinheiroDia = caixaViewModel.getDespesaDinheiro().toFloat()
            }
        }
    }
}