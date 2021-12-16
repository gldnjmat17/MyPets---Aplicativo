package br.uea.transirie.mypay.mypaytemplate2.ui.home.Cliente

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import br.com.paxbr.easypaymentpos.util.log
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Cliente
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_visualizar_cliente.*
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception

class VisualizarClienteActivity : AppCompatActivity() {
    private lateinit var cpfCliente:String
    private lateinit var clienteProcurado: Cliente
    private lateinit var usuario:String
    override fun onResume() {
        super.onResume()
        doAsync {
            /*
            localiza a lista do sistema de brindes cadastrado e filtra para obter o cadastro
            feito pelo usuário logado e com o cpd do cliente sendo visualizado
             */
            val viewModel = ManterClientesViewModel(AppDatabase.getDatabase(this@VisualizarClienteActivity))
            clienteProcurado = viewModel.clienteByCPF(cpfCliente)

            uiThread {
                // exibe as informações cadastradas do cliente localizado
                txtNomeClienteVisualizar.text = clienteProcurado.nome
                txtCPFClienteVisualizar.text = "${clienteProcurado.cpf}"
                txtTelefoneClienteVisualizar.text = "${clienteProcurado.telefone}"
                if (clienteProcurado.endereco.isEmpty()){
                    txtEndClienteVisualizar.text = "Não informado"
                }else{
                    txtEndClienteVisualizar.text="${clienteProcurado.endereco}"
                }
                if (clienteProcurado.sistema){
                    txtPontuacaoView.text = clienteProcurado.pontos.toString()
                    btConsultarBrindes.setTextColor(Color.parseColor("#3B688C"))
                    btConsultarBrindes.isClickable = true
                }else{
                    txtPontuacaoView.text = "Não participante"
                    btConsultarBrindes.setTextColor(Color.parseColor("#FFDCD6D6"))
                    btConsultarBrindes.isClickable = false
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_cliente)
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.colorPrimaryVariant
        )

        //finaliza a activity e volta para a tela anterior
        toolbar92.setNavigationOnClickListener { finish() }

        //resgata o usuário que está logado
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        usuario = preference.getString("nome_usuario", "").toString()

        //resgata o cpf do cliente selecionado
        val intent = intent
        cpfCliente = intent.getStringExtra("cpfCliente").toString()
        acharUsuario()

        btConsultarBrindes.setOnClickListener {
            val clienteJson = Gson().toJson(clienteProcurado)
            val intent = Intent(this,ResgatarBrindeActivity::class.java)
            intent.putExtra("clienteBrinde", clienteJson)
            startActivity(intent)
        }

        btEdExcluitCliente.setOnClickListener {
            val popup = PopupMenu(this,it)
            val inflater:MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.editar_excluir_menu,popup.menu)
            popup.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.menu_editar_cliente ->{
                        //envia o cpf do cliente sendo visualizado para a activity de edição
                        val intent = Intent(this, EditarClienteActivity::class.java)
                        intent.putExtra("cpf_cliente", cpfCliente)
                        startActivity(intent)
                        true
                    }
                    R.id.menu_excluir_cliente ->{
                        val clienteVenda = try {
                            Gson().fromJson(preference.getString("clienteEscolhido",""),Cliente::class.java).cpf
                        }catch (ex:Exception){
                            ""
                        }
                        if (clienteVenda.isEmpty()){
                            val builder:AlertDialog.Builder = AlertDialog.Builder(this)
                            builder.setMessage("Tem certeza que deseja excluir esse cliente?")
                            builder.setPositiveButton("EXCLUIR"){dialog,_->
                                doAsync {
                                    val viewModel = ManterClientesViewModel(AppDatabase.getDatabase(this@VisualizarClienteActivity))
                                    viewModel.deletarCliente(clienteProcurado)
                                    uiThread {
                                        val toast = Toast.makeText(this@VisualizarClienteActivity,"Cliente excluído com sucesso!",Toast.LENGTH_SHORT)
                                        toast.setGravity(Gravity.BOTTOM,0,144)
                                        toast.show()
                                        finish()
                                        dialog.dismiss()
                                    }
                                }
                            }
                            builder.setNegativeButton("CANCELAR"){dialog, _-> dialog.cancel()}
                            builder.show()
                        }else{
                            if (clienteVenda == cpfCliente){
                                val builder:AlertDialog.Builder = AlertDialog.Builder(this)
                                builder.setMessage("Cliente só pode ser excluído quando sua compra for finalizada ou o carrinho esvaziado")
                                builder.setPositiveButton("OK"){dialog,_->
                                    dialog.dismiss()
                                }
                                builder.show()
                            } else{
                                val builder:AlertDialog.Builder = AlertDialog.Builder(this)
                                builder.setMessage("Tem certeza que deseja excluir esse cliente?")
                                builder.setPositiveButton("EXCLUIR"){dialog,_->
                                    doAsync {
                                        val viewModel = ManterClientesViewModel(AppDatabase.getDatabase(this@VisualizarClienteActivity))
                                        viewModel.deletarCliente(clienteProcurado)
                                        uiThread {
                                            val toast = Toast.makeText(this@VisualizarClienteActivity,"Cliente excluído com sucesso!",Toast.LENGTH_SHORT)
                                            toast.setGravity(Gravity.BOTTOM,0,144)
                                            toast.show()
                                            finish()
                                            dialog.dismiss()
                                        }
                                    }
                                }
                                builder.setNegativeButton("CANCELAR"){dialog, _-> dialog.cancel()}
                                builder.show()
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }
    private fun acharUsuario(){
        doAsync {
            //localiza o cliente no banco de dados a partir do cpf
            val viewModel = ManterClientesViewModel(AppDatabase.getDatabase(this@VisualizarClienteActivity))
            clienteProcurado = viewModel.clienteByCPF(cpfCliente)
            try {
                acharUsuario(clienteProcurado)
            }catch (ex:Exception){}
        }
    }
    private fun AnkoAsyncContext<VisualizarClienteActivity>.acharUsuario(
        clienteProcurado:Cliente
    ){
        uiThread {
            // exibe as informações cadastradas do cliente localizado
            txtNomeClienteVisualizar.text = clienteProcurado.nome
            txtCPFClienteVisualizar.text = "${clienteProcurado.cpf}"
            txtTelefoneClienteVisualizar.text = "${clienteProcurado.telefone}"
            if (clienteProcurado.endereco.isEmpty()){
                txtEndClienteVisualizar.text = "Não informado"
            }else{
                txtEndClienteVisualizar.text="${clienteProcurado.endereco}"
            }
            if (clienteProcurado.sistema){
                txtPontuacaoView.text = clienteProcurado.pontos.toString()
                btConsultarBrindes.setTextColor(Color.parseColor("#3B688C"))
                btConsultarBrindes.isClickable = true
            }else{
                txtPontuacaoView.text = "Não participante"
                btConsultarBrindes.setTextColor(Color.parseColor("#FFDCD6D6"))
                btConsultarBrindes.isClickable = false
            }
        }
    }
}