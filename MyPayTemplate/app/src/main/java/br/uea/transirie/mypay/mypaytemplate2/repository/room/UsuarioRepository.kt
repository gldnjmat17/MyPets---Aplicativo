package br.uea.transirie.mypay.mypaytemplate2.repository.room

import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.UsuarioDataSource

class UsuarioRepository(database: AppDatabase): UsuarioDataSource {
    private val usuarioDao = database.usuarioDao()

    //Função que recebe o valor do id do estabelecimento
    override fun usuarioId(): Long = usuarioDao.usuarioIds()

//    override fun usuarioByEmailESenha(email: String, senha: String): Usuario? =
//        usuarioDao.usuarioByEmailESenha(email, senha)
//
//    override fun usuarioByNomeESenha(nomeUsuario: String, senha: String): Usuario? {
//        return usuarioDao.usuarioByNomeESenha(nomeUsuario, senha)
//    }

    override fun usuarioByPin(pin: Int): Usuario {
        return usuarioDao.usuarioByPin(pin)
    }
    override fun pinJaEmUso(pinUsuario: Int): Boolean {
        return usuarioDao.pinJaEmUso(pinUsuario)
    }
    override fun usuarioByEmail(email: String): Usuario {
        return usuarioDao.usuarioByEmail(email)
    }

    override fun emailJaEmuso(email: String): Boolean =
        usuarioDao.emailJaEmUso(email)

//    override fun cpfJaEmUso(cpf: String): Boolean =
//        usuarioDao.cpfJaEmUso(cpf)

    override fun getEstabelecimento(): Usuario {
        return usuarioDao.getUsuario()
    }

    override fun getAllUsuario(): List<Usuario> {
        return usuarioDao.getAllUsuario()
    }

    override fun usuarioEstaRegistrado(usuario: String): Usuario {
        return usuarioDao.getUsuario(usuario)
    }

    override fun save(obj: Usuario) {
        if(obj.id == 0L){
            val id = insert(obj)
            obj.id = id
        } else{
            update(obj)
        }
    }

    override fun insert(obj: Usuario): Long {
        return usuarioDao.insert(obj)
    }

    override fun update(obj: Usuario) {
        return usuarioDao.update(obj)
    }

    override fun delete(obj: Usuario) {
        return usuarioDao.delete(obj)
    }
}