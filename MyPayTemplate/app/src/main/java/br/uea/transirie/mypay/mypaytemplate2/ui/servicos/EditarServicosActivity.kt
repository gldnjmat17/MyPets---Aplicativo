package br.uea.transirie.mypay.mypaytemplate2.ui.servicos

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.adapters.ServicoAdapter
import br.uea.transirie.mypay.mypaytemplate2.databinding.ActivityEditarServicosBinding
import br.uea.transirie.mypay.mypaytemplate2.model.Servico
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Tela responsável por gerenciar os serviços cadastrados
 */
class EditarServicosActivity : AppCompatActivity() {
    private lateinit var adapterServico: ServicoAdapter
    private lateinit var viewModel: EditarServicosViewModel
    private lateinit var binding: ActivityEditarServicosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarServicosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // -------------------------------------------------------------------------------------
        // Inicializa RecyclerView
        adapterServico = ServicoAdapter(
            this@EditarServicosActivity,
            mutableListOf(),
            ::onEditServico,
            ::onDeleteServico
        )
        binding.rvListaServicos.adapter = adapterServico
        binding.rvListaServicos.layoutManager = LinearLayoutManager(this)

        // -------------------------------------------------------------------------------------
        // Inicializa viewModel e obtém lista inicial de serviços
        doAsync {
            viewModel = EditarServicosViewModel(AppDatabase.getDatabase(applicationContext))
            val listaServicos = viewModel.getAllServicos()
            uiThread {
                adapterServico.swapData(listaServicos)
            }
        }

        // -------------------------------------------------------------------------------------
        // Faz botão "adicionar" inserir no banco de dados e depois atualizar a lista
        binding.btAdicionar.setOnClickListener {
            doAsync {
                viewModel.adicionarServico(
                    Servico(
                        descricao = "Lavagem Simples",
                        preco = 10f
                    )
                )
                val novaListaServicos = viewModel.getAllServicos()
                uiThread {
                    adapterServico.swapData(novaListaServicos)
                }
            }
        }
    }

    /**
     * Callback para quando o usuário apertar em "editar serviço"
     */
    fun onEditServico(servico: Servico) {
        // abre uma caixa de diálogo para pedir novo valor
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.preco))
        builder.setMessage(getString(R.string.digite_preco))

        // cria campo de texto para receber novo valor e configura como numérico
        val input = EditText(this)
        input.setText(servico.preco.toString())
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        builder.setView(input)

        // botão OK: atualiza lista de serviços no viewModel e depois na interface
        // observe que consultamos no viewModel logo após inserir, pois os dados finais devem ficar
        // no viewModel.
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            try {
                servico.preco = input.text.toString().toFloat()
                doAsync {
                    viewModel.atualizarServico(servico)
                    val novaListaServico = viewModel.getAllServicos()
                    uiThread {
                        adapterServico.swapData(novaListaServico)
                        dialog.dismiss()
                    }
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Informe um valor", Toast.LENGTH_SHORT).show()
            }
        }

        // botão cancelar
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    /**
     * Callback para quando o usuário apertar em "deletar serviço"
     */
    fun onDeleteServico(servico: Servico) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.confirmacao))
        builder.setMessage(getString(R.string.pergunta_realmente_remover))
        builder.setPositiveButton(getString(R.string.sim)) { dialog, _ ->
            doAsync {
                viewModel.deletarServico(servico)
                val novaListaServico = viewModel.getAllServicos()
                uiThread {
                    adapterServico.swapData(novaListaServico)
                    dialog.dismiss()
                }
            }
        }
        builder.setNegativeButton(R.string.nao) { dialog, _ -> dialog.cancel() }
        builder.show()
    }
    // função para quando o foco dos editTexts mudarem, o teclado se recolher
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}