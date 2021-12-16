package br.uea.transirie.mypay.mypaytemplate2.repository

import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento


interface EstabelecimentoDataSource: BaseDataSource<Estabelecimento> {
    fun estabelecimentoId(): Long
    fun estabelecimentoById(id: Long): Estabelecimento
    fun estabelecimentoByEmailGerente(emailGerente: String): Estabelecimento?
    fun getEstabelecimentos(): List<Estabelecimento>
    fun estabelecimentoByCPFGerente(cpfGerente: String): Estabelecimento?
}