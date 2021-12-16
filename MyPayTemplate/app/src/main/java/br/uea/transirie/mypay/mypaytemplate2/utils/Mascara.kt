package br.uea.transirie.mypay.mypaytemplate2.utils

import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Switch
import com.google.android.material.textfield.TextInputEditText

/**
 *  @author edufpaiva
 *  @param mascara O formato que a string deverá ser formatada
 *  @sample mascaraSample
 *  @see
 *  A -> para letras maiusculas,
 *  9 ou N-> para números,
 *  D -> para letras maiusculas e números,
 *  d -> para letras maiusculas ou minusculas e números
 *  a -> letras minusculas,
 *  B -> letras maiusculas e minusculas,
 * */
class Mascara(var mascara: String) {
    private var listaRegex: List<String>
    private var regex: Regex

    init {
        listaRegex = mascara.split("").subList(1, mascara.length + 1).map { it ->
            when (it) {
                "A" -> "[A-Z]"
                "a" -> "[a-z]"
                "B" -> "[a-zA-Z]"
                "9" -> "[0-9]"
                "N" -> "[0-9]"
                "D" -> "[A-Z0-9]"
                "d" -> "[A-Za-z0-9]"
                else -> "[$it]"
            }
        }
        regex = Regex(listaRegex.reduce { acc, s -> "$acc$s" })
    }

    /**
     * Formata um texto no padrão de mascara desejado.
     * @param text O texto a ser formatodo ao padão da mascara.
     * @return A String Formatada
     * */
    fun formatTxt(text: String): String {

        var txt = text.replace(" ", "")

        if (txt.isNullOrEmpty()) return ""
        else if (txt.length > mascara.length) {
            var char = txt.drop(txt.length - 1)
            txt = "${txt.dropLast(2)}$char"
        }

        txt = txt.replace(Regex("[^0-9A-Z]"), "")

        var ret = mascara.replace(Regex("[ABD9Nad]"), "#")

        for (i in txt) ret = ret.replaceFirst("#", i.toString())


        ret = ret.dropLastWhile { it -> it.toString().matches(Regex("[^A-Z0-9]")) }

        if (ret.isNullOrEmpty()) return ""

        var subRegex = Regex(listaRegex.subList(0, ret.length).reduce { a, b -> "$a$b" })

        if (!ret.matches(subRegex)) ret = ret.dropLast(1)

        return ret
    }

    /**
     * Compara um texto com o padrão de mascara escolhido.
     * @param text O texto a ser comparado
     * @return True se o texto passar no padão, False se ele falhar no teste
     * */
    fun compareString(text: String): Boolean {
        return regex.matches(text)
    }

    companion object {
        val MASCARA_PLACA = "AAA-NDNN"
        val MASCARA_CNPJ = "NN.NNN.NNN/NNNN-NN"
        val MASCARA_CEP = "NNNNN-NNN"
        val MASCARA_TEL_FIXO = "(NN)NNNN-NNNN"
        val MASCARA_TEL_CELULAR = "(NN)NNNNN-NNNN"

        /**
         * Transforma o texto para o formato de dinheiro 0,00.
         * @see 1   retorna 0,01 <br> 12  retorna 0,12 <br> 123 retorna 1,23.
         * @param text Texto a ser transformado.
         * @param separador O separador entre os numeros - "," | "."
         *
         * */
        fun formatMoney(text: String, separador: String = ","): String {
            var nText = text
                .replace(".", "")
                .replace(",", "")
                .replace(separador, "")
                .replace("^[0]+".toRegex(), "")

            while (nText.length < 3) nText = "0$nText"
            nText = "${
                nText.substring(0,
                    nText.lastIndex - 1)
            }$separador${nText.substring(nText.lastIndex - 1)}"
            return nText
        }


        /**
         * Retorna texto no formato 99.999.999/9999-99.
         *@param text O texto a ser transformado.
         *@return String
         * */
        fun formatCNPJ(text: String): String {
            return Mascara(this.MASCARA_CNPJ).formatTxt(text)
        }

        /**
         * Retorna texto no formato (99)12345-6789 ou (99)1234-5678.
         *@param text O texto a ser transformado.
         *@return String
         * */
        fun formatTel(text: String): String {
            return if (text.replace(Regex("[+()\\-]*"), "").length > 10)
                Mascara(this.MASCARA_TEL_CELULAR).formatTxt(text)
            else
                Mascara(this.MASCARA_TEL_FIXO).formatTxt(text)
        }

        /**
         * Retorna texto no formato ABC-1234 ou ABC-1A12.
         *@param text O texto a ser transformado.
         *@return String
         * */
        fun formatPlaca(text: String): String {
            return Mascara(this.MASCARA_PLACA).formatTxt(text)
        }

        /**
         * Retorna texto no formato ABC-1234 ou ABC-1A12,
         * Atualiza o teclado da view para o tipo esperado
         * e mostra Switch de mercosul se houver.
         *
         *@param text -> O texto a ser transformado.
         *@param placaEditText -> O input de texto para ser atualizado.
         *@param switchMercosul -> Se houver, O switch que vai ser ativo se houver placa mercosul.
         *@param mercosul -> Se houver, true se a placa dor padrão mercosul e false se for modelo antigo.
         *@return String
         * */
        fun formatPlacaAndUpdateView(
            text: String,
            placaEditText: TextInputEditText,
            switchMercosul: Switch? = null,
            mercosul: Boolean? = null,
        ): String {
            val textWithMask = this.formatPlaca(text.toUpperCase())

            val mercosul = switchMercosul?.isChecked ?: (mercosul ?: false)

            when (textWithMask.length) {
                0 -> placaEditText.inputType =
                    InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                1 -> placaEditText.inputType =
                    InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                2 -> placaEditText.inputType =
                    InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                5 -> {
                    if (mercosul) {
                        placaEditText.inputType =
                            InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                    } else {
                        placaEditText.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                }
                else -> placaEditText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            if (switchMercosul != null) {
                switchMercosul.visibility =
                    if (textWithMask.length == 5) View.VISIBLE else View.GONE
            }

            if (!text.equals(textWithMask)) {
                placaEditText.setText(textWithMask)
                placaEditText.setSelection(placaEditText.text.toString().length)
            }

            return textWithMask
        }

        /**
         * Se o switch for ativo, altera o tipo de teclado e muda o foco para o edittext de placa.
         * @param context -> O contexto da activitie.
         * @param isChecked -> O estado do switch.
         * @param placaEditText -> O edit text para mudar o foco.
         * @return String
         * */
        fun onCheckPlacaSwitch(
            context: Context,
            isChecked: Boolean,
            placaEditText: TextInputEditText,
        ) {
            var textPlaca = placaEditText.text.toString()
            if (textPlaca.length == 5) {
                if (isChecked) {
                    placaEditText.inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                } else {
                    placaEditText.inputType = InputType.TYPE_CLASS_NUMBER
                }
            }
            placaEditText.requestFocus()
            if (placaEditText.hasFocus()) {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        }

    }

    private fun mascaraSample() {
        // AAA  ->  3 letras maiusculas
        // -    ->  hifem
        // N    ->  1 numero
        // D    ->  Numero ou Letra Maiuscula
        // NN   ->  2 Numeros
        var mascara = Mascara("AAA-NDNN")

        // retorno  PHP-8889
        var textoFormatado = mascara.formatTxt("PHP8889")

        // compara se o texto esta de acordo com a mascara
        if (mascara.compareString(textoFormatado)) {
            print("O texto foi formatado")
        }
    }
}