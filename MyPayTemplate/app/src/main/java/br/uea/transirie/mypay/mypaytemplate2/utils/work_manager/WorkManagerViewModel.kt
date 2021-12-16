package br.uea.transirie.mypay.mypaytemplate.utils.work_manager

import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.EstabelecimentoRepository
import br.uea.transirie.mypay.mypaytemplate2.repository.room.UsuarioRepository

class WorkManagerViewModel(appDatabase: AppDatabase) {
    val estabelecimentoRepository = EstabelecimentoRepository(appDatabase)
    private val usuarioRepository = UsuarioRepository(appDatabase)

    fun saveEstabelecimento(estabelecimento: Estabelecimento) {
        return estabelecimentoRepository.save(estabelecimento)
    }

    fun estabelecimentoByCPFGerente(cnpj: String): Estabelecimento? {
        return estabelecimentoRepository.estabelecimentoByCPFGerente(cnpj)
    }

    fun getUsuarios() = usuarioRepository.getAllUsuario()
}