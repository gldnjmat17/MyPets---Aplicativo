package br.uea.transirie.mypay.mypaytemplate2.repository

import br.uea.transirie.mypay.mypaytemplate2.model.Venda

interface VendaDataSource:BaseDataSource<Venda> {
    fun getAllVenda():List<Venda>
    fun vendaByID(idVenda:Long):Venda
}