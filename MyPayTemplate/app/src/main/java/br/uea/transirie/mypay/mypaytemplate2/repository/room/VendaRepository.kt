package br.uea.transirie.mypay.mypaytemplate2.repository.room

import br.uea.transirie.mypay.mypaytemplate2.model.Venda
import br.uea.transirie.mypay.mypaytemplate2.repository.VendaDataSource

class VendaRepository(database: AppDatabase):VendaDataSource {
    private val vendaDao = database.vendaDao()

    override fun getAllVenda(): List<Venda> {
        return vendaDao.getAllVenda()
    }

    override fun vendaByID(idVenda: Long): Venda {
        return vendaDao.vendaByID(idVenda)
    }

    override fun save(obj: Venda) {
        if (obj.id == 0L) {
            val id = insert(obj)
            obj.id = id
        } else {
            update(obj)
        }
    }

    override fun insert(obj: Venda): Long {
        return vendaDao.insert(obj)
    }

    override fun update(obj: Venda) {
        return vendaDao.update(obj)
    }

    override fun delete(obj: Venda) {
        return vendaDao.delete(obj)
    }
}