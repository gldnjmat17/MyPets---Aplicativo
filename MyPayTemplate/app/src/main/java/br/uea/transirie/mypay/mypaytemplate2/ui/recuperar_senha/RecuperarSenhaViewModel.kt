package br.uea.transirie.mypay.mypaytemplate2.ui.recuperar_senha

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.UsuarioRepository

class RecuperarSenhaViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository = UsuarioRepository(AppDatabase.getDatabase(application))

    fun estabelecimentoByEmail(email: String): Usuario? {
        return usuarioRepository.usuarioByEmail(email)
    }

    fun saveEstabelecimento(usuario: Usuario) {
        usuarioRepository.save(usuario)
    }
}