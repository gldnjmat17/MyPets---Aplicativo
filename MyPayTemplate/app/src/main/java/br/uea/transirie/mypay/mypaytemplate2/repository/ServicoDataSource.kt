package br.uea.transirie.mypay.mypaytemplate2.repository

import br.uea.transirie.mypay.mypaytemplate2.model.Servico
import br.uea.transirie.mypay.mypaytemplate2.repository.BaseDataSource

interface ServicoDataSource: BaseDataSource<Servico> {
    fun servicoById(id: Long): Servico
    fun searchByDescricao(termoBusca: String): List<Servico>
}