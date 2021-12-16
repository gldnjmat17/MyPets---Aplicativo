package br.uea.transirie.mypay.mypaytemplate2.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import br.uea.transirie.mypay.mypaytemplate2.model.Brinde
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_ID
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_NOME
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.TABLE_BRINDE

@Dao
interface BrindeDao:BaseDao<Brinde> {
    @Query("""SELECT * FROM $TABLE_BRINDE""")
    fun getAllBrinde():List<Brinde>

    @Query("""SELECT * FROM $TABLE_BRINDE WHERE $COLUMN_ID = :idBrinde""")
    fun brindeByID(idBrinde:Long):Brinde
}