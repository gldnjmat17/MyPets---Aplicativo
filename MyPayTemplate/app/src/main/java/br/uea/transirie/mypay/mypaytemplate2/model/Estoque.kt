package br.uea.transirie.mypay.mypaytemplate2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.*

@Entity(tableName = TABLE_ESTOQUE)
data class Estoque(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, index = true) var id:Long,
    @ColumnInfo(name = COLUMN_GERENTE_CPF) var gerenteCPF:String = "",
    @ColumnInfo(name = COLUMN_DESCRICAO) var descricao:String = "",
    @ColumnInfo(name = COLUMN_MARCA) var marca:String = "",
    @ColumnInfo(name = COLUMN_VALOR_INICIAL) var valorInicial:Float = 0f,
    @ColumnInfo(name = COLUMN_VALOR_VENDA) var valorVenda:Float = 0f,
    @ColumnInfo(name = COLUMN_COD_BARRAS) var codigoBarras:String = "",
    @ColumnInfo(name = COLUMN_QNTD) var quantidade:Int = 0)
