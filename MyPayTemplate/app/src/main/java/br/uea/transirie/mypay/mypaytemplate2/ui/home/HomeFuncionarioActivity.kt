package br.uea.transirie.mypay.mypaytemplate2.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityHomeFuncionarioBinding
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes.AjustesFuncionarioActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente.ClientesFuncActivity
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda.SelecaoProdutosActivity

class HomeFuncionarioActivity : AppCompatActivity() {
    private val myContext = this@HomeFuncionarioActivity
    private val binding by lazy { ActivityHomeFuncionarioBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        title = getString(R.string.app_name)

        listeners()
    }
    private fun listeners(){
        binding.homeFBtnAjustes.setOnClickListener {
            startActivity(Intent(myContext, AjustesFuncionarioActivity::class.java))
        }
        binding.btVenda.setOnClickListener {
            startActivity(Intent(myContext, SelecaoProdutosActivity::class.java))
        }
        binding.homeFBtnClientes.setOnClickListener {
            startActivity(Intent(myContext, ClientesFuncActivity::class.java))
        }
    }
}