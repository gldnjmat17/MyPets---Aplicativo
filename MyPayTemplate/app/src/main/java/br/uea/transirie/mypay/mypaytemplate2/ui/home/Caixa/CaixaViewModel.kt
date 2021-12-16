package br.uea.transirie.mypay.mypaytemplate2.ui.home.Caixa

import br.uea.transirie.mypay.mypaytemplate2.model.Despesa
import br.uea.transirie.mypay.mypaytemplate2.model.Pagamento
import br.uea.transirie.mypay.mypaytemplate2.model.TipoPagamento
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.DespesaRepository
import br.uea.transirie.mypay.mypaytemplate2.repository.room.PagamentoRepository
import com.google.gson.Gson
import java.time.LocalDate

class CaixaViewModel(appDatabase: AppDatabase) {
    private var despesaRepository = DespesaRepository(appDatabase)
    private var despesaList: List<Despesa> = despesaRepository.getAllDespesa()
    private var pagamentoRepository = PagamentoRepository(appDatabase)
    private var pagamentoList:List<Pagamento> = pagamentoRepository.getAllPagamentos()
    private var pagamentoDinheiro = 0.0
    private var pagamentoCartao = 0.0
    private var pagamentoCredito = 0.0
    private var pagamentoDebito = 0.0
    private var despesaDinheiro = 0.0
    private var despesaCartao = 0.0
    private var despesaCredito = 0.0
    private var despesaDebito = 0.0
    private var pagamentoTotal = 0.0
    private var despesaTotal = 0.0
    private var totalClientes = 0

    fun getRecebidosAno(dataHoje:LocalDate, gerenteCPF:String){
        pagamentoDinheiro = 0.0
        pagamentoCartao = 0.0
        pagamentoCredito = 0.0
        pagamentoDebito = 0.0
        totalClientes = 0
        val lista = pagamentoList.filter { it.gerenteCPF == gerenteCPF }
        lista.forEach { pagamento ->
            val dataPagamento = Gson().fromJson(pagamento.dataPagamento,LocalDate::class.java)
            if (dataPagamento.year == dataHoje.year){
                totalClientes+=1
                pagamentoTotal += pagamento.valor
                when (pagamento.tipoPagamento){
                    TipoPagamento.CREDITO ->{
                        pagamentoCartao += pagamento.valor
                        pagamentoCredito += pagamento.valor
                    }
                    TipoPagamento.DEBITO ->{
                        pagamentoCartao += pagamento.valor
                        pagamentoDebito += pagamento.valor
                    }
                    TipoPagamento.DINHEIRO ->{
                        pagamentoDinheiro += pagamento.valor
                    }
                }
            }
        }
    }
    fun getRecebidosDia(dataHoje:LocalDate, gerenteCPF:String){
        pagamentoDinheiro = 0.0
        pagamentoCartao = 0.0
        pagamentoCredito = 0.0
        pagamentoDebito = 0.0
        totalClientes = 0
        val lista = pagamentoList.filter { it.gerenteCPF == gerenteCPF }
        lista.forEach { pagamento ->
            val dataPagamento = Gson().fromJson(pagamento.dataPagamento,LocalDate::class.java)
            if (dataPagamento.dayOfMonth == dataHoje.dayOfMonth &&
                dataPagamento.month == dataHoje.month &&
                dataPagamento.year == dataHoje.year){
                totalClientes+=1
                pagamentoTotal += pagamento.valor
                when (pagamento.tipoPagamento){
                    TipoPagamento.CREDITO ->{
                        pagamentoCartao += pagamento.valor
                        pagamentoCredito += pagamento.valor
                    }
                    TipoPagamento.DEBITO ->{
                        pagamentoCartao += pagamento.valor
                        pagamentoDebito += pagamento.valor
                    }
                    TipoPagamento.DINHEIRO ->{
                        pagamentoDinheiro += pagamento.valor
                    }
                }
            }
        }
    }
    fun getRecebidosMes(dataHoje:LocalDate, gerenteCPF:String){
        pagamentoDinheiro = 0.0
        pagamentoCartao = 0.0
        pagamentoCredito = 0.0
        pagamentoDebito = 0.0
        totalClientes = 0
        val lista = pagamentoList.filter { it.gerenteCPF == gerenteCPF }
        lista.forEach { pagamento ->
            val dataPagamento = Gson().fromJson(pagamento.dataPagamento,LocalDate::class.java)
            if (dataPagamento.month == dataHoje.month &&
                dataPagamento.year == dataHoje.year){
                totalClientes+=1
                pagamentoTotal += pagamento.valor
                when (pagamento.tipoPagamento){
                    TipoPagamento.CREDITO ->{
                        pagamentoCartao += pagamento.valor
                        pagamentoCredito += pagamento.valor
                    }
                    TipoPagamento.DEBITO ->{
                        pagamentoCartao += pagamento.valor
                        pagamentoDebito += pagamento.valor
                    }
                    TipoPagamento.DINHEIRO ->{
                        pagamentoDinheiro += pagamento.valor
                    }
                }
            }
        }
    }
    fun getDespesasAno(dataHoje:LocalDate, gerenteCPF:String){
        despesaDinheiro = 0.0
        despesaCartao = 0.0
        despesaCredito = 0.0
        despesaDebito = 0.0
        val lista = despesaList.filter { it.gerenteCPF == gerenteCPF }
        lista.forEach { despesa ->
            val dataPagamento = Gson().fromJson(despesa.dataPagamento,LocalDate::class.java)
            if (dataPagamento.year == dataHoje.year){
                despesaTotal += despesa.valor
                when (despesa.tipoPagamento){
                    TipoPagamento.CREDITO ->{
                        despesaCartao += despesa.valor
                        despesaCredito += despesa.valor
                    }
                    TipoPagamento.DEBITO ->{
                        despesaCartao += despesa.valor
                        despesaDebito += despesa.valor
                    }
                    TipoPagamento.DINHEIRO ->{
                        despesaDinheiro += despesa.valor
                    }
                }
            }
        }
    }
    fun getDespesasMes(dataHoje:LocalDate, gerenteCPF:String){
        despesaDinheiro = 0.0
        despesaCartao = 0.0
        despesaCredito = 0.0
        despesaDebito = 0.0
        val lista = despesaList.filter { it.gerenteCPF == gerenteCPF }
        lista.forEach { despesa ->
            val dataPagamento = Gson().fromJson(despesa.dataPagamento,LocalDate::class.java)
            if (dataPagamento.month == dataHoje.month &&
                dataPagamento.year == dataHoje.year){
                despesaTotal += despesa.valor
                when (despesa.tipoPagamento){
                    TipoPagamento.CREDITO ->{
                        despesaCartao += despesa.valor
                        despesaCredito += despesa.valor
                    }
                    TipoPagamento.DEBITO ->{
                        despesaCartao += despesa.valor
                        despesaDebito += despesa.valor
                    }
                    TipoPagamento.DINHEIRO ->{
                        despesaDinheiro += despesa.valor
                    }
                }
            }
        }
    }
    fun getDespesasDia(dataHoje:LocalDate, gerenteCPF:String){
        despesaDinheiro = 0.0
        despesaCartao = 0.0
        despesaCredito = 0.0
        despesaDebito = 0.0
        val lista = despesaList.filter { it.gerenteCPF == gerenteCPF }
        lista.forEach { despesa ->
            val dataPagamento = Gson().fromJson(despesa.dataPagamento,LocalDate::class.java)
            if (dataPagamento.dayOfMonth == dataHoje.dayOfMonth &&
                dataPagamento.month == dataHoje.month &&
                dataPagamento.year == dataHoje.year){
                despesaTotal += despesa.valor
                when (despesa.tipoPagamento){
                    TipoPagamento.CREDITO ->{
                        despesaCartao += despesa.valor
                        despesaCredito += despesa.valor
                    }
                    TipoPagamento.DEBITO ->{
                        despesaCartao += despesa.valor
                        despesaDebito += despesa.valor
                    }
                    TipoPagamento.DINHEIRO ->{
                        despesaDinheiro += despesa.valor
                    }
                }
            }
        }
    }
    fun getClientes() = totalClientes

    fun getPagamentoDinheiro() = pagamentoDinheiro

    fun getPagamentoCartao() = pagamentoCartao

    fun getPagamentoCredito() = pagamentoCredito

    fun getPagamentoDebito() = pagamentoDebito

    fun getDespesaDinheiro() = despesaDinheiro

    fun getDespesaCartao() = despesaCartao

    fun getDespesaCredito() = despesaCredito

    fun getDespesaDebito() = despesaDebito

    fun getPagamentoTotal() = pagamentoTotal

    fun getDespesaTotal() = despesaTotal

    fun getTotal() = pagamentoTotal - despesaTotal
}
