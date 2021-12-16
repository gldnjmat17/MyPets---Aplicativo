package br.uea.transirie.mypay.mypaytemplate2.repository

import br.uea.transirie.mypay.mypaytemplate2.model.ItemVenda

interface ItemVendaDataSource:BaseDataSource<ItemVenda> {
    fun getAllItemVenda():List<ItemVenda>
    fun itemVendaByID(idItemVenda:Long):ItemVenda
    fun itemVendaByCodBarras(codBarras:String):ItemVenda
}