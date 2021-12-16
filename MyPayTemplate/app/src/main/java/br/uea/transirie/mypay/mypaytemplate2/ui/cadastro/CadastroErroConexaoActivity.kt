package br.uea.transirie.mypay.mypaytemplate2.ui.cadastro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityCadastroErroConexaoBinding

class CadastroErroConexaoActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCadastroErroConexaoBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.tvTentarNovamente.setOnClickListener {
            finish()
        }
    }
}