package br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes

import android.util.Log
import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.EstabelecimentoRepository
import br.uea.transirie.mypay.mypaytemplate2.repository.room.UsuarioRepository

class EstabelecimentoUsuarioViewModel(appDatabase: AppDatabase) {
    private var estabelecimentoRepository = EstabelecimentoRepository(appDatabase)
    private val usuarioRepository = UsuarioRepository(appDatabase)
    private val usuarios = getUsuarios()

    fun getUsuarios(): List<Usuario>{
//        CoroutineScope(Dispatchers.IO).launch {
//            val usuarios = usuarioRepository.getAllUsuario()
//            _usuarios.postValue(usuarios)
//        }
        return usuarioRepository.getAllUsuario()
    }

    fun estabelecimentoByCPFGerente(cpfGerente: String): Estabelecimento? =
        estabelecimentoRepository.estabelecimentoByCPFGerente(cpfGerente)

    fun usuarioByPin(pin: Int): Usuario {
        val usuario = usuarioRepository.usuarioByPin(pin)

        if (usuario == null)
            Log.i("LOGIN_VIEW_MODEL", "Pin não cadastrado no banco de dados")

        return usuario
    }
    fun usuarioByEmail(email: String): Usuario = usuarioRepository.usuarioByEmail(email)

    fun deleteUsuario(usuario: Usuario) = usuarioRepository.delete(usuario)

    fun getNumGerentes(): Int = usuarios.filter { it.isGerente }.size

    fun deleteEstabelecimento(estab: Estabelecimento) = estabelecimentoRepository.delete(estab)

    fun deleteAllUsuarios() {
        usuarios.forEach {
            deleteUsuario(it)
        }
    }
}