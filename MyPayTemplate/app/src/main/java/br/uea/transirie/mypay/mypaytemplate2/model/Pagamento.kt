package br.uea.transirie.mypay.mypaytemplate2.model

import androidx.room.*
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.*
import java.time.LocalDate

@Entity(tableName = TABLE_PAGAMENTO)
class Pagamento (@PrimaryKey(autoGenerate = true)
                 @ColumnInfo(name = COLUMN_ID) var id: Long = 0,
                 @ColumnInfo(name = COLUMN_GERENTE_CPF) var gerenteCPF:String = "",
                 @ColumnInfo(name = COLUMN_CPF_CLIENTE) var cpfCliente: String = "",
                 @ColumnInfo(name = COLUMN_VENDA) var venda: String? = "",
                 @ColumnInfo(name = COLUMN_VALOR) var valor: Float = 0f,
                 @ColumnInfo(name = COLUMN_DATA_PAGAMENTO) var dataPagamento: String?,
                 @ColumnInfo(name = COLUMN_HORA_PAGAMENTO) var horaPagamento: String?,
                 @ColumnInfo(name = COLUMN_TIPO_PAGAMENTO) var tipoPagamento: TipoPagamento
){
    object dataHoje{
        var dataHoje: LocalDate = LocalDate.now()
    }
    object caixaInicial{
        var valorCaixaInicial: Float =0f
    }
    object totalClientes{
        var totalCliente: Int = 0 // variável que armazena o total de clientes do dia
    }
    object dinheiroRecebimento{
        var valorDinheiro: Float = 0f
    }
    object creditoRecebimento{
        var valorCredito: Float = 0f
    }
    object debitoRecebimento{
        var valorDebito: Float = 0f
    }
    object despesasCartao{
        var valorDespesasCartaoCredito: Float = 0f
        var valorDespesasCartaoDebito: Float = 0f
    }
    object despesasDinheiro{
        var valorDespesasDinheiroDia: Float = 0f
    }
}