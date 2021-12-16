package br.uea.transirie.mypay.mypaytemplate2.ui.home.HistoricoVendas

import br.uea.transirie.mypay.mypaytemplate2.model.Pagamento
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.PagamentoRepository

class ManterPagamentoViewModel(appDatabase: AppDatabase) {
    private var pagamentoRepository = PagamentoRepository(appDatabase)
    private var pagamentoList:List<Pagamento>

    init {
        pagamentoList = pagamentoRepository.getAllPagamentos()
    }
    fun getAllPagamento():List<Pagamento>{
        return pagamentoRepository.getAllPagamentos()
    }
}