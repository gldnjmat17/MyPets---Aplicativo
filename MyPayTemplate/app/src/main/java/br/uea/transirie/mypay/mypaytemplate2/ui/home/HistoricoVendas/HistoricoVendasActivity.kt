package br.uea.transirie.mypay.mypaytemplate2.ui.home.HistoricoVendas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Caixa.CaixaViewModel
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.printer.ModulePrinter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_cadastrar_meta.*
import kotlinx.android.synthetic.main.activity_historico_vendas.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class HistoricoVendasActivity : AppCompatActivity() {
    private lateinit var dataMes:String
    private lateinit var gerenteCPF:String
    private var df = DecimalFormat("0.00")
    private var tabSelecionada = 1
    private lateinit var caixaViewModel: CaixaViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico_vendas)

        gerenteCPF = AppPreferences.getCPFGerente(this)
        preencherTela()
        listeners()
    }
    private fun listeners(){
        toolbar741.setNavigationOnClickListener {
            finish()
        }

        btnConsultarLucro.setOnClickListener {
            layoutHistorico.visibility = View.INVISIBLE
            if (validDados()){
                calcularHistorico()
            }
        }
        btImpressao.setOnClickListener {
            if (layoutHistorico.visibility == View.VISIBLE){
                impressao()
            }else{
                val toast = Toast.makeText(this,"Selecione uma data!",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM,0,32)
                toast.show()
            }
        }
    }
    private fun impressao(){
        val data = when(tabSelecionada){
            1 ->{"     ${autoCompleteTextviewPrimeiroHistoricoVendas.text}/$dataMes/${autoCompleteTextviewTerceiroHistoricoVendas.text}\n"}
            2 ->{"     $dataMes/${autoCompleteTextviewSegundoHistoricoVendas.text}\n"}
            3 ->{"     ${autoCompleteTextviewPrimeiroHistoricoVendas.text}\n"}
            else -> {""}
        }
        val str = data + "     Total de clientes: ${caixaViewModel.getClientes()}\n\n" +
                "     Recebimentos em cartão: R$ ${df.format(caixaViewModel.getPagamentoCartao()).replace(".",",")}\n" +
                "     Recebimentos em dinheiro: R$ ${df.format(caixaViewModel.getPagamentoDinheiro()).replace(".",",")}\n" +
                "     Despesas no cartão: R$ ${df.format(caixaViewModel.getDespesaCartao()).replace(".",",")}\n" +
                "     Despesas no dinheiro: R$ ${df.format(caixaViewModel.getDespesaDinheiro()).replace(".",",")}\n\n\n\n"
        ModulePrinter(this).printText(str)

    }
    private fun calcularHistorico(){
        dataMes()
        doAsync {
            caixaViewModel = CaixaViewModel(AppDatabase.getDatabase(this@HistoricoVendasActivity))
            when(tabSelecionada){
                1 ->{
                    caixaViewModel.getDespesasDia(LocalDate.parse("${autoCompleteTextviewPrimeiroHistoricoVendas.text}/$dataMes/${autoCompleteTextviewTerceiroHistoricoVendas.text}", DateTimeFormatter.ofPattern("dd/MM/yyyy")),gerenteCPF)
                    caixaViewModel.getRecebidosDia(LocalDate.parse("${autoCompleteTextviewPrimeiroHistoricoVendas.text}/$dataMes/${autoCompleteTextviewTerceiroHistoricoVendas.text}", DateTimeFormatter.ofPattern("dd/MM/yyyy")),gerenteCPF)
                    uiThread {
                        txtDataConsulta.text = "${autoCompleteTextviewPrimeiroHistoricoVendas.text}/$dataMes/${autoCompleteTextviewTerceiroHistoricoVendas.text}"
                    }
                }
                2 ->{
                    caixaViewModel.getDespesasMes(LocalDate.parse("01/$dataMes/${autoCompleteTextviewSegundoHistoricoVendas.text}", DateTimeFormatter.ofPattern("dd/MM/yyyy")),gerenteCPF)
                    caixaViewModel.getRecebidosMes(LocalDate.parse("01/$dataMes/${autoCompleteTextviewSegundoHistoricoVendas.text}", DateTimeFormatter.ofPattern("dd/MM/yyyy")),gerenteCPF)
                    uiThread {
                        txtDataConsulta.text = "$dataMes/${autoCompleteTextviewSegundoHistoricoVendas.text}"
                    }
                }
                3 ->{
                    caixaViewModel.getDespesasAno(LocalDate.parse("01/01/${autoCompleteTextviewPrimeiroHistoricoVendas.text}", DateTimeFormatter.ofPattern("dd/MM/yyyy")),gerenteCPF)
                    caixaViewModel.getRecebidosAno(LocalDate.parse("01/01/${autoCompleteTextviewPrimeiroHistoricoVendas.text}", DateTimeFormatter.ofPattern("dd/MM/yyyy")),gerenteCPF)
                    uiThread {
                        txtDataConsulta.text = "${autoCompleteTextviewPrimeiroHistoricoVendas.text}"
                    }
                }
            }
            uiThread {
                if (caixaViewModel.getPagamentoCartao() != 0.0 ||
                    caixaViewModel.getPagamentoDinheiro() != 0.0 ||
                    caixaViewModel.getDespesaCartao() != 0.0 ||
                    caixaViewModel.getDespesaDinheiro() != 0.0){
                    txtTotalClientesRelatorio.text = "Total de clientes: ${caixaViewModel.getClientes()}"
                    txtRecebimentosCartaoHistorico.text = "Total: ${df.format(caixaViewModel.getPagamentoCartao()).replace(".",",")}"
                    txtRecebimentoDinheiroHistorico.text = "Total: ${df.format(caixaViewModel.getPagamentoDinheiro()).replace(".",",")}"
                    txtDespesaCartaoHistorico.text = "Total: ${df.format(caixaViewModel.getDespesaCartao()).replace(".",",")}"
                    txtDespesaDinheiroHistorico.text = "Total: ${df.format(caixaViewModel.getDespesaDinheiro()).replace(".",",")}"
                    layoutHistorico.visibility = View.VISIBLE
                }else{
                    val toast = Toast.makeText(this@HistoricoVendasActivity, "Nenhuma atividade foi registrada nessa data", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,32)
                    toast.show()
                }
            }
        }
    }
    private fun validDados():Boolean{
        when (tabSelecionada){
            1 -> {
                if (autoCompleteTextviewPrimeiroHistoricoVendas.text.toString().isEmpty()){
                    val toast = Toast.makeText(this,"Selecione uma data!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,32)
                    toast.show()
                    return false
                }
                if (autoCompleteTextviewSegundoHistoricoVendas.text.toString().isEmpty()){
                    val toast = Toast.makeText(this,"Selecione uma data!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,32)
                    toast.show()
                    return false
                }
                if (autoCompleteTextviewTerceiroHistoricoVendas.text.toString().isEmpty()){
                    val toast = Toast.makeText(this,"Selecione uma data!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,32)
                    toast.show()
                    return false
                }
                try {
                    dataMes()
                    val spf = SimpleDateFormat("dd/MM/yyyy")
                    spf.isLenient = false
                    val validaData = spf.parse("${autoCompleteTextviewPrimeiroHistoricoVendas.text}/$dataMes/${autoCompleteTextviewTerceiroHistoricoVendas.text}")
                }catch (ex:Exception){
                    val toast = Toast.makeText(this,"Data inválida",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,32)
                    toast.show()
                    return false
                }
                return true
            }
            2 -> {
                if (autoCompleteTextviewPrimeiroHistoricoVendas.text.toString().isEmpty()){
                    val toast = Toast.makeText(this,"Selecione uma data!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,32)
                    toast.show()
                    return false
                }
                if (autoCompleteTextviewSegundoHistoricoVendas.text.toString().isEmpty()){
                    val toast = Toast.makeText(this,"Selecione uma data!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,32)
                    toast.show()
                    return false
                }
                return true
            }
            3 -> {
                if (autoCompleteTextviewPrimeiroHistoricoVendas.text.toString().isEmpty()){
                    val toast = Toast.makeText(this,"Selecione uma data!",Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,32)
                    toast.show()
                    return false
                }
                return true
            }
        }
        return true
    }
    private fun preencherTela(){
        val arrayAdapterDia  = ArrayAdapter(this,R.layout.dropdown_meta, resources.getStringArray(R.array.Dias))
        autoCompleteTextviewPrimeiroHistoricoVendas.setAdapter(arrayAdapterDia)
        val arrayAdapterMes = ArrayAdapter(this,R.layout.dropdown_meta,resources.getStringArray(R.array.MesRed))
        autoCompleteTextviewSegundoHistoricoVendas.setAdapter(arrayAdapterMes)
        val arrayAdapterAno = ArrayAdapter(this,R.layout.dropdown_meta, resources.getStringArray(R.array.Ano))
        autoCompleteTextviewTerceiroHistoricoVendas.setAdapter(arrayAdapterAno)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                layoutHistorico.visibility = View.INVISIBLE
                when {
                    tab?.contentDescription?.equals("diario") == true -> {
                        textInputLayoutPrimeiro.visibility = View.VISIBLE
                        textInputLayoutPrimeiro.hint = "Dia"
                        autoCompleteTextviewPrimeiroHistoricoVendas.visibility = View.VISIBLE
                        autoCompleteTextviewPrimeiroHistoricoVendas.setText("")
                        autoCompleteTextviewPrimeiroHistoricoVendas.setAdapter(arrayAdapterDia)
                        textInputLayoutSegundo.visibility = View.VISIBLE
                        textInputLayoutSegundo.hint = "Mês"
                        autoCompleteTextviewSegundoHistoricoVendas.visibility = View.VISIBLE
                        autoCompleteTextviewSegundoHistoricoVendas.setText("")
                        autoCompleteTextviewSegundoHistoricoVendas.setAdapter(arrayAdapterMes)
                        textInputLayoutTerceiro.visibility = View.VISIBLE
                        textInputLayoutTerceiro.hint = "Ano"
                        autoCompleteTextviewTerceiroHistoricoVendas.visibility = View.VISIBLE
                        autoCompleteTextviewTerceiroHistoricoVendas.setText("")
                        autoCompleteTextviewTerceiroHistoricoVendas.setAdapter(arrayAdapterAno)
                        tabSelecionada = 1
                    }
                    tab?.contentDescription?.equals("mensal") == true -> {
                        textInputLayoutPrimeiro.visibility = View.VISIBLE
                        textInputLayoutPrimeiro.hint = "Mês"
                        autoCompleteTextviewPrimeiroHistoricoVendas.visibility = View.VISIBLE
                        autoCompleteTextviewPrimeiroHistoricoVendas.setText("")
                        autoCompleteTextviewPrimeiroHistoricoVendas.setAdapter(arrayAdapterMes)
                        textInputLayoutSegundo.visibility = View.VISIBLE
                        textInputLayoutSegundo.hint = "Ano"
                        autoCompleteTextviewSegundoHistoricoVendas.visibility = View.VISIBLE
                        autoCompleteTextviewSegundoHistoricoVendas.setText("")
                        autoCompleteTextviewSegundoHistoricoVendas.setAdapter(arrayAdapterAno)
                        textInputLayoutTerceiro.visibility = View.INVISIBLE
                        autoCompleteTextviewTerceiroHistoricoVendas.visibility = View.INVISIBLE
                        tabSelecionada = 2
                    }
                    tab?.contentDescription?.equals("anual") == true -> {
                        textInputLayoutPrimeiro.visibility = View.VISIBLE
                        textInputLayoutPrimeiro.hint = "Ano"
                        autoCompleteTextviewPrimeiroHistoricoVendas.visibility = View.VISIBLE
                        autoCompleteTextviewPrimeiroHistoricoVendas.setText("")
                        autoCompleteTextviewPrimeiroHistoricoVendas.setAdapter(arrayAdapterAno)
                        textInputLayoutSegundo.visibility = View.INVISIBLE
                        autoCompleteTextviewSegundoHistoricoVendas.visibility = View.INVISIBLE
                        textInputLayoutTerceiro.visibility = View.INVISIBLE
                        autoCompleteTextviewTerceiroHistoricoVendas.visibility = View.INVISIBLE
                        tabSelecionada = 3
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // handle tab unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // handel tab reselected
            }

        })
    }
    private fun dataMes(){
        var mes = ""
        if (tabSelecionada == 1){
            mes = autoCompleteTextviewSegundoHistoricoVendas.text.toString()
        }else if(tabSelecionada == 2){
            mes = autoCompleteTextviewPrimeiroHistoricoVendas.text.toString()
        }
        if (mes == "Jan"){
            dataMes = "01"
        }
        if (mes == "Fev"){
            dataMes = "02"
        }
        if (mes == "Mar"){
            dataMes = "03"
        }
        if (mes == "Abr"){
            dataMes = "04"
        }
        if (mes == "Mai"){
            dataMes = "05"
        }
        if (mes == "Jun"){
            dataMes = "06"
        }
        if (mes == "Jul"){
            dataMes = "07"
        }
        if (mes == "Ago"){
            dataMes = "08"
        }
        if (mes == "Set"){
            dataMes = "09"
        }
        if (mes == "Out"){
            dataMes = "10"
        }
        if (mes == "Nov"){
            dataMes = "11"
        }
        if (mes == "Dez"){
            dataMes = "12"
        }
    }
}