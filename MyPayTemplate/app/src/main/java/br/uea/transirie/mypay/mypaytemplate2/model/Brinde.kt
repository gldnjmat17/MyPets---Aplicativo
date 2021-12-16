package br.uea.transirie.mypay.mypaytemplate2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.*

@Entity(tableName = TABLE_BRINDE)
data class Brinde(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, index = true) var id:Long,
    @ColumnInfo(name = COLUMN_GERENTE_CPF) var gerenteCPF:String = "",
    @ColumnInfo(name = COLUMN_NOME) var nome:String = "",
    @ColumnInfo(name = COLUMN_QNTD) var quantidade:Int = 0,
    @ColumnInfo(name = COLUMN_PONTOS) var pontos:Int = 0
)
