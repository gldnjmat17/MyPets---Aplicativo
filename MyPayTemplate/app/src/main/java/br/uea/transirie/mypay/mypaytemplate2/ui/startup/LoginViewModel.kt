package br.uea.transirie.mypay.mypaytemplate2.ui.startup

import android.util.Log
import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.EstabelecimentoRepository
import br.uea.transirie.mypay.mypaytemplate2.repository.room.UsuarioRepository
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_CPF_GERENTE
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.TABLE_ESTABELECIMENTO
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.TABLE_USUARIO
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginViewModel(appDatabase: AppDatabase) {
    private val usuarioRepository = UsuarioRepository(appDatabase)
    private val estabelecimentoRepository = EstabelecimentoRepository(appDatabase)
    private val logtag = "LOGIN_VIEW_MODEL"
    private val db = Firebase.firestore
    private val userRef = db.collection(TABLE_USUARIO)
    private val estabelecimentoRef = db.collection(TABLE_ESTABELECIMENTO)

    fun saveEstabelecimento(dataEstabelecimento: Estabelecimento) =
        estabelecimentoRepository.insert(dataEstabelecimento)

    fun saveUsuario(dataUsuario: Usuario) =
        usuarioRepository.insert(dataUsuario)

    fun usuarioByPin(pin: Int): Usuario {
        val usuario = usuarioRepository.usuarioByPin(pin)

        if (usuario == null)
            Log.i("LOGIN_VIEW_MODEL", "Pin não cadastrado no banco de dados")

        return usuario
    }

    fun estabelecimentoById(id: Long):Estabelecimento{
        return estabelecimentoRepository.estabelecimentoById(id)
    }

    fun estabelecimentoByCPFGeerente(cpfGeremte: String): Estabelecimento?{
        return estabelecimentoRepository.estabelecimentoByCPFGerente(cpfGeremte)
    }

//    fun localizarEstabelecimento(cnpj: String, callback: (Estabelecimento?) -> Unit){
//        Log.d(logtag, "Iniciando procura pelo estabelecimento no firebase...")
//
//        estabelecimentoRef.whereEqualTo(
//            COLUMN_CPF_GERENTE,
//            cnpj).get().addOnSuccessListener { document ->
//            Log.d(logtag, "Procura do estabelecimento no firebase completa.")
//
//            if (document.metadata.isFromCache) {
//                Log.w(logtag, "Dados do cache são descartados pois podem não estar atualizados.")
//            } else {
//                if (document.isEmpty) callback(null)
//                else document.map { estab ->
//                    callback(
//                        Estabelecimento(
//                            id = estab[COLUMN_ID] as Long,
//                            nomeFantasia = estab[COLUMN_NOME_FANTASIA] as String,
//                            cnpj = estab[COLUMN_CNPJ] as String
//                        )
//                    )
//                }
//            }
//        }
//    }
//
//    fun localizaUsuarios(cnpj: String, callback: (List<Usuario>?) -> Unit ){
//        Log.d(logtag, "Iniciando localização dos usuários no firebase...")
//
//        userRef.whereEqualTo(
//            COLUMN_ESTABELECIMENTO_CNPJ, cnpj
//        ).get().addOnSuccessListener { document ->
//            Log.d(logtag, "Localização dos usuários no firebase completa.")
//
//            if (document.metadata.isFromCache) {
//                Log.w(logtag, "Dados do cache são descartados pois podem não estar atualizados.")
//            } else {
//                val listaUsuariosExiste = !document.isEmpty
//
//                val simOuNao = if (listaUsuariosExiste) "Sim" else "Não"
//                Log.d(logtag, "Cadastro existe? $simOuNao")
//
//                if (listaUsuariosExiste) {
//                    val listaUsuarios = mutableListOf<Usuario>()
//
//                    document.forEach { usuario ->
//                        val usu = Usuario(
//                            id = usuario[COLUMN_ID] as Long,
//                            estabelecimentoCNPJ = usuario[COLUMN_ESTABELECIMENTO_CNPJ] as String,
//                            pin = (usuario[COLUMN_PIN] as Long).toInt(),
//                            nome = usuario[COLUMN_NOME] as String,
//                            email = usuario[COLUMN_EMAIL] as String,
//                            telefone = usuario[COLUMN_TELEFONE] as String,
//                            isGerente = usuario[COLUMN_IS_GERENTE] as Boolean
//                        )
//                        listaUsuarios.add(usu)
//                    }
//
//                    callback(listaUsuarios)
//                    Log.d(logtag, callback.toString())
//                } else callback(null)
//            }
//
//        }.addOnFailureListener { exception ->
//            Log.w(logtag, "Erro no firebase: ${exception.message}")
//        }
//    }
}