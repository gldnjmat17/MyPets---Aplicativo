package br.uea.transirie.mypay.mypaytemplate2.utils.MaskCopy

import kotlin.math.round
import kotlin.math.roundToLong

/**
 * @return retorna uma string que representa, em formato de dinheiro (2 casas decimais),
 * o valor do objeto Double. Exemplos: f(1.710001) -> R$ 1,71; f(98765.969995) -> R$ 99999,97
 */
fun Double.toPrecoString(withPrefix: Boolean = true): String {
    val precoStr = doubleToString(this.roundToPreco())

    return if (withPrefix) precoStr.withPrecoPrefix() else precoStr
}

/**
 * Uma vez que é impossível representar todos os valores reais, alguns valores vêm do banco
 * com o valor da segunda casa decimal diferente. Para corrigir, multiplicamos o valor por 100,
 * arredondamos esse produto a um valor inteiro e então dividimos por 100. Desconheço outra solução.
 */
fun Double.roundToPreco(): Double {
    val factor = 100.0
    return (this * factor).roundToLong() / factor
}

/**
 * Adiciona um prefixo "R$ " à string.
 */
fun String.withPrecoPrefix(): String {
    val precoPrefix = "R$ "

    return precoPrefix.plus(this)
}

private fun doubleToString(f: Double, precision: Int = 2): String {
    var s = f.toString()

    val dot = "."
    val integerPart = s.substringBefore(dot)
    var floatingPart = s.substringAfter(dot)

    val zero = '0'
    val len = floatingPart.length
    floatingPart = if (len > precision) {
        floatingPart.delete(precision, len)
    } else {
        floatingPart.padEnd(precision, zero)
    }

    val colon = ","
    s = integerPart + colon + floatingPart

    return s
}

fun String.toPrecoDouble(hasPrefix: Boolean = false): Double {
    return if (hasPrefix) {
        val prefixLength = 3
        stringToDouble(this.drop(prefixLength))
    } else stringToDouble(this)
}

private fun stringToDouble(s: String, precision: Int = 2): Double {
    s.filter { it.isDigit() }.let {
        val dot = '.'

        return it.insert(it.length - precision, "$dot").toDouble()
    }
}

/**
 * Remove parte da string de acordo com o intervalo informado.
 *
 * @param st o índice do primeiro caractere a ser removido
 * @param en o índice do primeiro caractere depois da parte removida
 */
fun String.delete(st: Int, en: Int): String {
    return this.replaceRange(st, en, "")
}

fun String.insert(st: Int, charSequence: CharSequence): String {
    return this.replaceRange(st, st, charSequence)
}

fun arredondaCentavos(f: Float) = round(f*100)/100