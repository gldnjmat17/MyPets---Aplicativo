package br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityAlterarPinBinding
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.ui.cadastro.GerarPinActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.MyValidations
import org.jetbrains.anko.doAsync

class AlterarPinActivity : AppCompatActivity() {
    private val context = this@AlterarPinActivity
    private lateinit var viewModel: EstabelecimentoUsuarioViewModel
    private val binding by lazy { ActivityAlterarPinBinding.inflate(layoutInflater) }
    private lateinit var userPrefs: SharedPreferences
    private var pinUsuario: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.title_alterar_pin)

        userPrefs = getSharedPreferences(getString(R.string.PREF_USER_DATA), Context.MODE_PRIVATE)
        pinUsuario = userPrefs.getInt(getString(R.string.PREF_PIN), 0)

        binding.alterarPinBtAvancar.setOnClickListener {
            checkAndAdvance()
        }
    }
    private fun checkAndAdvance(){
        val myValidations = MyValidations(context)
        val isPinAtualCorrect = !myValidations.pinHasErrors(binding.alterarPinTiPin)
        val isPinAtualConfCorrect = !myValidations.confPinHasErrors(
            binding.alterarPinTiConfirmePin, binding.alterarPinTiPin
        )

        if (isPinAtualConfCorrect && isPinAtualCorrect ){
            if (binding.alterarPinEtPin.text.toString().toInt()*100 == pinUsuario){
                doAsync {
                    viewModel = EstabelecimentoUsuarioViewModel(AppDatabase.getDatabase(context))
                    val usuario = viewModel.usuarioByPin(pinUsuario!!)
                    val intent = Intent(context, GerarPinActivity::class.java)
                        .putExtra(getString(R.string.EXTRA_USUARIO), usuario)
                        .putExtra(getString(R.string.EXTRA_TOPBAR_TITLE), getString(R.string.title_alterar_pin))
                    startActivity(intent)
                }
            }else{
                binding.alterarPinTiPin.error = "PIN incorreto"
                binding.alterarPinTiConfirmePin.error = "PIN incorreto"
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}