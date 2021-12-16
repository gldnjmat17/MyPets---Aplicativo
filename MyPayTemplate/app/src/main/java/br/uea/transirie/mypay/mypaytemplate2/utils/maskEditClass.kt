package br.uea.transirie.mypay.mypaytemplate2.utils

import android.widget.EditText
import com.redmadrobot.inputmask.MaskedTextChangedListener

fun maskEditText(mask: String = "", editText: EditText) {
    val listener = MaskedTextChangedListener(mask, editText)
    editText.addTextChangedListener(listener)
    editText.onFocusChangeListener = listener
}
