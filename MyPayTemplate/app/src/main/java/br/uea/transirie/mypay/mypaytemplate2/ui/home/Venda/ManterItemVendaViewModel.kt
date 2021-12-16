package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import br.uea.transirie.mypay.mypaytemplate2.model.ItemVenda
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.ItemVendaRepository

class ManterItemVendaViewModel(appDatabase: AppDatabase) {
    private var itemVendaRepository = ItemVendaRepository(appDatabase)
    private var itemVendaList:List<ItemVenda>
    init {
        itemVendaList = itemVendaRepository.getAllItemVenda()
    }
    fun getAllItemVenda():List<ItemVenda>{
        return itemVendaRepository.getAllItemVenda()
    }
    fun itemVendaByID(idItemVenda:Long):ItemVenda{
        return itemVendaRepository.itemVendaByID(idItemVenda)
    }
    fun updateItemVenda(itemVenda:ItemVenda){
        return itemVendaRepository.update(itemVenda)
    }
    fun deletarItemVenda(itemVenda:ItemVenda){
        return itemVendaRepository.delete(itemVenda)
    }
    fun itemVendaByCodBarras(codBarras:String):ItemVenda{
        return itemVendaRepository.itemVendaByCodBarras(codBarras)
    }
}