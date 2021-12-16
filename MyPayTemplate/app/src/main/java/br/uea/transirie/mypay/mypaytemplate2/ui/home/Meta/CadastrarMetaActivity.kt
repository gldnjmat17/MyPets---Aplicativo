package br.uea.transirie.mypay.mypaytemplate2.ui.home.Meta

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Meta
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.PREF_DATA_NAME
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskBrMonetaryValue
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_cadastrar_meta.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class CadastrarMetaActivity : AppCompatActivity() {
    var day = 0
    var month = 0
    var year = 0
    private var df = DecimalFormat("0.00")
    private lateinit var gerenteCPF:String
    private lateinit var novoCadastro:Meta
    private lateinit var dataMes:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_meta)

        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.colorPrimaryVariant
        )
        preencherDropDown()
        configuraCampoPreco(txtValorMeta)

        // resgata usuário que está logado
        val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
        gerenteCPF = preference.getString("nome_usuario", "").toString()

        //finaliza a activity e volta para a tela anterior
        toolbarM.setNavigationOnClickListener {finish()}

        btCadastrarMeta.setOnClickListener {
            textInputNomeMeta.error = null
            textInputLayoutDia.error = null
            textInputLayoutMes.error = null
            textInputLayoutAno.error = null
            textInputValorMeta.error = null

            it.hideKeyboard()
            if (validDados()){
                fazerCadastro()
            }
        }
    }
    private fun validDados():Boolean{
        val nome = txtNomeMeta.text.toString()
        val dia = autoCompleteTextView.text.toString()
        val mes = autoCompleteTextView2.text.toString()
        val ano = autoCompleteTextView3.text.toString()
        val valor = txtValorMeta.text.toString()
        var x = 0

        if (nome.isEmpty()){
            textInputNomeMeta.error = "Campo obrigatório."
            x += 1
        }else if(nome.matches(Regex(".*\\d.*"))){
            textInputNomeMeta.error = "Nome inválido"
            x += 1
        }
        if (dia == "" || mes == "" || ano == ""){
            val toast = Toast.makeText(this,"Selecione uma data!",Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM,0,192)
            toast.show()
            x += 1
        }
        if (valor.isEmpty()){
            textInputValorMeta.error = "Campo obrigatório."
            x += 1
        }
        dataMes()
        val validaData:Date
        getDateTimeCalendar()
        val dataAtual:Date
        try {
            val spf = SimpleDateFormat("dd/MM/yyyy")
            dataAtual = spf.parse("$day/$month/$year")
            spf.isLenient = false
            validaData = spf.parse("${autoCompleteTextView.text}/$dataMes/${autoCompleteTextView3.text}")
            if (validaData.before(dataAtual)){
                val toast = Toast.makeText(this,"Data inválida",Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM,0,192)
                toast.show()
                x += 1
            }
        }catch (ex:Exception){
            val toast = Toast.makeText(this,"Data inválida",Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM,0,192)
            toast.show()
            x += 1
        }
        if (x != 0){
            return false
        }
        return true
    }
    private fun fazerCadastro(){
        val valor = txtValorMeta.text.toString()
            .replace(".","").replace(",",".").toFloat()
        dataMes()
        val dataMeta = "${autoCompleteTextView.text}/$dataMes/${autoCompleteTextView3.text}"
        //preenche o objeto com as informações inseridas para cadastro
        novoCadastro = Meta(0L,
            gerenteCPF,
            txtNomeMeta.text.toString(),
            dataMeta,
            valor,
            0f)
        val db = AppDatabase.getDatabase(this)
        doAsync {
            val viewModel = ManterMetaViewModel(AppDatabase.getDatabase(this@CadastrarMetaActivity))
            val metaCadastrada = viewModel.getAllMeta().filter {
                it.gerenteCPF == gerenteCPF
            }
            if (metaCadastrada.isNotEmpty()){
                viewModel.deletarMeta(metaCadastrada[0])
            }
            //insere o objeto no banco de dados
            db.metaDao().insert(novoCadastro)
            uiThread {
                val preference = getSharedPreferences(PREF_DATA_NAME, MODE_PRIVATE)
                val excedente = preference.getFloat("excedenteMeta",0f)
                if (metaCadastrada.isNotEmpty() && excedente > 1){
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@CadastrarMetaActivity)
                    builder.setCancelable(false)
                    builder.setMessage("Deseja adicionar o valor remanescente de R$ ${df.format(excedente).replace(".", ",")} da sua meta anterior em sua nova meta?")
                    builder.setPositiveButton("SIM"){dialog,_->
                        doAsync {
                            val viewModelM = ManterMetaViewModel(AppDatabase.getDatabase(this@CadastrarMetaActivity))
                            val metaCadastradaM = viewModelM.getAllMeta().filter {
                                it.gerenteCPF == gerenteCPF
                            }
                            metaCadastradaM[0].progresso = excedente
                            viewModel.updateMeta(metaCadastradaM[0])
                            uiThread {
                                val sharedEditor = preference.edit()
                                sharedEditor.putFloat("excedenteMeta",0f)
                                sharedEditor.apply()
                                val toast = Toast.makeText(this@CadastrarMetaActivity, "Meta cadastrada com sucesso!", Toast.LENGTH_SHORT)
                                toast.setGravity(Gravity.BOTTOM,0,144)
                                toast.show()
                                finish()
                                dialog.dismiss()
                            }
                        }
                    }
                    builder.setNegativeButton("NÃO"){dialog,_->
                        val sharedEditor = preference.edit()
                        sharedEditor.putFloat("excedenteMeta",0f)
                        sharedEditor.apply()
                        val toast = Toast.makeText(this@CadastrarMetaActivity, "Meta cadastrada com sucesso!", Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.BOTTOM,0,144)
                        toast.show()
                        finish()
                        dialog.dismiss()
                    }
                    builder.show()
                }else{
                    val toast = Toast.makeText(this@CadastrarMetaActivity, "Meta cadastrada com sucesso!", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM,0,144)
                    toast.show()
                    finish()
                }
            }
        }
    }
    private fun dataMes(){
        val mes = autoCompleteTextView2.text.toString()
        if (mes == "Jan"){
            dataMes = "01"
        }
        if (mes == "Fev"){
            dataMes = "02"
        }
        if (mes == "Mar"){
            dataMes = "03"
        }
        if (mes == "Abr"){
            dataMes = "04"
        }
        if (mes == "Mai"){
            dataMes = "05"
        }
        if (mes == "Jun"){
            dataMes = "06"
        }
        if (mes == "Jul"){
            dataMes = "07"
        }
        if (mes == "Ago"){
            dataMes = "08"
        }
        if (mes == "Set"){
            dataMes = "09"
        }
        if (mes == "Out"){
            dataMes = "10"
        }
        if (mes == "Nov"){
            dataMes = "11"
        }
        if (mes == "Dez"){
            dataMes = "12"
        }
    }

    private fun preencherDropDown(){
        //resgatam as listas com dias, meses e anos e preenchem o adapter para gerar o dropdown
        val dias = resources.getStringArray(R.array.Dias)
        val arrayAdapter = ArrayAdapter(this,R.layout.dropdown_meta, dias)
        autoCompleteTextView.setAdapter(arrayAdapter)
        val mes = resources.getStringArray(R.array.MesRed)
        val arrayAdapter2 = ArrayAdapter(this, R.layout.dropdown_meta, mes )
        autoCompleteTextView2.setAdapter(arrayAdapter2)
        val ano = resources.getStringArray(R.array.Ano)
        val arrayAdapter3= ArrayAdapter(this, R.layout.dropdown_meta, ano )
        autoCompleteTextView3.setAdapter(arrayAdapter3)
    }
    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
    private fun getDateTimeCalendar(){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH) + 1
        year = cal.get(Calendar.YEAR)
    }
    @SuppressLint("SetTextI18n")
    private fun configuraCampoPreco(preco: TextInputEditText?) {
        preco?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && preco.text.toString().isEmpty()) {
                preco.setText("0,00")
            } else if (!hasFocus && preco.text.toString() == "0,00") {
                preco.setText("")
            }
        }
        preco?.addTextChangedListener(MaskBrMonetaryValue.mask(preco))
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