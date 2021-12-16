package br.uea.transirie.mypay.mypaytemplate2.repository.room

import br.uea.transirie.mypay.mypaytemplate2.model.Brinde
import br.uea.transirie.mypay.mypaytemplate2.repository.BrindeDataSource

class BrindeRepository(database: AppDatabase):BrindeDataSource {
    private val brindeDao = database.brindeDao()

    override fun getAllBrinde(): List<Brinde> {
        return brindeDao.getAllBrinde()
    }

    override fun brindeByID(idBrinde: Long): Brinde {
        return brindeDao.brindeByID(idBrinde)
    }

    override fun save(obj: Brinde) {
        if (obj.id == 0L) {
            val id = insert(obj)
            obj.id = id
        } else {
            update(obj)
        }
    }

    override fun insert(obj: Brinde): Long {
        return  brindeDao.insert(obj)
    }

    override fun update(obj: Brinde) {
        return brindeDao.update(obj)
    }

    override fun delete(obj: Brinde) {
        return brindeDao.delete(obj)
    }
}