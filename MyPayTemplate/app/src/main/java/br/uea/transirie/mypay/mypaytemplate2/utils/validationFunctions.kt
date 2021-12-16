package br.uea.transirie.mypay.mypaytemplate2.utils

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import br.uea.transirie.mypay.mypaytemplate2.R
import com.google.android.material.textfield.TextInputLayout

const val erro_preenchimento_obrigatorio = "Campo de preenchimento obrigatório."
const val CAMPO_OBRIGATORIO = "Campo obrigatório."
private const val CNPJ_INVALIDO = "CNPJ inválido."
private val REGEX_CNPJ = "^[0-9]{2}[.][0-9]{3}[.][0-9]{3}[/][0-9]{4}-[0-9]{2}$".toRegex()
private const val CNPJ_FIRST_DIGIT = 1
private const val CNPJ_LAST_DIGIT = 15
private const val CNPJ_FIRST_VERIF_DIGIT = 12
private const val CNPJ_SECON_VERIF_DIGIT = 13

fun pinHasErrors(tiPin: TextInputLayout, context: Context): Boolean {
    var hasErrors = false
    val senha = tiPin.editText?.text.toString()
    val tam = 4

    when {
        senha.isBlank() -> {
            tiPin.error = context.getString(R.string.erro_campo_obrigatorio)
            hasErrors = true
        }
        senha.length != tam -> {
            tiPin.error = context.getString(R.string.erro_pin_4_caracteres)
            hasErrors = true
        }
        else -> tiPin.error = null
    }

    return hasErrors
}
/**
 * Verifica se há erros no campo de confirmação de pin.
 * Se houver, atribui mensagens de erro e retorna true.
 * Se não, limpa as mensagens de erro e retorna false.
 * Os erros verificados são: se está vazio ou se o campo é diferente da senha.
 */
fun confPinHasErrors(tiConfPin: TextInputLayout, tiPin: TextInputLayout, context: Context): Boolean {
    var hasErrors = false
    val pin = tiPin.editText?.text.toString()
    val confPin = tiConfPin.editText?.text.toString()

    when {
        confPin.isBlank() -> {
            tiConfPin.error = context.getString(R.string.erro_campo_obrigatorio)
            hasErrors = true
        }
        confPin != pin -> {
            tiConfPin.error = context.getString(R.string.erro_pins_incompativeis)
            hasErrors = true
        }
        else -> tiConfPin.error = null
    }

    return hasErrors
}

/**
 * Verifica se há erros no campo de telefone.
 * Se houver, atribui mensagens de erro e retorna true.
 * Se não, limpa as mensagens de erro e retorna false.
 * Os erros verificados são: se está vazio e se os dados não tem formato de um telefone.
 */
fun telefoneHasErrors(tiTelefone: TextInputLayout): Boolean {
    var hasErrors = false
    val telefone = tiTelefone.editText?.text.toString()

    if (telefone.isBlank()) {
        tiTelefone.error = CAMPO_OBRIGATORIO
        hasErrors = true
    } else {
        val telefoneRegex = "^\\(?\\d{2}\\)? ?(([1-7])|(9\\d))\\d{3}[\\-]?\\d{4}$"
        val isValid = telefone.matches(Regex(telefoneRegex))

        if (!isValid) {
            tiTelefone.error = "Insira um telefone válido."
            hasErrors = true
        }
        else tiTelefone.error = null
    }

    return hasErrors
}

/**
 * Verifica se há erros em um campo de senha.
 * Se houver, atribui mensagens de erro. Se não, limpa as mensagens de erro.
 * Os erros verificados são: se está vazio se a entrada é menor que 8 caracteres.
 *
 * @return true se houver erros.
 * @param tiSenha o campo de texto que contém a senha.
 * @param context objeto utilizado para acessar o arquivo de string resources.
 */
fun senhaHasErrors(tiSenha: TextInputLayout, context: Context): Boolean {
    var hasErrors = false
    val senha = tiSenha.editText?.text.toString()
    val tamMinimo = 6

    when {
        senha.isBlank() -> {
            tiSenha.error = context.getString(R.string.erro_campo_obrigatorio)
            hasErrors = true
        }
        senha.length < tamMinimo -> {
            tiSenha.error = context.getString(R.string.erro_senha_6_ou_mais_caracteres)
            hasErrors = true
        }
        else -> tiSenha.error = null
    }

    return hasErrors
}

/**
 * Verifica se há erros no campo de confirmação de senha.
 * Se houver, atribui mensagens de erro e retorna true.
 * Se não, limpa as mensagens de erro e retorna false.
 * Os erros verificados são: se está vazio ou se o campo é diferente da senha.
 */
fun confSenhaHasErrors(tiConfSenha: TextInputLayout, tiSenha: TextInputLayout, context: Context): Boolean {
    var hasErrors = false
    val senha = tiSenha.editText?.text.toString()
    val confSenha = tiConfSenha.editText?.text.toString()

    when {
        confSenha.isBlank() -> {
            tiConfSenha.error = context.getString(R.string.erro_campo_obrigatorio)
            hasErrors = true
        }
        confSenha != senha -> {
            tiConfSenha.error = context.getString(R.string.erro_senhas_incompativeis)
            hasErrors = true
        }
        else -> tiConfSenha.error = null
    }

    return hasErrors
}

/**
 * Verifica se há erros no textInputLayout de email.
 * Se houver, atribui mensagens de erro e retorna true.
 * Se não, limpa as mensagens de erro e retorna false.
 * Os erros verificados são: se o campo está fora do formato correto para um email ou está vazio.
 */
fun emailHasErrors(tiEmail: TextInputLayout, context: Context): Boolean {
    var hasErrors = false
    val email = tiEmail.editText?.text.toString()

    if(email.isBlank()) {
        tiEmail.error = context.getString(R.string.erro_campo_obrigatorio)
        hasErrors = true
    } else {
        val emailPattern = Patterns.EMAIL_ADDRESS.matcher(email)

        if (!emailPattern.matches()) {
            tiEmail.error = context.getString(R.string.erro_insira_email_valido)
            hasErrors = true
        }
        else tiEmail.error = null
    }

    return hasErrors
}

/**
 * Verifica se o textInputLayout está vazio.
 * Caso esteja, atribui uma mensagem de erro e retorna true.
 * Caso não, limpa as mensagens de erro e retorna false.
 */
fun inputHasEmptyError(textInput: TextInputLayout, context: Context): Boolean {
    var hasEmptyError = false

    if (textInput.editText?.text.toString().isBlank() ) {
        textInput.error = context.getString(R.string.erro_campo_obrigatorio)
        hasEmptyError = true
    }
    else textInput.error = null

    return hasEmptyError
}

/**
 * Verifica se há erros no campo de CPF.
 * Se houver, atribui mensagens de erro e retorna true.
 * Se não, limpa as mensagens de erro e retorna false.
 * Os erros verificados são: se está vazio e se os dígitos verificadores não são válidos.
 */
fun cpfHasErrors(tiCpf: TextInputLayout, context: Context): Boolean {
    var hasErrors = false
    val campo = tiCpf.editText?.text.toString()

    if (campo.isBlank()) {
        tiCpf.error = context.getString(R.string.erro_campo_obrigatorio)
        hasErrors = true
    } else {
        if (campo.length < 15 && !isCPF(campo)) {
            tiCpf.error = context.getString(R.string.erro_insira_cpf_valido)
            hasErrors = true
        }
        else tiCpf.error = null
    }

    return hasErrors
}

/**
 * Verifica se é um cpf válido.
 *
 * Autor: Thiago Filadelfo
 * Disponível em <gist.github.com/trfiladelfo/92edd1cad568ae6bae6c026dac52dff2>
 */
private fun isCPF(document: String): Boolean {
    if (TextUtils.isEmpty(document)) return false

    val numbers = arrayListOf<Int>()

    document.filter { it.isDigit() }.forEach {
        numbers.add(it.toString().toInt())
    }

    if (numbers.size != 11) return false

    //repeticao
    (0..9).forEach { n ->
        val digits = arrayListOf<Int>()
        repeat((0..10).count()) { digits.add(n) }
        if (numbers == digits) return false
    }

    //digito 1
    val dv1 = ((0..8).sumBy { (it + 1) * numbers[it] }).rem(11).let {
        if (it >= 10) 0 else it
    }

    val dv2 = ((0..8).sumBy { it * numbers[it] }.let { (it + (dv1 * 9)).rem(11) }).let {
        if (it >= 10) 0 else it
    }

    return numbers[9] == dv1 && numbers[10] == dv2
}
