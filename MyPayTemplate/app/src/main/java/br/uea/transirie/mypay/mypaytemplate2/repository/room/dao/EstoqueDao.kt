package br.uea.transirie.mypay.mypaytemplate2.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import br.uea.transirie.mypay.mypaytemplate2.model.Estoque
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_COD_BARRAS
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_DESCRICAO
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_VALOR_INICIAL
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.TABLE_ESTOQUE

@Dao
interface EstoqueDao: BaseDao<Estoque>{
    @Query("""SELECT * FROM $TABLE_ESTOQUE""")
    fun getAllEstoque():List<Estoque>

    @Query("""SELECT * FROM $TABLE_ESTOQUE WHERE $COLUMN_COD_BARRAS = :codBarras""")
    fun estoqueByCodBarras(codBarras:String):Estoque
}