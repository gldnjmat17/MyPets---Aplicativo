package br.uea.transirie.mypay.mypaytemplate2.ui.home.Brinde

import br.uea.transirie.mypay.mypaytemplate2.model.Brinde
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.BrindeRepository

class ManterBrindeViewModel(appDatabase: AppDatabase) {
    private var brindeRepository = BrindeRepository(appDatabase)
    private var brindeList: List<Brinde>

    init {
        brindeList = brindeRepository.getAllBrinde()
    }
    fun getAllBrinde():List<Brinde>{
        return brindeRepository.getAllBrinde()
    }
    fun brindeByID(idBrinde:Long):Brinde{
        return brindeRepository.brindeByID(idBrinde)
    }
    fun updateBrinde(brinde: Brinde){
        return brindeRepository.update(brinde)
    }
    fun deletarBrinde(brinde: Brinde){
        return brindeRepository.delete(brinde)
    }
}