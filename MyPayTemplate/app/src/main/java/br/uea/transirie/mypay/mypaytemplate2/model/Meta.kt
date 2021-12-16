package br.uea.transirie.mypay.mypaytemplate2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.*

@Entity(tableName = TABLE_META)
data class Meta(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, index = true) var id:Long,
    @ColumnInfo(name = COLUMN_GERENTE_CPF) var gerenteCPF:String = "",
    @ColumnInfo(name = COLUMN_NOME) var nome:String = "",
    @ColumnInfo(name = COLUMN_DATA) var data:String = "",
    @ColumnInfo(name = COLUMN_VALOR) var valor:Float = 0f,
    @ColumnInfo(name = COLUMN_PROGRESSO) var progresso:Float = 0f
)
