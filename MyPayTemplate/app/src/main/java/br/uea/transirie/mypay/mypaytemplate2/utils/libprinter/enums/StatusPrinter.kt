package br.transire.payment_easy_library.enums.printer

enum class StatusPrinter(val codeStatus: Int) {
    SUCCESS(0),
    PRINTER_IS_BUSY(1),
    OUT_OF_PAPER(2),
    THE_FORMAT_OF_PRINT_DATA_PACKER_ERROR(3),
    PRINTER_MALFUNCTIONS(4),
    PRINTER_OVER_HEATS(8),
    VOLTAGE_IS_TOO_LOW(9),
    PRINTING_IS_UNFINISHED(240),
    NOT_INSTALLED_FONT_LIBRARY(252),
    DATA_PACKAGE_IS_TOO_LONG(254)
}
