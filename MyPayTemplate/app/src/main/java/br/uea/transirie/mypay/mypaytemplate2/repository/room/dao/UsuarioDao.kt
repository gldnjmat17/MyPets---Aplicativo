package br.uea.transirie.mypay.mypaytemplate2.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.dao.BaseDao
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.*

@Dao
interface UsuarioDao: BaseDao<Usuario> {

    @Query("""SELECT * FROM $TABLE_USUARIO WHERE $COLUMN_NOME = :nome""")
    fun getUsuario(nome: String): Usuario

    @Query("""SELECT * FROM $TABLE_USUARIO WHERE $COLUMN_ID = :id""")
    fun usuarioById(id: Long): Usuario

    @Query("""SELECT * FROM $TABLE_USUARIO""")
    fun getAllUsuario():List<Usuario>

    @Query("SELECT * FROM $TABLE_USUARIO WHERE $COLUMN_PIN = :pin")
    fun usuarioByPin(pin: Int): Usuario

    /**
     * Realiza consulta por meio do email do estabelecimento
     */

//    /**
//     * Consulta o valor por meio da coluna a senha do estabelecimento
//     */
//    @Query("""SELECT * FROM $TABLE_USUARIO WHERE $COLUMN_SENHA_OBFUSCATED = :senha""")
//    fun usuarioBySenha(senha: String): Usuario

//    /**
//     * Recupera um Estabelecendo através do usuario e senha
//     */
//    @Query("""SELECT * FROM $TABLE_USUARIO WHERE $COLUMN_NOME = :nome AND $COLUMN_SENHA_OBFUSCATED = :senha""")
//    fun usuarioByNomeESenha(nome: String, senha: String): Usuario?

    /**
     * Recupera o id de um estabelecimento através de seu email
     */
    @Query("""SELECT * FROM $TABLE_USUARIO WHERE $COLUMN_EMAIL = :email""")
    fun usuarioByEmail(email: String): Usuario

//    /**
//     * Recupera um Estabelecendo através do email e senha
//     */
//    @Query("""SELECT * FROM $TABLE_USUARIO WHERE $COLUMN_EMAIL = :email AND $COLUMN_SENHA_OBFUSCATED = :senha""")
//    fun usuarioByEmailESenha(email: String, senha: String): Usuario?

    @Query("""SELECT * FROM $TABLE_USUARIO""")
    fun getUsuario(): Usuario

    //Faz a consulta do id na tabela
    @Query("SELECT _id FROM $TABLE_USUARIO LIMIT 1")
    fun usuarioIds(): Long

    /**
     * Retorna true caso haja um usuário com o email informado
     * @param email o email informado
     */
    @Query("SELECT 1 FROM $TABLE_USUARIO WHERE $COLUMN_EMAIL = :email")
    fun emailJaEmUso(email: String): Boolean

//    /**
//     * Retorna true caso haja um usuário cadastrado com o cpf informado
//     * @param cpf o cnpj informado
//     */
//    @Query("SELECT 1 FROM $TABLE_USUARIO WHERE $COLUMN_CPF = :cpf")
//    fun cpfJaEmUso(cpf: String): Boolean

    @Query("""SELECT 1 FROM $TABLE_USUARIO WHERE $COLUMN_PIN = :pinUsuario""")
    fun pinJaEmUso(pinUsuario: Int):Boolean
}