package br.uea.transirie.mypay.mypaytemplate2.ui.cadastro

import android.util.Log
import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.EstabelecimentoRepository
import br.uea.transirie.mypay.mypaytemplate2.repository.room.UsuarioRepository
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_EMAIL
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_NOME_FANTASIA
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.TABLE_ESTABELECIMENTO
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.TABLE_USUARIO
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CadastroEstabelecimentoEUsuarioViewModel(appDatabase: AppDatabase){
    private val estabelecimentoRepository = EstabelecimentoRepository(appDatabase)
    private val usuarioRepository = UsuarioRepository(appDatabase)
    private val db = Firebase.firestore
    private val estabelecimentoRef = db.collection(TABLE_ESTABELECIMENTO)
    private val userRef = db.collection(TABLE_USUARIO)
    private val logtag = "CADASTRO_VIEW_MODEL"

    fun saveEstabelecimento(dataEstabelecimento: Estabelecimento) =
        estabelecimentoRepository.insert(dataEstabelecimento)

    fun deleteEstabelecimento(estabelecimento: Estabelecimento) =
        estabelecimentoRepository.delete(estabelecimento)

    fun updateUsuario(dataUsuario: Usuario) = usuarioRepository.update(dataUsuario)

    fun updateEstab(dataEstab:Estabelecimento) = estabelecimentoRepository.update(dataEstab)

    fun saveUsuario(dataUsuario:Usuario) =
        usuarioRepository.insert(dataUsuario)

    fun emailJaEmUsoLocalmente(email: String): Boolean =
        usuarioRepository.emailJaEmuso(email)

//    fun cnpjJaEmUsoFirebase(cnpj: String, callback: (Boolean) -> Unit) {
//        Log.d(logtag, "Iniciando verificação do cnpj no firebase...")
////        Log.d(logtag, "CNPJ a procurar: $cnpj")
//
//        estabelecimentoRef.whereEqualTo(
//            COLUMN_CNPJ, cnpj
//        ).get().addOnSuccessListener { document ->
//            Log.d(logtag, "Verificação do cnpj no firebase completa.")
//
//            if (document.metadata.isFromCache) {
//                Log.w(logtag, "Dados do cache são descartados pois podem não estar atualizados.")
//            } else {
//                val cnpjJaEmUso = !document.isEmpty
//
//                val simOuNao = if (cnpjJaEmUso) "Sim" else "Não"
//                Log.d(logtag, "CNPJ já está em uso? $simOuNao")
//
//                callback(cnpjJaEmUso)
//            }
//
//        }.addOnFailureListener { exception ->
//            Log.w(logtag, "Erro no firebase: ${exception.message}")
//        }
//    }

    fun emailJaEmUsoFirebase(email: String, callback: (Boolean) -> Unit) {
        Log.d(logtag, "Iniciando verificação do email no firebase...")
//        Log.d(logtag, "Email a procurar: $email")

        userRef.whereEqualTo(
            COLUMN_EMAIL, email
        ).get().addOnSuccessListener { document ->
            Log.d(logtag, "Verificação do email no firebase completa.")

            if (document.metadata.isFromCache) {
                Log.w(logtag, "Dados do cache são descartados pois podem não estar atualizados.")
            } else {
                val emailJaEmUso = !document.isEmpty

                val simOuNao = if (emailJaEmUso) "Sim" else "Não"
                Log.d(logtag, "Email já está em uso? $simOuNao")

                callback(emailJaEmUso)
            }

        }.addOnFailureListener { exception ->
            Log.w(logtag, "Erro no firebase: ${exception.message}")
        }
    }

    fun nomeJaEmUsoFirebase(nome: String, callback: (Boolean) -> Unit) {
        Log.d(logtag, "Iniciando verificação do nome no firebase...")
//        Log.d(logtag, "Nome a procurar: nome")

        estabelecimentoRef.whereEqualTo(
            COLUMN_NOME_FANTASIA, nome
        ).get().addOnSuccessListener { document ->
            Log.d(logtag, "Verificação do nome no firebase completa.")

            if (document.metadata.isFromCache) {
                Log.w(logtag, "Dados do cache são descartados pois podem não estar atualizados.")
            } else {
                val nomeJaEmUso = !document.isEmpty

                val simOuNao = if (nomeJaEmUso) "Sim" else "Não"
                Log.d(logtag, "Nome já está em uso? $simOuNao")

                callback(nomeJaEmUso)
            }

        }.addOnFailureListener { exception ->
            Log.w(logtag, "Erro no firebase: ${exception.message}")
        }

    }
}
