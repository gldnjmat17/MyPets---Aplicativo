package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import br.uea.transirie.mypay.mypaytemplate2.model.TipoPagamento
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Caixa.SplashPrimeiroUsoActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente.ManterClientesViewModel
import br.uea.transirie.mypay.mypaytemplate2.ui.home.HistoricoVendas.ManterPagamentoViewModel
import br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.printer.ModulePrinter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_pagamento_realizado.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class PagamentoRealizadoActivity : AppCompatActivity() {
    var bitmap:Bitmap? = null
    private var df = DecimalFormat("0.00")
    private lateinit var cliente:Cliente
    private var parcelaInt = 0
    private var dinheiroRecebido = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagamento_realizado)
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        parcelaInt = preference.getString("parcelas","0").toString().toInt()
        dinheiroRecebido = preference.getString("recebido","0").toString().toFloat()
        btImprimirComprovante.setOnClickListener {
            doAsync {
                val viewModel = ManterPagamentoViewModel(AppDatabase.getDatabase(this@PagamentoRealizadoActivity))
                val lista = viewModel.getAllPagamento()
                val pagamento = lista[lista.size-1]
                val clienteCpf = pagamento.cpfCliente
                var clienteNome = ""
                var clientePontos = ""
                if (clienteCpf.isEmpty()){
                    clienteNome = "Não informado"
                    clientePontos = "---"
                }else{
                    val viewModelCliente = ManterClientesViewModel(AppDatabase.getDatabase(this@PagamentoRealizadoActivity))
                    cliente = viewModelCliente.clienteByCPF(clienteCpf)
                    clienteNome = cliente.nome
                    if (cliente.sistema){
                        clientePontos = cliente.pontos.toString()
                    }else{
                        clientePontos = "Não participante"
                    }
                }
                uiThread {
                    val dataPagamento = Gson().fromJson(pagamento.dataPagamento,LocalDate::class.java).format(
                        DateTimeFormatter.ofPattern("dd/MM/yy"))
                    val horaPagamento = Gson().fromJson(pagamento.horaPagamento,LocalTime::class.java).format(
                        DateTimeFormatter.ofPattern("HH:mm"))
                    val valor = "R$ ${df.format(pagamento.valor).replace(".",",")}"
                    Log.d("tipoPagamento",pagamento.tipoPagamento.name)
                    Log.d("total",valor)
                    Log.d("data",dataPagamento)
                    Log.d("hora",horaPagamento)
                    Log.d("cliente",clienteNome)
                    Log.d("pontos",clientePontos)
                    val str = "     VIA DO CLIENTE\n     TRANSIRE\n\n     CLIENTE: $clienteNome\n     PONTOS DO CLIENTE: $clientePontos\n     TIPO DE PAGAMENTO: ${pagamento.tipoPagamento.name}\n     TOTAL: ${valor}\n     DATA: ${dataPagamento}\n     HORA: ${horaPagamento}\n\n\n\n\n\n\n"
                    if (pagamento.tipoPagamento == TipoPagamento.CREDITO){
                        val valorParcelado = pagamento.valor/parcelaInt
                        val parcelas = "${parcelaInt}x de R$ ${df.format(valorParcelado).replace(".",",")}"
                        Log.d("parcelas",parcelas)
                        val str2 = "     VIA DO CLIENTE\n     TRANSIRE\n\n     CLIENTE: $clienteNome\n     PONTOS DO CLIENTE: $clientePontos\n     TIPO DE PAGAMENTO: ${pagamento.tipoPagamento.name}\n     TOTAL: ${valor}\n     PARCELAS: $parcelas\n     DATA: ${dataPagamento}\n     HORA: ${horaPagamento}\n\n\n\n\n\n"
                        ModulePrinter(this@PagamentoRealizadoActivity).printText(str2)
                    }else if (pagamento.tipoPagamento == TipoPagamento.DINHEIRO){
                        val troco = "R$ ${df.format(dinheiroRecebido - pagamento.valor).replace(".",",")}"
                        val recebido = "R$ ${df.format(dinheiroRecebido).replace(".",",")}"
                        Log.d("recebido",recebido)
                        Log.d("troco",troco)
                        val str3 = "     VIA DO CLIENTE\n     TRANSIRE\n\n     CLIENTE: $clienteNome\n     PONTOS DO CLIENTE: $clientePontos\n     TIPO DE PAGAMENTO: ${pagamento.tipoPagamento.name}\n     TOTAL: ${valor}\n     VALOR RECEBIDO: $recebido\n     TROCO: $troco     DATA: ${dataPagamento}\n     HORA: ${horaPagamento}\n\n\n\n\n\n"
                        ModulePrinter(this@PagamentoRealizadoActivity).printText(str3)
                    } else{
                        ModulePrinter(this@PagamentoRealizadoActivity).printText(str)
                    }
                    /*
                    bitmap?.let { it1 -> ModulePrinter(this@PagamentoRealizadoActivity).printResumo(str, it1) }
                     */
                }
            }
        }
        btConcluirPag.setOnClickListener {
            startActivity(Intent(this,SplashPrimeiroUsoActivity::class.java))
            finish()
        }
    }
}