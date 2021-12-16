package br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityEstabelecimentoBinding
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.ui.startup.SplashActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DadosCadastradosGerenteActivity : AppCompatActivity() {
    private var usuarioPrimeiroGerente: Usuario? = null
    private lateinit var viewModel: EstabelecimentoUsuarioViewModel
    private val context = this
    private var usuario: Usuario? = null
    private var estab: Estabelecimento? = null
    private var ultimoUpload = ""
    private var pin = 0
    private var cpfGerente = ""

    private val binding by lazy { ActivityEstabelecimentoBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ultimoUpload = AppPreferences.getUltimoUpload(context)
        pin = AppPreferences.getPIN(context)
        cpfGerente = AppPreferences.getCPFGerente(context)

        setAppTopBarEvents()

        doAsync {
            viewModel = EstabelecimentoUsuarioViewModel(AppDatabase.getDatabase(context))
            usuario = viewModel.usuarioByPin(pin)
            estab = viewModel.estabelecimentoByCPFGerente(cpfGerente)
            usuarioPrimeiroGerente = viewModel.usuarioByEmail(estab!!.emailGerente)

            uiThread {
                binding.txtCpfGerente.text = cpfGerente

                estab?.let {
                    with(it) {
                        binding.txtEmpresaColaborador.text = nomeFantasia
                        binding.txtNomeEstab.text = nomeFantasia
                        binding.txtCpfGerente.text = cpfGerente
                    }
                }

                usuario?.let {
                    with(it) {
                        binding.txtEmailColaborador.text = email
                        val (primeiroNome, sobrenome) = nome.split(" ", limit = 2)
                        binding.txtNomeColaborador.text = primeiroNome
                        binding.txtSobrenomeColaborador.text = sobrenome
                        binding.txtTelefoneColaborador.text = telefone
                    }
                }

                val strNuvem = "Atualizado na nuvem em: $ultimoUpload"
                binding.txtNuvemAtt.text = strNuvem
            }
        }

        binding.btDeletarColaborador.setOnClickListener {
            val builder = AlertDialog.Builder(context)

            val msg = if (usuario == usuarioPrimeiroGerente)
                getString(R.string.dialog_deletar_txt2)
            else getString(R.string.dialog_deletar_txt)

            builder.setMessage(msg)
            builder.setPositiveButton("DELETAR") { _, _ ->
                doAsync {
                    viewModel.deleteUsuario(usuario!!)
                    if (usuario == usuarioPrimeiroGerente) {
                        viewModel.deleteEstabelecimento(estab!!)
                        viewModel.deleteAllUsuarios()
                        AppPreferences.clearPreferences(context)
                    }
                    else AppPreferences.setUserLogado(false, context)

                    uiThread {
                        finishAffinity()

                        startActivity(Intent(context, SplashActivity::class.java))
                        Toast.makeText(context, "Conta deletada com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton("CANCELAR"){dialog,_-> dialog.cancel()}
            builder.show()
        }
    }
    private fun setAppTopBarEvents(){
        binding.listaColabTopAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.listaColabTopAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.editar_dados ->{
                    startActivity(Intent(context, EditarEstabelecimentoActivity::class.java)
                        .putExtra(getString(R.string.EXTRA_USUARIO), usuario)
                        .putExtra(getString(R.string.EXTRA_ESTABELECIMENTO), estab))
                    true
                }
                else -> false
            }
        }
    }
}