package br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes

import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.UsuarioRepository

class AtualizarSenhaViewModel(appDatabase: AppDatabase) {
    private var usuarioRespository = UsuarioRepository(appDatabase)

    fun recuperaTodosEstabelecimentos(): List<Usuario> {
        return usuarioRespository.getAllUsuario()
    }

    fun atualizaSenha(objUsuario: Usuario) {
        return usuarioRespository.update(objUsuario)
    }

    fun usuarioByEmail(email: String): Usuario {
        return usuarioRespository.usuarioByEmail(email)
    }
}