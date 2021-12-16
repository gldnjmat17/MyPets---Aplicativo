package br.uea.transirie.mypay.mypaytemplate2.utils.libprinter.base

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import br.transire.payment_easy_library.enums.printer.StatusPrinter
import com.pax.dal.IDAL
import com.pax.dal.IPrinter
import com.pax.dal.entity.EFontTypeAscii
import com.pax.dal.entity.EFontTypeExtCode
import com.pax.dal.exceptions.PrinterDevException
import com.pax.neptunelite.api.NeptuneLiteUser

object PrinterBase {

    private val TAG = "IMPRESSORA-BASE"

    private var dal: IDAL? = null
    private var neptuneLiteUser: NeptuneLiteUser? = null
    private var iPrinter: IPrinter? = null

    fun setupPrinter(context: Context) {
        neptuneLiteUser = NeptuneLiteUser.getInstance()
        neptuneLiteUser?.let {
            dal = neptuneLiteUser!!.getDal(context)
        }
        dal?.let {
            iPrinter = dal!!.printer
        }

    }

    fun printBitmapWithMonoThreshold(bitmap: Bitmap, grayThreshold: Int) {
        try {
            iPrinter?.printBitmapWithMonoThreshold(bitmap, grayThreshold)
        } catch (e: PrinterDevException) {
            Log.e(TAG, "NÃO FOI POSSÍVEL REALIZAR IMPRESSÃO")
        }
    }


    fun step(pixel: Int) {
        try {
            iPrinter?.step(pixel)
        } catch (e: PrinterDevException) {
            Log.e(TAG, "PROBLEMAS COM STEP")
        }
    }

    fun init() {
        try {
            iPrinter?.init()
        } catch (ex: PrinterDevException) {
            Log.e(TAG, "NÃO FOI POSSÍVEL INICIAR IMPRESSÃO")
        }
    }

    fun start() {
        try {
            iPrinter?.start()
        } catch (ex: PrinterDevException) {
            Log.e(TAG, "NÃO FOI POSSÍVEL STARTAR IMPRESSÃO")
        }
    }

    fun printStr(string1: String, string2: String?) {
        try {
            iPrinter?.printStr(string1, string2)
        } catch (ex: PrinterDevException) {
            Log.e(TAG, "NÃO FOI POSSÍVEL REALIZAR IMPRESSÃO")
        }
    }

    fun printImageBase(image: Bitmap) {
        try {
            iPrinter?.printBitmap(image)
        } catch (e: PrinterDevException) {
            Log.e(TAG, "NÃO FOI POSSÍVEL REALIZAR IMPRESSÃO")
        }
    }

    fun setSizeFont(asciiFontType: EFontTypeAscii, cFontType: EFontTypeExtCode) {

        try {
            iPrinter?.fontSet(asciiFontType, cFontType)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
            Log.e("SETFONT", e.printStackTrace().toString())
        }
    }

    fun setSpaceFont(wordSpace: Byte, lineSpace: Byte) {
        try {
            iPrinter?.spaceSet(wordSpace, lineSpace)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
            Log.e("SETFONT", e.printStackTrace().toString())
        }
    }

    fun setLeftIntent(intent: Int) {
        try {
            iPrinter?.leftIndent(intent)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
            Log.e("SETFONT", e.printStackTrace().toString())
        }
    }

    fun setIntentFont(intent: Int) {
        try {
            iPrinter?.setGray(intent)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
            Log.e("SETFONT", e.printStackTrace().toString())
        }

    }


    fun getStatus(): StatusPrinter {
        return try {
            val status = iPrinter?.status
            var result = 300
            status?.let {
                result = it
            }
            getStatusPrinterMapper(result)

        } catch (e: PrinterDevException) {
             getStatusPrinterMapper(300)
        }

    }




    private fun getStatusPrinterMapper(status: Int) =
        when (status) {
            0 -> StatusPrinter.SUCCESS
            1 -> StatusPrinter.PRINTER_IS_BUSY
            2 -> StatusPrinter.OUT_OF_PAPER
            3 -> StatusPrinter.THE_FORMAT_OF_PRINT_DATA_PACKER_ERROR
            4 -> StatusPrinter.PRINTER_MALFUNCTIONS
            8 -> StatusPrinter.PRINTER_OVER_HEATS
            9 -> StatusPrinter.VOLTAGE_IS_TOO_LOW
            240 -> StatusPrinter.PRINTING_IS_UNFINISHED
            252 -> StatusPrinter.NOT_INSTALLED_FONT_LIBRARY
            254 -> StatusPrinter.DATA_PACKAGE_IS_TOO_LONG
            else -> StatusPrinter.PRINTING_IS_UNFINISHED
        }


}