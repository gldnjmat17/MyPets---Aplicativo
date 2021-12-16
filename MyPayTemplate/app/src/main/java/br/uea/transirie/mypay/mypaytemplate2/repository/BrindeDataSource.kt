package br.uea.transirie.mypay.mypaytemplate2.repository

import br.uea.transirie.mypay.mypaytemplate2.model.Brinde

interface BrindeDataSource:BaseDataSource<Brinde> {
    fun getAllBrinde():List<Brinde>
    fun brindeByID(idBrinde:Long):Brinde
}