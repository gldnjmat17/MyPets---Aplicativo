package br.uea.transirie.mypay.mypaytemplate2.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import br.uea.transirie.mypay.mypaytemplate2.model.Meta
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_ID
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.TABLE_META

@Dao
interface MetaDao:BaseDao<Meta> {
    @Query("""SELECT * FROM $TABLE_META""")
    fun getAllMeta():List<Meta>
    @Query("""SELECT * FROM $TABLE_META WHERE $COLUMN_ID = :idMeta""")
    fun metaByID(idMeta:Long):Meta
}