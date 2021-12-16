package br.uea.transirie.mypay.mypaytemplate2.ui.home.Estoque

import br.uea.transirie.mypay.mypaytemplate2.model.Estoque
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.EstoqueRepository

class ManterEstoqueViewModel(appDatabase: AppDatabase) {
    private var estoqueRepository = EstoqueRepository(appDatabase)
    private var estoqueList: List<Estoque>

    init {
        estoqueList = estoqueRepository.getAllEstoque()
    }
    fun getAllEstoque():List<Estoque>{
        return estoqueRepository.getAllEstoque()
    }
    fun estoqueByCodBarras(codBarras:String):Estoque{
        return estoqueRepository.estoqueByCodBarras(codBarras)
    }
    fun updateEstoque(estoque:Estoque){
        return estoqueRepository.update(estoque)
    }
    fun deletarEstoque(estoque: Estoque){
        return estoqueRepository.delete(estoque)
    }
}