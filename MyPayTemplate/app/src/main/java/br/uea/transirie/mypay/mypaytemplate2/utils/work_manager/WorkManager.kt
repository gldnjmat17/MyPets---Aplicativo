package br.uea.transirie.mypay.mypaytemplate2.utils.work_manager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import br.uea.transirie.mypay.mypaytemplate.utils.work_manager.WorkManagerViewModel
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.*
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UpdateWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
) : Worker(appContext, workerParams) {
    private val logtag = "WORK_MANAGER"
    private lateinit var viewModel: WorkManagerViewModel
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val userRef = db.collection(TABLE_USUARIO)
    private val estabelecimentoRef = db.collection(TABLE_ESTABELECIMENTO)
    private var cpfGerente = ""
    private var estab = Estabelecimento()
    private var listaUsu = listOf<Usuario>()

    /**
     * A função principal da classe.
     *
     * De início, recuperamos o CPF, os dados do estabelecimento e os dados de usuário.
     * Em seguida, realizamos o backup na nuvem e, no fim, gravamos a data e hora do upload.
     */
    override fun doWork(): Result {
        Log.d(logtag, "BACKUP")

        cpfGerente = AppPreferences.getCPFGerente(appContext)
        val desconhecido = appContext.getString(R.string.cpf_desconhecido)

        if (cpfGerente != desconhecido) {
            doAsync {
                viewModel = WorkManagerViewModel(AppDatabase.getDatabase(appContext))
                val estabelecimento = viewModel.estabelecimentoByCPFGerente(cpfGerente)
                val listaUsuarios = viewModel.getUsuarios()

                uiThread {
                    if (estabelecimento != null) {
                        estab = estabelecimento
                        listaUsu = listaUsuarios

                        atualizaNuvem { taskCompleted ->
                            if (taskCompleted)
                                atualizaUltimoUpload()
                        }
                    }
                    else Log.d(logtag, appContext.getString(R.string.erro_no_estabelecimento_cadastrado))
                }
            }
        }

        return Result.success()
    }

    /** Aqui é onde chamamos as funções que atualizam o banco de dados da nuvem. **/
    private fun atualizaNuvem(callback: (Boolean) -> Unit) {
        verificaUsuarios { usuariosAtualizados ->
            if (usuariosAtualizados)
                verificaEstabelecimento { estabAtualizados ->
                    if (estabAtualizados)
                        callback(true)
                }
        }
    }

    /**
     * Nesta função, procuramos no banco de dados da nuvem pelos usuários do estabelecimento logado.
     * Caso encontremos, verificamos quais usuários foram apagados localmente e os deletamos da nuvem.
     * Em seguida, verificamos quais usuários cadastrados localmente ainda não estão na nuvem e
     * então os cadastramos lá.
     */
    private fun verificaUsuarios(callback: (Boolean) -> Unit) {
        Log.d(logtag, "Iniciando localização dos usuários no firebase...")

        userRef.whereEqualTo(
            COLUMN_CPF_GERENTE, cpfGerente
        ).get().addOnSuccessListener { document ->
            Log.d(logtag, "Localização dos usuários no firebase completa.")

            if (document.metadata.isFromCache) {
                Log.w(logtag, appContext.getString(R.string.erro_dados_cache))
                callback(false)
            } else {
                val listaUsuariosExiste = !document.isEmpty
                val listaUsuariosFirebase = mutableListOf<Usuario>()

                val simOuNao = if (listaUsuariosExiste) "Sim" else "Não"
                Log.d(logtag, "Cadastro existe? $simOuNao")

                if (document.isEmpty) {
                    adicionaNovosUsuarios(listaUsuariosFirebase)
                } else {
                    /** transforma document do firebase em uma lista de usuários **/
                    document.forEach { usuario ->
                        val usu = Usuario(
                            id = usuario[COLUMN_ID] as Long,
                            cpfGerente = usuario[COLUMN_CPF_GERENTE] as String,
                            pin = (usuario[COLUMN_PIN] as Long).toInt(),
                            nome = usuario[COLUMN_NOME] as String,
                            email = usuario[COLUMN_EMAIL] as String,
                            telefone = usuario[COLUMN_TELEFONE] as String,
                            isGerente = usuario[COLUMN_IS_GERENTE] as Boolean
                        )
                        listaUsuariosFirebase.add(usu)
                    }

                    val usuariosARemover = listaUsuariosFirebase - listaUsu
                    var nARemover = usuariosARemover.size
                    log("Usuários a remover: $nARemover")

                    if (usuariosARemover.isEmpty()) {
                        adicionaNovosUsuarios(listaUsuariosFirebase)
                    } else {
                        log("Usuários a remover: ${usuariosARemover.map { it.email }}")

                        usuariosARemover.forEach {
                            userRef.document(it.email).delete()

                            var logmsg = "O usuário (${it.email}) não existe mais localmente. " +
                                    "É preciso deletá-lo no Firebase auth. " +
                                    "Iniciando autenticação."
                            log(logmsg)

                            /** É necessário reautenticar o usuário antes de deletá-lo. **/
                            auth.signInWithEmailAndPassword(it.email, it.pin.toString())
                                .addOnCompleteListener { signIn ->
                                    if (signIn.isSuccessful) {
                                        logmsg = "Autenticação concluída: " +
                                                "${auth.currentUser?.email}. " +
                                                "Deletando usuário."
                                        log(logmsg)

                                        auth.currentUser!!.delete().addOnCompleteListener { deletar ->
                                            if (deletar.isSuccessful) {
                                                log("Usuário deletado do Firebase Auth.")
                                            } else {
                                                logmsg = "Houve um erro ao deletar " +
                                                        "o usuário no Firebase Auth."
                                                log(logmsg)
                                            }

                                            nARemover -= 1
                                            if (nARemover == 0)
                                                adicionaNovosUsuarios(listaUsuariosFirebase)
                                        }
                                    } else {
                                        logmsg = "Erro ao reautenticar o usuário " +
                                                "${it.email}: ${signIn.exception?.message}"
                                        log(logmsg)
                                    }
                                }
                        }
                    }
                }

                listaUsu.forEach {
                    if (!listaUsuariosFirebase.contains(it)) {
                        val data = hashMapOf(
                            COLUMN_ID to it.id,
                            COLUMN_CPF_GERENTE to it.cpfGerente,
                            COLUMN_NOME to it.nome,
                            COLUMN_EMAIL to it.email,
                            COLUMN_TELEFONE to it.telefone,
                            COLUMN_PIN to it.pin,
                            COLUMN_IS_GERENTE to it.isGerente
                        )
                        userRef.document(it.email).set(data)
                    }
                }

                callback(true)
            }
        }
    }

    /** Este método apenas adiciona os novos usuários locais ao Firebase. **/
    private fun adicionaNovosUsuarios(listaUsuariosFirebase: List<Usuario>) {
        val novosUsuarios = listaUsu - listaUsuariosFirebase
        log("Novos usuários: ${novosUsuarios.size}")

        novosUsuarios.forEach {
            val data = hashMapOf(
                COLUMN_ID to it.id,
                COLUMN_CPF_GERENTE to it.cpfGerente,
                COLUMN_NOME to it.nome,
                COLUMN_EMAIL to it.email,
                COLUMN_TELEFONE to it.telefone,
                COLUMN_PIN to it.pin,
                COLUMN_IS_GERENTE to it.isGerente
            )
            userRef.document(it.email).set(data)

            auth.createUserWithEmailAndPassword(it.email, it.pin.toString())
        }
    }

    /**
     * Nesta função, procuramos pelo estabelecimento logado através do CNPJ no banco de dados da
     * nuvem. Se encontramos, verificamos se algum dado do estabelecimento está desatualizado e,
     * caso esteja, o atualizamos. Se não encontramos, realizamos seu cadastro na nuvem.
     */
    private fun verificaEstabelecimento(callback: (Boolean) -> Unit) {
        Log.d(logtag, "Iniciando procura pelo estabelecimento no firebase...")

        estabelecimentoRef.whereEqualTo(
            COLUMN_CPF_GERENTE, cpfGerente
        ).get().addOnSuccessListener { document ->
            Log.d(logtag, "Procura pelo estabelecimento no firebase completa.")

            if (document.metadata.isFromCache) {
                Log.w(logtag, appContext.getString(R.string.erro_dados_cache))
                callback(false)
            } else {
                if (document.isEmpty) {
                    Log.d(logtag, "Na nuvem, não há um estabelecimento cadastrado com esse CPF.")

                    val dataEstabelecimento = hashMapOf(
                        COLUMN_ID to estab.id,
                        COLUMN_NOME_FANTASIA to estab.nomeFantasia,
                        COLUMN_EMAIL_GERENTE to estab.emailGerente,
                        COLUMN_CPF_GERENTE to estab.cpfGerente,
                    )

                    estabelecimentoRef.document(
                        estab.cpfGerente.filter { it.isDigit() }
                    ).set(dataEstabelecimento)
                } else {
                    Log.d(logtag, "Na nuvem, já há um estabelecimento cadastrado com esse CPF.")
                    var estabFirebase = Estabelecimento()

                    document.map { estab ->
                        estabFirebase = Estabelecimento(
                            id = estab[COLUMN_ID] as Long,
                            nomeFantasia = estab[COLUMN_NOME_FANTASIA] as String,
                            emailGerente = estab[COLUMN_EMAIL_GERENTE] as String,
                            cpfGerente = estab[COLUMN_CPF_GERENTE] as String,
                        )
                    }

                    if (estabFirebase != estab) {
                        estabelecimentoRef.document(
                            estabFirebase.cpfGerente.filter { it.isDigit() }
                        ).delete()

                        val dataEstabelecimento = hashMapOf(
                            COLUMN_ID to estab.id,
                            COLUMN_NOME_FANTASIA to estab.nomeFantasia,
                            COLUMN_EMAIL_GERENTE to estab.emailGerente,
                            COLUMN_CPF_GERENTE to estab.cpfGerente,
                        )

                        estabelecimentoRef.document(
                            estab.cpfGerente.filter { it.isDigit() }
                        ).set(dataEstabelecimento)
                    }
                }

                callback(true)
            }
        }
    }

    /**
     * Nesta função, gravamos, no registro local do estabelecimento, a data e hora do upload realizado.
     */
    private fun atualizaUltimoUpload() {
        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy',' HH:mm")
        val dataEHora = LocalDateTime.now().format(dtf)
        Log.d(logtag, "Data e hora: $dataEHora")

        AppPreferences.setUltimoUpload(dataEHora, appContext)
    }

    private fun log(msg: String = "") = Log.d(logtag, msg)
}