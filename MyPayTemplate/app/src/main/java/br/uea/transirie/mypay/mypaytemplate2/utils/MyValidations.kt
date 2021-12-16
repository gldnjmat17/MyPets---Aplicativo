package br.uea.transirie.mypay.mypaytemplate2.utils

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import br.uea.transirie.mypay.mypaytemplate2.R
import com.google.android.material.textfield.TextInputLayout

class MyValidations(context: Context) {

    private val erroCampoObrigatorio = context.getString(R.string.erro_campo_obrigatorio)
    private val erroInsiraCpfValido = context.getString(R.string.erro_insira_cpf_valido)
    private val erroTelefoneInvalido = context.getString(R.string.erro_telefone_invalido)
    private val erroEmailInvalido = context.getString(R.string.erro_insira_email_valido)
    private val erroPinQuatroCaracteres = context.getString(R.string.erro_pin_4_caracteres)
    private val erroPinsIncompativeis = context.getString(R.string.erro_pins_incompativeis)

    /**
     * Verifica se o textInputLayout está vazio.
     * Caso esteja, atribui uma mensagem de erro. Caso não, limpa as mensagens de erro.
     *
     * @return um booleano simbolizando se o campo está vazio.
     * @param textInput o campo de texto a verificar.
     */
    fun inputHasEmptyError(textInput: TextInputLayout): Boolean {
        var hasEmptyError = false

        if (textInput.editText?.text.toString().isBlank()) {
            textInput.error = erroCampoObrigatorio
            hasEmptyError = true
        }
        else textInput.error = null

        return hasEmptyError
    }

    /**
     * Verifica se há erros no campo de CPF.
     * Se houver, atribui mensagens de erro. Se não, limpa as mensagens de erro.
     * Os erros verificados são: se está vazio e se os dígitos verificadores são inválidos.
     *
     * @return um booleano simbolizando se há erros.
     * @param tiCpf o campo de texto que contém o cpf.
     */
    fun cpfHasErrors(tiCpf: TextInputLayout): Boolean {
        var hasErrors = false
        val campo = tiCpf.editText?.text.toString()

        if (campo.isBlank()) {
            tiCpf.error = erroCampoObrigatorio
            hasErrors = true
        } else {
            if (campo.length < 15 && !isCPF(campo)) {
                tiCpf.error = erroInsiraCpfValido
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

    /**
     * Verifica se há erros no campo de telefone.
     * Se houver, atribui mensagens de erro. Se não, limpa as mensagens de erro.
     * Os erros verificados são: se está vazio; se os dados não tem formato de um telefone.
     *
     * @return um booleano simbolizando se há erros.
     * @param tiTelefone o campo de texto que contém o telefone.
     */
    fun telefoneHasErrors(tiTelefone: TextInputLayout): Boolean {
        var hasErrors = false
        val telefone = tiTelefone.editText?.text.toString()

        if (telefone.isBlank()) {
            tiTelefone.error = erroCampoObrigatorio
            hasErrors = true
        } else {
            val telefoneRegex = "^\\(?\\d{2}\\)? ?(([1-7])|(9\\d))\\d{3}[\\-]?\\d{4}$"
            val isValid = telefone.matches(Regex(telefoneRegex))

            if (!isValid) {
                tiTelefone.error = erroTelefoneInvalido
                hasErrors = true
            }
            else tiTelefone.error = null
        }

        return hasErrors
    }

    /**
     * Verifica se há erros no textInputLayout de email.
     * Se houver, atribui mensagens de erro. Se não, limpa as mensagens de erro.
     * Os erros verificados são: se está vazio; se os dados não tem formato de um email.
     *
     * @return um booleano simbolizando se há erros.
     * @param tiEmail o campo de texto que contém o email.
     */
    fun emailHasErrors(tiEmail: TextInputLayout): Boolean {
        var hasErrors = false
        val email = tiEmail.editText?.text.toString()

        if(email.isBlank()) {
            tiEmail.error = erroCampoObrigatorio
            hasErrors = true
        } else {
            val emailPattern = Patterns.EMAIL_ADDRESS.matcher(email)

            if (!emailPattern.matches()) {
                tiEmail.error = erroEmailInvalido
                hasErrors = true
            }
            else tiEmail.error = null
        }

        return hasErrors
    }

    /** Verifica se há erros no textInputLayout de Pin.
     * Se houver, atribui mensagens de erro. Se não, limpa as mensagens de erro.
     * Os erros verificados são: se está vazio; se os dados não tem 4 dígitos.
     *
     * @return um booleano simbolizando se há erros.
     * @param tiPin o campo de texto que contém o email.
     */
    fun pinHasErrors(tiPin: TextInputLayout): Boolean {
        var hasErrors = false
        val senha = tiPin.editText?.text.toString()
        val tam = 4

        when {
            senha.isBlank() -> {
                tiPin.error = erroCampoObrigatorio
                hasErrors = true
            }
            senha.length != tam -> {
                tiPin.error = erroPinQuatroCaracteres
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
    fun confPinHasErrors(tiConfPin: TextInputLayout, tiPin: TextInputLayout): Boolean {
        var hasErrors = false
        val pin = tiPin.editText?.text.toString()
        val confPin = tiConfPin.editText?.text.toString()

        when {
            confPin.isBlank() -> {
                tiConfPin.error = erroCampoObrigatorio
                hasErrors = true
            }
            confPin != pin -> {
                tiConfPin.error = erroPinsIncompativeis
                hasErrors = true
            }
            else -> tiConfPin.error = null
        }

        return hasErrors
    }
}