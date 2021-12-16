package br.uea.transirie.mypay.mypaytemplate2.repository

import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
//import br.uea.transirie.mypay.mypaytemplate2.model.ClienteComAtendimentos
import br.uea.transirie.mypay.mypaytemplate2.model.ClienteMinimal
import br.uea.transirie.mypay.mypaytemplate2.repository.BaseDataSource

interface ClienteDataSource: BaseDataSource<Cliente> {
    fun clienteById(id: Long): Cliente
    fun getClientesMinimal(): List<ClienteMinimal>
    fun getAllClientes(): List<Cliente>
    fun clienteByCPF(cpf:String):Cliente
//    fun getClienteComAtendimentosById(id: Long): List<ClienteComAtendimentos>
}