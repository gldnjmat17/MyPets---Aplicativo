package br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.printer


import android.content.Context
import android.graphics.Bitmap
import android.util.Log


import br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.utils.ConfigurationFont
import br.uea.transirie.mypay.mypaytemplate2.R

import br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.base.PrinterBase
import br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.enums.*
import br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.utils.ConvertLayout


import com.pax.dal.entity.EFontTypeExtCode
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ModulePrinter(private val context: Context) {

    init {
        PrinterBase.setupPrinter(context)
    }

    fun printText(
        text: String?,
        size: FontSize = FontSize.NORMAL,
        spacingLetters: SpacingLetters = SpacingLetters.NORMAL,
        spacingLines: SpacingLines = SpacingLines.NORMAL,
        indentLeft: IndentLeft = IndentLeft.TAB_UM,
        intensity: FontIntensity = FontIntensity.NORMAL
    ) {

        text?.let {
           GlobalScope.launch{
                PrinterBase.init()
                PrinterBase.setSizeFont(
                    ConfigurationFont.getFont(size),
                    EFontTypeExtCode.FONT_24_24
                )
                PrinterBase.setSpaceFont(
                    ConfigurationFont.getSpaceLetter(spacingLetters).toByte(),
                    ConfigurationFont.getSpaceLine(spacingLines).toByte()
                )
                PrinterBase.setLeftIntent(ConfigurationFont.getIndentLeft(indentLeft))
                PrinterBase.setIntentFont(intensity.nivel)
                PrinterBase.printStr(text, null)
                PrinterBase.start()
            }
        } ?: kotlin.run {
            Log.e("TEXTO", "ESTÁ VAZIO")
        }
    }

    fun printImagePattern(logo: Int = R.drawable.grupo_1270) {
        val imageBitmap = ConvertLayout.convertResourceInBitmap(context, logo)
        GlobalScope.launch {
            PrinterBase.init()
            PrinterBase.printImageBase(imageBitmap)
            PrinterBase.start()
        }
    }

    fun printImage(image: Bitmap) {

        GlobalScope.launch {
            PrinterBase.init()
            PrinterBase.printImageBase(image)
            PrinterBase.start()
        }

    }
    fun printResumo(string: String, bitmap: Bitmap, size: FontSize = FontSize.NORMAL,
                    spacingLetters: SpacingLetters = SpacingLetters.NORMAL,
                    spacingLines: SpacingLines = SpacingLines.NORMAL,
                    indentLeft: IndentLeft = IndentLeft.TAB_UM,
                    intensity: FontIntensity = FontIntensity.NORMAL){
        GlobalScope.launch {
            PrinterBase.init()
            PrinterBase.setSizeFont(
                ConfigurationFont.getFont(size),
                EFontTypeExtCode.FONT_24_24
            )
            PrinterBase.setSpaceFont(
                ConfigurationFont.getSpaceLetter(spacingLetters).toByte(),
                ConfigurationFont.getSpaceLine(spacingLines).toByte()
            )
            PrinterBase.setLeftIntent(ConfigurationFont.getIndentLeft(indentLeft))
            PrinterBase.setIntentFont(intensity.nivel)
            PrinterBase.printImageBase(bitmap)
            PrinterBase.printStr(string,null)
            PrinterBase.start()
        }

    }

    private fun startPrintReceipt(reciboBitmap: Bitmap) {
        GlobalScope.launch {
            PrinterBase.init()
            PrinterBase.printBitmapWithMonoThreshold(reciboBitmap, 250)
            PrinterBase.step(50)
            PrinterBase.start()
        }
    }


}
