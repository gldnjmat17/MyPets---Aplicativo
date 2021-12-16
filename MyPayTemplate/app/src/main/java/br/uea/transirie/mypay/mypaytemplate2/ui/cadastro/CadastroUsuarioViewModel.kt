package br.uea.transirie.mypay.mypaytemplate2.ui.cadastro

import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.UsuarioRepository

class CadastroUsuarioViewModel(appDatabase: AppDatabase) {
    private val usuarioRepository = UsuarioRepository(appDatabase)

    fun save(dataUsuario: Usuario) {
        usuarioRepository.insert(dataUsuario)
    }

    fun emailJaEmuso(email: String): Boolean =
        usuarioRepository.emailJaEmuso(email)

//    fun cpfJaEmUso(cnpj: String): Boolean =
//        usuarioRepository.cpfJaEmUso(cnpj)

}