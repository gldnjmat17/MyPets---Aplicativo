package br.uea.transirie.mypay.mypaytemplate2.ui.home.Caixa

import java.time.LocalDate

data class CaixaStatus(var status:Boolean, var data:LocalDate)
// false = caixa aberto, true = caixa fechado