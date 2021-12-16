package br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente

import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import br.uea.transirie.mypay.mypaytemplate2.model.Servico
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.ClienteRepository
import br.uea.transirie.mypay.mypaytemplate2.repository.room.ServicoRepository

class ManterClientesViewModel(appDatabase: AppDatabase) {
    private var clienteRepository = ClienteRepository(appDatabase)
    private var clienteList: List<Cliente>

    init {
        clienteList  = clienteRepository.getAllClientes()
    }
    fun getAllClientes():List<Cliente>{
        return clienteRepository.getAllClientes()
    }
    fun clienteByCPF(cpf:String):Cliente{
        return clienteRepository.clienteByCPF(cpf)
    }
    fun updateCliente(cliente: Cliente){
        return clienteRepository.update(cliente)
    }
    fun deletarCliente(cliente:Cliente){
        return clienteRepository.delete(cliente)
    }

}