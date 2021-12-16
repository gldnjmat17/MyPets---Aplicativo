package br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class MyMask(private val maskType: MaskType, private val editText: EditText): TextWatcher{
    /*private val classTag = "MY_MASK"*/
    private var priceColAlreadyRemoved = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        s?.let {
            if (after == 0) {
                /* Log.i(classTag, "Remoção apenas.") */

                if (maskType == MaskType.Price)
                    priceColAlreadyRemoved = (start == it.length - 3) && (count == 1)
            }
            /*
            else if (count == 0)
                 Log.i(classTag, "Adição apenas.")
            */
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        editText.removeTextChangedListener(this)

        s?.let {
            var editStr = it.toString()
            /* Log.i(classTag, "editable:" + editStr + " \\ length: ${it.length}") */

            editStr = when (maskType) {
                MaskType.Price -> {
                    afterPriceTextChanged(editStr)
                }
                MaskType.Telephone -> {
                    afterPhoneTextChanged(editStr)
                }
            }

            it.replace(0, it.length, editStr)
        }

        editText.addTextChangedListener(this)
    }

    private fun afterPriceTextChanged(strArg: String): String {
        var str = strArg

        str = removeColons(str)

        str = checkIntegerPart(str)

        str = addColon(str)

        return str
    }

    private fun removeColons(strArg: String): String {
        var str = strArg
        val colonChar = ','; val one = 1

        if (priceColAlreadyRemoved) {
            str = removeIntegerUnit(str)
        } else {
            var delPos = str.indexOf(colonChar)

            while (delPos != -1) {
                str = str.delete(delPos, delPos.plus(one))
                /* Log.i(classTag, "virgula removida: $str") */

                delPos = str.indexOf(colonChar)
            }
        }

        return str
    }

    private fun checkIntegerPart(strArg: String): String {
        var str = strArg
        var length = str.length

        if (length > 3) {
            if (str.first() == '0') {
                str = str.delete(0, 1)
                /* Log.i(classTag, "zero à esquerda retirado: $str") */
            }
            /*else {
                 Log.i(classTag, "Parte inteira: >= 10")
            }*/
        } else {
            /* Log.i(classTag, "Parte inteira: < 10") */

            while (length < 3) {
                str = str.insert(0, "0")
                length += 1
                /*Log.i(classTag, "zero à esquerda adicionado: $str")*/
            }
        }

        return str
    }

    private fun addColon(strArg: String): String {
        var str = strArg
        val delPos = str.length - 2
        val col = ","

        str = str.insert(delPos, col)

        /*Log.i(classTag, "virgula reposicionada: $str")*/
        return str
    }

    private fun removeIntegerUnit(strArg: String): String {
        var str = strArg
        val length = str.length

        str = str.delete(length - 3, length - 2)

        /*Log.i(classTag, "unidade dos inteiros removida: $str")*/
        return str
    }

    private fun afterPhoneTextChanged(strArg: String): String {
        var str = strArg

        //removeNotNumbers
        str = str.filter { it.isDigit() }
        /*Log.i(classTag, "String sem caracteres especiais: $str")*/

        val length = str.length

        if (length > 0) {
            var s = "("
            var position = 0

            str = str.insert(position, s)

            if (length > 2) {
                s = ") "
                position += 3

                str = str.insert(position, s)

                //929452 <- length 6
                if (length > 6) {
                    s = "-"
                    position += 6

                    //9294523690 <- length 10
                    //92994523690 <- ... 11
                    if (length > 10)
                        position += 1

                    str = str.insert(position, s)

                    /*Log.i(classTag, "String com traço: $str")*/
                }
            }
        }


        return str
    }
}

enum class MaskType {
    Price,
    Telephone,
}