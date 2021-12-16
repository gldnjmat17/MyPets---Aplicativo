package br.uea.transirie.mypay.mypaytemplate2.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = TABLE_ESTABELECIMENTO)
data class Estabelecimento (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID) var id: Long = 0,
    @ColumnInfo(name = COLUMN_NOME_FANTASIA) var nomeFantasia: String = "",
    @ColumnInfo(name = COLUMN_EMAIL_GERENTE) var emailGerente: String = "",
    @ColumnInfo(name = COLUMN_CPF_GERENTE) var cpfGerente: String = ""
): Parcelable
