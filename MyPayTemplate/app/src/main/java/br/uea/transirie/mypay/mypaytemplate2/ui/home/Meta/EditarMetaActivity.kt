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
import androidx.core.content.ContextCompat
import br.uea.transirie.mypay.mypaytemplate2.R
import br.uea.transirie.mypay.mypaytemplate2.model.Meta
import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy.MaskBrMonetaryValue
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_cadastrar_meta.*
import kotlinx.android.synthetic.main.activity_editar_meta.*
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class EditarMetaActivity : AppCompatActivity() {
    var day = 0
    var month = 0
    var year = 0
    var idMeta:Long = 0L
    lateinit var viewModel:ManterMetaViewModel
    lateinit var metaProcurada:Meta
    private lateinit var dataMes:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_meta)

        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.colorPrimaryVariant
        )

        toolbarME.setNavigationOnClickListener { finish() }

        val intent = intent
        idMeta = intent.getIntExtra("id_meta",0).toLong()

        mostrarDados()

        btEditarMeta.setOnClickListener {
            textInputNomeMetaEdit.error = null
            textInputValorMetaEdit.error =null

            it.hideKeyboard()
            if (validDados()){
                modificarDados()
            }
        }
    }
    private fun validDados():Boolean{
        val nome = txtNomeMetaEdit.text.toString()
        val valor = txtValorMetaEdit.text.toString()
        var x = 0

        if (nome.isEmpty()){
            textInputNomeMetaEdit.error = "Campo obrigatório."
            x += 1
        }
        if (valor.isEmpty()){
            textInputValorMetaEdit.error = "Campo obrigatório."
            x += 1
        }
        dataMesResgatar()
        val validaData: Date
        getDateTimeCalendar()
        val dataAtual: Date
        try {
            val spf = SimpleDateFormat("dd/MM/yyyy")
            dataAtual = spf.parse("$day/$month/$year")
            spf.isLenient = false
            validaData = spf.parse("${autoCompleteTextView22.text}/$dataMes/${autoCompleteTextView31.text}")
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
    private fun modificarDados(){
        doAsync {
            val valor = txtValorMetaEdit.text.toString()
                .replace(".","").replace(",",".").toFloat()
            dataMesResgatar()
            val dataMeta = "${autoCompleteTextView22.text}/$dataMes/${autoCompleteTextView31.text}"
            metaProcurada.nome = txtNomeMetaEdit.text.toString()
            metaProcurada.data = dataMeta
            metaProcurada.valor = valor
            viewModel.updateMeta(metaProcurada)
            modificarDados()
        }
    }
    private fun AnkoAsyncContext<EditarMetaActivity>.modificarDados(){
        uiThread {
            val toast = Toast.makeText(this@EditarMetaActivity,"Meta atualizada com sucesso!",Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM,0,144)
            toast.show()
            finish()
        }
    }
    private fun mostrarDados(){
        doAsync {
            viewModel = ManterMetaViewModel(AppDatabase.getDatabase(this@EditarMetaActivity))
            metaProcurada = viewModel.metaByID(idMeta)
            uiThread {
                val data = metaProcurada.data
                val dataLista = data.split("/")
                txtNomeMetaEdit.setText(metaProcurada.nome)

                val dias = resources.getStringArray(R.array.Dias)
                val arrayAdapter = ArrayAdapter(this@EditarMetaActivity,R.layout.dropdown_meta, dias)
                autoCompleteTextView22.setText(dataLista[0])
                autoCompleteTextView22.setAdapter(arrayAdapter)

                dataMesPreencher(dataLista[1])
                val mes = resources.getStringArray(R.array.MesRed)
                val arrayAdapter2 = ArrayAdapter(this@EditarMetaActivity, R.layout.dropdown_meta, mes )
                autoCompleteTextView21.setText(dataMes)
                autoCompleteTextView21.setAdapter(arrayAdapter2)

                val ano = resources.getStringArray(R.array.Ano)
                val arrayAdapter3= ArrayAdapter(this@EditarMetaActivity, R.layout.dropdown_meta, ano )
                autoCompleteTextView31.setText(dataLista[2])
                autoCompleteTextView31.setAdapter(arrayAdapter3)

                configuraCampoPreco(txtValorMetaEdit)
                var meta = ""
                if (metaProcurada.valor.toString().last().toString() == "0"){
                    meta = metaProcurada.valor.toString() + "0"
                }else{
                    meta = metaProcurada.valor.toString()
                }
                txtValorMetaEdit.setText(meta)
            }
        }
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

    private fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
    // função para quando o foco dos editTexts mudarem, o teclado se recolher
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun dataMesResgatar(){
        val mes = autoCompleteTextView21.text.toString()
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
    private fun dataMesPreencher(mes:String){
        if (mes == "01"){
            dataMes = "Jan"
        }
        if (mes == "02"){
            dataMes = "Fev"
        }
        if (mes == "03"){
            dataMes = "Mar"
        }
        if (mes == "04"){
            dataMes = "Abr"
        }
        if (mes == "05"){
            dataMes = "Mai"
        }
        if (mes == "06"){
            dataMes = "Jun"
        }
        if (mes == "07"){
            dataMes = "Jul"
        }
        if (mes == "08"){
            dataMes = "Ago"
        }
        if (mes == "09"){
            dataMes = "Set"
        }
        if (mes == "10"){
            dataMes = "Out"
        }
        if (mes == "11"){
            dataMes = "Nov"
        }
        if (mes == "12"){
            dataMes = "Dez"
        }
    }
    private fun getDateTimeCalendar(){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH) + 1
        year = cal.get(Calendar.YEAR)
    }
}