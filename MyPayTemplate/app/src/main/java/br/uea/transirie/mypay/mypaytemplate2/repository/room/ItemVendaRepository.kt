package br.uea.transirie.mypay.mypaytemplate2.repository.room

import br.uea.transirie.mypay.mypaytemplate2.model.ItemVenda
import br.uea.transirie.mypay.mypaytemplate2.repository.ItemVendaDataSource

class ItemVendaRepository(database: AppDatabase):ItemVendaDataSource {
    private val itemVendaDao = database.itemVendaDao()

    override fun getAllItemVenda(): List<ItemVenda> {
        return itemVendaDao.getAllItemVenda()
    }

    override fun itemVendaByID(idItemVenda: Long): ItemVenda {
        return itemVendaDao.itemVendaByID(idItemVenda)
    }

    override fun itemVendaByCodBarras(codBarras: String): ItemVenda {
        return itemVendaDao.itemVendaByCodBarras(codBarras)
    }
    override fun save(obj: ItemVenda) {
        if (obj.id == 0L) {
            val id = insert(obj)
            obj.id = id
        } else {
            update(obj)
        }
    }

    override fun insert(obj: ItemVenda): Long {
        return  itemVendaDao.insert(obj)
    }

    override fun update(obj: ItemVenda) {
        return itemVendaDao.update(obj)
    }

    override fun delete(obj: ItemVenda) {
        return itemVendaDao.delete(obj)
    }
}