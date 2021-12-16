package br.uea.transirie.mypay.mypaytemplate2.repository

import androidx.room.Query
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario

interface UsuarioDataSource: BaseDataSource<Usuario> {
    fun usuarioEstaRegistrado(usuario: String): Usuario
    fun getEstabelecimento(): Usuario
    fun usuarioId(): Long
//    fun usuarioByEmailESenha(email: String, senha: String): Usuario?
//    fun usuarioByNomeESenha(usuario: String, senha: String): Usuario?
    fun getAllUsuario():List<Usuario>
    fun usuarioByEmail(email: String): Usuario?
    fun pinJaEmUso(pinUsuario: Int): Boolean
    fun emailJaEmuso(email: String): Boolean
//    fun cpfJaEmUso(cpf: String): Boolean
    fun usuarioByPin(pin: Int): Usuario
}