package br.uea.transirie.mypay.mypaytemplate2.repository

import br.uea.transirie.mypay.mypaytemplate2.model.Meta

interface MetaDataSource:BaseDataSource<Meta> {
    fun getAllMeta():List<Meta>
    fun metaByID(idMeta:Long):Meta
}