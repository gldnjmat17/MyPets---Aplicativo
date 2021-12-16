package br.uea.transirie.mypay.mypaytemplate2.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import br.uea.transirie.mypay.mypaytemplate2.model.ItemVenda
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_COD_BARRAS
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_ID
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.TABLE_ITEM_VENDA

@Dao
interface ItemVendaDao:BaseDao<ItemVenda> {
    @Query("""SELECT * FROM $TABLE_ITEM_VENDA""")
    fun getAllItemVenda():List<ItemVenda>

    @Query("""SELECT * FROM $TABLE_ITEM_VENDA WHERE $COLUMN_ID = :idItemVenda""")
    fun itemVendaByID(idItemVenda:Long):ItemVenda

    @Query("""SELECT * FROM $TABLE_ITEM_VENDA WHERE $COLUMN_COD_BARRAS = :codBarras""")
    fun itemVendaByCodBarras(codBarras:String):ItemVenda
}