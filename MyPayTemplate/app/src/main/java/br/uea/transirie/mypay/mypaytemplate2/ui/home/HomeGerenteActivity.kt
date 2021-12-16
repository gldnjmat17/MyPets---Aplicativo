package br.uea.transirie.mypay.mypaytemplate2.ui.home

import android.R.attr.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityHomeBinding
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Caixa.CaixaStatus
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.ui.startup.StartupActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import java.time.LocalDate


/**
 * Activity responsável por manter o bottom navigation e fazer a troca de fragmentos para cada
 * seleção do usuário
 */
class HomeGerenteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var gerenteCPF:String
    private lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.colorPrimaryVariant
        )

        val dataHoje = LocalDate.now()
        // resgata usuário que está logado
        preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        gerenteCPF = AppPreferences.getCPFGerente(this)
        val edit = preference.edit()
        val statusCaixa = Gson().fromJson(preference.getString(gerenteCPF, Gson().toJson(CaixaStatus(status = false, data = LocalDate.now()))),
            CaixaStatus::class.java)
        Log.i("statusCaixa", statusCaixa.status.toString())
        if (statusCaixa.data.dayOfMonth == dataHoje.dayOfMonth &&
            statusCaixa.data.monthValue == dataHoje.monthValue &&
            statusCaixa.data.year == dataHoje.year &&
            statusCaixa.status){
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@HomeGerenteActivity)
            builder.setMessage("Você já encerrou suas atividades do dia. Tem certeza que quer continuar?")
            builder.setPositiveButton("SIM"){dialog,_->
                statusCaixa.status = false
                edit.putString(gerenteCPF, Gson().toJson(statusCaixa))
                edit.apply()
                dialog.dismiss()
            }
            builder.setNegativeButton("CANCELAR"){dialog,_->
                finishAffinity()
                startActivity(Intent(this@HomeGerenteActivity, StartupActivity::class.java))
                val prefLogado = getSharedPreferences(getString(R.string.PREF_USER_DATA), Context.MODE_PRIVATE)
                val editLogado = prefLogado.edit()
                editLogado.putBoolean(getString(R.string.PREF_USER_LOGADO), false)
                editLogado.apply()
                dialog.dismiss()
            }
            builder.show()
        }

        val verificaMeta = preference.getString("verificaMeta", "")
        if (verificaMeta != ""){
            val snackbar =
                Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Parabéns, você concluiu sua meta atual!", 4000)
                    .setAction("VER"){
                        loadFragment(MetaFragment(), FRAGMENT_META)
                        bottomNavigation.selectedItemId = R.id.ic_meta
                    }.setActionTextColor(Color.parseColor("#5BD897"))
            val snackBarView: View = snackbar.view

            val params = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )

            snackBarView.setLayoutParams(params)
            snackbar.show()

            val sharedEditor = preference.edit()
            sharedEditor.putString("verificaMeta", "")
            sharedEditor.apply()
        }
        // carrega o fragmento principal, inicial, "HOME"
        loadFragment(HomeFragment(), FRAGMENT_HOME)

        // configura ações de clique em cada uma das opções do bottom navigation
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.ic_home -> {
                    loadFragment(HomeFragment(), FRAGMENT_HOME)
                    true
                }
                R.id.ic_caixa -> {
                    loadFragment(ClientesFragment(), FRAGMENT_CLIENTES)
                    true
                }
                R.id.ic_meta -> {
                    loadFragment(MetaFragment(), FRAGMENT_META)
                    true
                }
                R.id.ic_ajustes -> {
                    loadFragment(AjustesFragment(), FRAGMENT_AJUSTES)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Função criada para deixar código mais enxuto e centralizar o carregamento de fragmentos
     */
    private fun loadFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment, tag)
            .commit()
    }

    /**
     * "tags" dos fragmentos
     */
    companion object {
        private const val FRAGMENT_HOME = "FRAGMENT_HOME"
        private const val FRAGMENT_CLIENTES = "FRAGMENT_CLIENTES"
        private const val FRAGMENT_META = "FRAGMENT_META"
        private const val FRAGMENT_AJUSTES = "FRAGMENT_AJUSTES"
    }
}

