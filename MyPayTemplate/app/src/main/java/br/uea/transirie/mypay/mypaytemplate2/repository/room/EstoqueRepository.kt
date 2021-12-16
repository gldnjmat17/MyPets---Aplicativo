package br.uea.transirie.mypay.mypaytemplate2.repository.room

import br.uea.transirie.mypay.mypaytemplate2.model.Estoque
import br.uea.transirie.mypay.mypaytemplate2.repository.EstoqueDataSource

class EstoqueRepository(database: AppDatabase):EstoqueDataSource {
    private val estoqueDao = database.estoqueDao()

    override fun getAllEstoque(): List<Estoque> {
        return estoqueDao.getAllEstoque()
    }

    override fun estoqueByCodBarras(codBarras: String): Estoque {
        return estoqueDao.estoqueByCodBarras(codBarras)
    }

    override fun save(obj: Estoque) {
        if (obj.id == 0L) {
            val id = insert(obj)
            obj.id = id
        } else {
            update(obj)
        }
    }

    override fun insert(obj: Estoque): Long {
        return estoqueDao.insert(obj)
    }

    override fun update(obj: Estoque) {
        return estoqueDao.update(obj)
    }

    override fun delete(obj: Estoque) {
        return estoqueDao.delete(obj)
    }
}