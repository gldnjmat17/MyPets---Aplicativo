package br.uea.transirie.mypay.mypaytemplate2.ui.home.Meta

import br.uea.transirie.mypay.mypaytemplate2.model.Meta
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.MetaRepository

class ManterMetaViewModel(appDatabase: AppDatabase) {
    private var metaRepository = MetaRepository(appDatabase)
    private var metaList: List<Meta>

    init {
        metaList = metaRepository.getAllMeta()
    }
    fun getAllMeta():List<Meta>{
        return metaRepository.getAllMeta()
    }
    fun metaByID(idMeta:Long):Meta{
        return metaRepository.metaByID(idMeta)
    }
    fun updateMeta(meta:Meta){
        return metaRepository.update(meta)
    }
    fun deletarMeta(meta: Meta){
        return metaRepository.delete(meta)
    }
}