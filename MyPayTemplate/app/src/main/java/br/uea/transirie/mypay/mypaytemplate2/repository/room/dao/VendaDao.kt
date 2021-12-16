package br.uea.transirie.mypay.mypaytemplate2.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import br.uea.transirie.mypay.mypaytemplate2.model.Venda
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_ID
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.TABLE_VENDA

@Dao
interface VendaDao:BaseDao<Venda> {
    @Query("""SELECT * FROM $TABLE_VENDA""")
    fun getAllVenda():List<Venda>

    @Query("""SELECT * FROM $TABLE_VENDA WHERE $COLUMN_ID = :idVenda""")
    fun vendaByID(idVenda:Long):Venda
}