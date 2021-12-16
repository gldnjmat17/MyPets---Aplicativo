package br.uea.transirie.mypay.mypaytemplate2.utils

import br.uea.transirie.mypay.mypaytemplate2.repository.room.AppDatabase
import br.uea.transirie.mypay.mypaytemplate2.repository.room.UsuarioRepository

class MyPinGenerator(appDatabase: AppDatabase) {
    private val usuarioRepository = UsuarioRepository(appDatabase)
    private val start = 1000
    private val end = 9999

    fun newPin(): Int {
        val pin = rand()*100

        val pinJaEmUso = usuarioRepository.pinJaEmUso(pin)

        return if (pinJaEmUso) newPin() else pin
    }

    private fun rand(): Int {
        return (start..end).random()
    }
}