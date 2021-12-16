package br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.utils

import br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.enums.SpacingLetters
import br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.enums.SpacingLines
import br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.enums.IndentLeft
import br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.enums.FontSize
import com.pax.dal.entity.EFontTypeAscii

object ConfigurationFont {

    fun getFont(fontSize: FontSize): EFontTypeAscii {

        return when (fontSize) {
            FontSize.PEQUENA -> EFontTypeAscii.FONT_8_16
            FontSize.NORMAL -> EFontTypeAscii.FONT_8_32
            FontSize.MEDIA -> EFontTypeAscii.FONT_12_24
            FontSize.GRANDE -> EFontTypeAscii.FONT_12_48
            FontSize.EXTRA_GRANDE -> EFontTypeAscii.FONT_32_48
        }

    }

    fun getSpaceLetter(spacingLetters: SpacingLetters): Int {
        return when (spacingLetters) {
            SpacingLetters.NORMAL -> 0
            SpacingLetters.GRANDE -> 16
            SpacingLetters.EXTRA_GRANDE -> 32
        }
    }

    fun getSpaceLine(spacingLines: SpacingLines): Int {
        return when (spacingLines) {
            SpacingLines.NORMAL -> 0
            SpacingLines.GRANDE -> 16
            SpacingLines.EXTRA_GRANDE -> 32
        }
    }

    fun getIndentLeft(indentLeft: IndentLeft): Int {
        return when (indentLeft) {
            IndentLeft.TAB_ZERO -> 0
            IndentLeft.TAB_UM -> 8
            IndentLeft.TAB_DOIS -> 16
            IndentLeft.TAB_TRES -> 32
            IndentLeft.TAB_QUATRO -> 64
        }
    }

}


