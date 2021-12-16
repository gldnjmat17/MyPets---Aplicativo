package br.uea.transirie.mypay.mypaytemplate2.repository

import br.uea.transirie.mypay.mypaytemplate2.model.Pagamento
import br.uea.transirie.mypay.mypaytemplate2.repository.BaseDataSource

interface PagamentoDataSource: BaseDataSource<Pagamento> {
    fun getAllPagamentos(): List<Pagamento>
}