package br.uea.transirie.mypay.mypaytemplate2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.*

@Entity(tableName = TABLE_CLIENTE)
data class Cliente (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID, index = true) var id: Long = 0,
    @ColumnInfo(name = COLUMN_GERENTE_CPF) var gerenteCPF:String = "",
    @ColumnInfo(name = COLUMN_NOME) var nome: String = "",
    @ColumnInfo(name = COLUMN_CPF) var cpf: String = "",
    @ColumnInfo(name = COLUMN_TELEFONE) var telefone: String = "",
    @ColumnInfo(name = COLUMN_ENDERECO) var endereco: String = "",
    @ColumnInfo(name = COLUMN_PONTOS) var pontos:Int? = 0,
    @ColumnInfo(name = COLUMN_IS_CHECKED) var sistema:Boolean = false,
    @ColumnInfo(name = COLUMN_ESCOLHIDO) var escolhido:Boolean = false
)