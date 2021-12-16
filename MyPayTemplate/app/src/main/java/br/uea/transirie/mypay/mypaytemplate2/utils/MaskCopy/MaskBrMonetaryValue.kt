package br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class MaskBrMonetaryValue {
    companion object {
        fun mask(editableText: EditText): TextWatcher {
            return object : TextWatcher {

                private fun clearInputText(s: CharSequence?): String {
                    return s.toString()
                        .replace(",", "")
                        .replace(".", "")
                }

                private fun insertChar(c: Char, s: String, position: Int): String {
                    return s.substring(0, position) + c + s.substring(position, s.length)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    if (s.toString().isEmpty())
                        return

                    // extrai virgula e pontos
                    var formatedValue = clearInputText(s)

                    if (formatedValue.length < 3) {
                        formatedValue = "0$formatedValue"
                    }

                    if (formatedValue.length >= 3) {

                        // caso a entrada tenha os valores de zeros iniciais
                        // remove-se os zeros a esquerda enquanto se insere valores
                        if (s.toString().length == 5 && formatedValue[0] == '0') {
                            formatedValue = formatedValue.drop(1)
                        }

                        formatedValue = formatedValue.reversed()

                        // adiciona virgula de casa decimal
                        formatedValue = insertChar(',', formatedValue, 2)

                        // adiciona pontos de grandezas
                        if (formatedValue.length > 6) {
                            for (i in 6 until s.toString().length step 4) {
                                if (i != formatedValue.length) {
                                    formatedValue = insertChar('.', formatedValue, i)
                                }
                            }
                        }

                        formatedValue = formatedValue.reversed()
                    }

                    editableText.removeTextChangedListener(this)
                    editableText.setText(formatedValue)
                    editableText.setSelection(formatedValue.length)
                    editableText.addTextChangedListener(this)
                }

                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int,
                ) {
                }
            }
        }
    }
}