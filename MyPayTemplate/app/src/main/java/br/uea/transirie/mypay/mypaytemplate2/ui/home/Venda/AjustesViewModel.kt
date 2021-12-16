package br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.UsuarioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AjustesViewModel(appDatabase: AppDatabase) {
    private val _usuarios = MutableLiveData<Usuario>()
    val usuarios: LiveData<Usuario> = _usuarios

    private var usuarioRespository = UsuarioRepository(appDatabase)

    fun consultaUsuarioByEmail(email: String) =
        CoroutineScope(Dispatchers.IO).launch {
            val usuario = usuarioRespository.usuarioByEmail(email)
            _usuarios.postValue(usuario)
        }

}
