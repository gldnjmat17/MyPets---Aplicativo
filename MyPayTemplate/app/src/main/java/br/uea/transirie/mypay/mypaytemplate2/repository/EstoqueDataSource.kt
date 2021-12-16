package br.uea.transirie.mypay.mypaytemplate2.repository

import br.uea.transirie.mypay.mypaytemplate2.model.Estoque
import br.uea.transirie.mypay.mypaytemplate2.repository.BaseDataSource

interface EstoqueDataSource:BaseDataSource<Estoque> {
    fun getAllEstoque():List<Estoque>
    fun estoqueByCodBarras(codBarras:String):Estoque
}