package br.uea.transirie.mypay.mypaytemplate2.repository.room

import br.uea.transirie.mypay.mypaytemplate2.model.Meta
import br.uea.transirie.mypay.mypaytemplate2.repository.MetaDataSource

class MetaRepository(database: AppDatabase):MetaDataSource {
    private val metaDao = database.metaDao()

    override fun getAllMeta(): List<Meta> {
        return metaDao.getAllMeta()
    }

    override fun metaByID(idMeta: Long): Meta {
        return metaDao.metaByID(idMeta)
    }

    override fun save(obj: Meta) {
        if (obj.id == 0L) {
            val id = insert(obj)
            obj.id = id
        } else {
            update(obj)
        }
    }

    override fun insert(obj: Meta): Long {
        return metaDao.insert(obj)
    }

    override fun update(obj: Meta) {
        return metaDao.update(obj)
    }

    override fun delete(obj: Meta) {
        return metaDao.delete(obj)
    }
}