package br.uea.transirie.mypay.mypaytemplate2.repository

import br.uea.transirie.mypay.mypaytemplate2.model.ItemAtendimento
import br.uea.transirie.mypay.mypaytemplate2.repository.BaseDataSource

interface ItemAtendimentoDataSource: BaseDataSource<ItemAtendimento> {
    fun itemAtendimentoById(id: Long): ItemAtendimento
    fun itemAtendimentoByAtendimentoId(atendimentoId: Long): List<ItemAtendimento>
    fun itemAtendimentoByServicoId(servicoId: Long): List<ItemAtendimento>
}