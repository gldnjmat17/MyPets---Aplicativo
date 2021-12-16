package br.uea.transirie.mypay.mypaytemplate2.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.databinding.FragmentAjustesBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Usuario
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Ajustes.*
import br.uea.transirie.mypay.mypaytemplate2.ui.home.Venda.AjustesViewModel
import br.uea.transirie.mypay.mypaytemplate2.ui.startup.SplashActivity
import br.uea.transirie.mypay.mypaytemplate2.utils.AppPreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AjustesFragment : Fragment() {
    private lateinit var viewModel: EstabelecimentoUsuarioViewModel
    private var _binding: FragmentAjustesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAjustesBinding.inflate(inflater, container, false)
        val myContext = requireContext()
        val cnpjEstabelecimento = AppPreferences.getCPFGerente(myContext)
        val pinUsuario = AppPreferences.getPIN(myContext)

        doAsync {
            viewModel = EstabelecimentoUsuarioViewModel(AppDatabase.getDatabase(myContext))
            val estab = viewModel.estabelecimentoByCPFGerente(cnpjEstabelecimento)
            val usuario = viewModel.usuarioByPin(pinUsuario)

            uiThread {
                binding.tvNomeEstabelecimentoAjustes.text = estab?.nomeFantasia
                binding.tvNomeGerente.text = usuario.nome

                binding.cvDadosCadastradosAjustes.setOnClickListener {
                    val intent = Intent(activity, DadosCadastradosGerenteActivity::class.java)
                        .putExtra(getString(R.string.EXTRA_USUARIO), usuario)
                        .putExtra(getString(R.string.EXTRA_ESTABELECIMENTO), estab)
                    startActivity(intent)
                }

                binding.cvColbaoradoresAjustes.setOnClickListener {
                    startActivity(Intent(activity, ColaboradoresActivity::class.java))
                }
            }
        }

        binding.cvPinAjustes.setOnClickListener {
            startActivity(Intent(activity, AlterarPinActivity::class.java))
        }

        binding.cvSobreAppAjustes.setOnClickListener {
            startActivity(Intent(activity, SobreAplicativoActivity::class.java))
        }

        // Função para efetuar o logout
        binding.cvEncerrarSessaoAjustes.setOnClickListener {
            container?.context?.let { intent ->
                MaterialAlertDialogBuilder(intent)
                    .setTitle("Sair do aplicativo")
                    .setMessage("Você tem certeza que deseja encerrar sua sessão?")
                    .setNegativeButton("Cancelar") { _, _ -> }

                    .setPositiveButton("Sair") { _, _ ->
                        activity?.let { ctx ->
                            val sharedPreferences = ctx.getSharedPreferences(
                                getString(R.string.PREF_USER_DATA),
                                Context.MODE_PRIVATE
                            )
                            val editor = sharedPreferences.edit()
                            editor.putBoolean(getString(R.string.PREF_USER_LOGADO), false)
                            editor.apply()

                            ctx.finishAffinity()

                            startActivity(Intent(intent, SplashActivity::class.java))
                        }
                    }.show()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.ajustes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}