package br.uea.transirie.mypay.mypaytemplate2.repository.room.converters

import androidx.room.TypeConverter
import br.uea.transirie.mypay.mypaytemplate2.model.TipoPagamento

class TipoPagamentoConverter {
    @TypeConverter
    fun fromTipoPagamento(tipoPagamento: TipoPagamento): String {
        return tipoPagamento.name
    }

    @TypeConverter
    fun toTipoPagamento(strTipoPagamento: String): TipoPagamento {
        return TipoPagamento.valueOf(strTipoPagamento)
    }
}