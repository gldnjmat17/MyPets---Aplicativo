package br.uea.transirie.mypay.mypaytemplate2.ui.cadastro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityCadastroPassoUnicoBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskType
import br.uea.transirie.mypay.mypaytemplate2.utils.MyMask
import br.uea.transirie.mypay.mypaytemplate2.utils.MyValidations
import br.uea.transirie.mypay.mypaytemplate2.utils.maskEditText

class CadastroPassoUnicoActivity : AppCompatActivity() {
    private val myContext = this
    private val tag = "CADASTRO"
    private val binding by lazy { ActivityCadastroPassoUnicoBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        title = getString(R.string.title_cadastro)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.cadastroEstabelecimentoBtAvancar.setOnClickListener {
            checkAndAdvance()
        }

        masks()
    }

    /**
     * Nessa função, verificamos os dados de cadastro fornecidos pelo usuário.
     *
     * A validação dos dados é feita por funções do arquivo de validações (pasta utils) que
     * verifica erros comuns no campos de entrada. Caso haja algum erro comum, será exibida
     * uma mensagem abaixo do campo que contém o problema.
     *
     * Se todos os dados forem válidos, seguimos à tela seguinte para gerar um PIN de usuário.
     */
    private fun checkAndAdvance(){
        val myValidations = MyValidations(myContext)
        val isNomeEstabelecimentoOkay =
            !myValidations.inputHasEmptyError(binding.cadastroEstabelecimentoTiNome)
        val isCpfOkay = !myValidations.cpfHasErrors(binding.cadastroEstabelecimentoTiCPF)
        val isEmailOkay = !myValidations.emailHasErrors(
            binding.cadastroEstabelecimentoTiEmail
        )
        val isNomeUsuarioOkay = !myValidations.inputHasEmptyError(
            binding.cadastroEstabelecimentoTiNomeUsuario
        )

        val isSobrenomeUsuarioOkay = !myValidations.inputHasEmptyError(
            binding.cadastroEstabelecimentoTiSobrenomeUsuario
        )

        val isTelefoneOkay = !myValidations.telefoneHasErrors(
            binding.cadastroEstabelecimentoTiTelefone
        )

        if (isCpfOkay && isEmailOkay) {
            Log.d(tag, "O cnpj é válido.")
            Log.d(tag, "O email é válido.")

            if (isNomeEstabelecimentoOkay && isNomeUsuarioOkay && isTelefoneOkay && isSobrenomeUsuarioOkay) {
                Log.d(tag, "Todos os dados são válidos.")

                val nomeEstabelecimento = binding.cadastroEstabelecimentoEtNome.text.toString()
                val cpfGerente = binding.cadastroEstabelecimentoEtCPF.text.toString()
                val emailGerente = binding.cadastroEstabelecimentoEtEmail.text.toString().trim()
                val estabelecimento = Estabelecimento(nomeFantasia = nomeEstabelecimento,
                    emailGerente = emailGerente,
                    cpfGerente = cpfGerente)

                val email = binding.cadastroEstabelecimentoEtEmail.text.toString().trim()
                val nome = binding.cadastroEstabelecimentoEtNomeUsuario.text.toString()
                val sobrenome = binding.cadastroEstabelecimentoEtSobrenomeUsuario.text.toString()
                val nomeUsuarioCompleto = "$nome $sobrenome"
                val telefone = binding.cadastroEstabelecimentoEtTelefone.text.toString()
                val usuario = Usuario(
                    email = email,
                    nome = nomeUsuarioCompleto,
                    telefone = telefone,
                    isGerente = true,
                    cpfGerente = cpfGerente
                )

                val intent = Intent(myContext, GerarPinActivity::class.java)
                    .putExtra(getString(R.string.EXTRA_USUARIO), usuario)
                    .putExtra(getString(R.string.EXTRA_ESTABELECIMENTO), estabelecimento)
                    .putExtra(getString(R.string.EXTRA_TOPBAR_TITLE), getString(R.string.title_cadastro))
                startActivity(intent)
            }

        }
    }

    /** Função que adiciona máscara aos campos de CPF e de Telefone. */
    private fun masks(){
        maskEditText(getString(R.string.MASK_CPF), binding.cadastroEstabelecimentoEtCPF)
        binding.cadastroEstabelecimentoEtTelefone.let {
            it.addTextChangedListener(MyMask(MaskType.Telephone, it))
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}