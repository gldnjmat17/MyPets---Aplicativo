package br.uea.transirie.mypay.mypaytemplate2.repository

import br.uea.transirie.mypay.mypaytemplate2.model.Despesa
import br.uea.transirie.mypay.mypaytemplate2.repository.room.dao.BaseDao

interface DespesaDataSource: BaseDataSource<Despesa> {
    fun getAllDespesa(): List<Despesa>
}