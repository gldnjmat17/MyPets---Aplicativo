package br.uea.transirie.mypay.mypaytemplate2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.*

@Entity(tableName = TABLE_VENDA)
data class Venda(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, index = true) var id:Long,
    @ColumnInfo(name = COLUMN_NOME) var usuario:String = "",
    @ColumnInfo(name = COLUMN_CPF_CLIENTE) var cpfCliente: String = "",
    @ColumnInfo(name = COLUMN_VALOR_INICIAL) var valorInicial:Float = 0f,
    @ColumnInfo(name = COLUMN_TOTAL) var total:Float = 0f
)