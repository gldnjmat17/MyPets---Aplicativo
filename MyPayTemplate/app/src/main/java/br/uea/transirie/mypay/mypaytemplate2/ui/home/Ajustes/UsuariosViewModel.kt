package br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes

import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.UsuarioRepository

class UsuariosViewModel(appDatabase: AppDatabase) {
    private var usuarioRepository = UsuarioRepository(appDatabase)

    fun getAllUsuario():List<Usuario> = usuarioRepository.getAllUsuario()
}