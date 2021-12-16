package br.uea.transirie.mypay.mypaytemplate2.model

import androidx.room.*
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.*

@Entity(tableName = TABLE_ITEM_ATENDIMENTO,
    foreignKeys = [
        ForeignKey(entity = Servico::class,
            parentColumns = [COLUMN_ID],
            childColumns = [COLUMN_SERVICO_ID]),
        ForeignKey(entity = Atendimento::class,
            parentColumns = [COLUMN_ID],
            childColumns = [COLUMN_ATENDIMENTO_ID])
    ])
class ItemAtendimento(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID) var id: Long = 0,
    @ColumnInfo(name = COLUMN_SERVICO_ID) var servicoId: Long = 0,
    @ColumnInfo(name = COLUMN_ATENDIMENTO_ID) var atendimentoId: Long = 0,
)
