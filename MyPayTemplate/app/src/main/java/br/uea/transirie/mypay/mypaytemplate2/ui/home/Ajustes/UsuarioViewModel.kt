package br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.UsuarioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsuarioViewModel(appDatabase: AppDatabase) {
    private var usuarioRepository = UsuarioRepository(appDatabase)
    private val _usuarios = MutableLiveData<Usuario>()
    val usuarios: LiveData<Usuario> = _usuarios

    /**
     * Acessa repositório de cliente para atualizar um existente.
     */
    fun atualizarUsuario(usuario: Usuario) {
        usuarioRepository.update(usuario)
    }

    /**
     * Acessa repositório do estabelecimento para deletar um existente.
     */
    fun deletarUsuario(usuario: Usuario) {
        usuarioRepository.delete(usuario)
    }

    fun consultaUsuarioByEmail(email: String) =
        CoroutineScope(Dispatchers.IO).launch {
            val usuario = usuarioRepository.usuarioByEmail(email)
            _usuarios.postValue(usuario)
        }
}