package br.uea.transirie.mypay.mypaytemplate2.repository

import br.uea.transirie.mypay.mypaytemplate2.model.Atendimento

interface AtendimentoDataSource: BaseDataSource<Atendimento> {
    fun atendimentoById(id: Long): Atendimento
    fun atendimentoByClienteId(clienteId: Long): List<Atendimento>
}