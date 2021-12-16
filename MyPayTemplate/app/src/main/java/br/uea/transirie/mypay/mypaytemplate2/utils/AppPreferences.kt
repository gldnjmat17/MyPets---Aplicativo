package br.uea.transirie.mypay.mypaytemplate2.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import br.uea.transirie.mypay.mypaytemplate2.R

object AppPreferences {

    private const val logtag = "APP_PREFERENCES"

    fun getPIN(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.PREF_USER_DATA),
            Context.MODE_PRIVATE
        )

        return sharedPreferences.getInt(context.getString(R.string.PREF_PIN), 0)
    }
    fun getUltimoUpload(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.PREF_USER_DATA),
            Context.MODE_PRIVATE
        )

        val ultimoUpload = sharedPreferences.getString(
            context.getString(R.string.PREF_USER_ULTIMO_UPLOAD),
            context.getString(R.string.ultimo_upload_sem_data)
        )

        Log.d(logtag, "Último upload: $ultimoUpload")

        return ultimoUpload.toString()
    }
    fun setUltimoUpload(ultimoUpload: String, context: Context) {
        put(context) {
            it.putString(context.getString(R.string.PREF_USER_ULTIMO_UPLOAD), ultimoUpload)
        }
    }
    fun putCPF(cpf: String, context: Context) {
        put(context) { editor ->
            editor.putString(
                context.getString(R.string.PREF_USER_GERENTE_CPF), cpf
            )
        }
    }
    fun putPin(pin: Int, context: Context) {
        put(context) { editor ->
            editor.putInt(context.getString(R.string.PREF_PIN), pin)
        }
    }
    fun getCPFGerente(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.PREF_USER_DATA),
            Context.MODE_PRIVATE
        )

        val cpf = sharedPreferences.getString(
            context.getString(R.string.PREF_USER_GERENTE_CPF),
            context.getString(R.string.cpf_desconhecido)
        )

        Log.d(logtag, "CPF: $cpf")

        return cpf.toString()
    }
    fun clearPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.PREF_USER_DATA),
            Context.MODE_PRIVATE
        )

        sharedPreferences.edit().clear().apply()
    }
    fun setUserLogado(status: Boolean, context: Context) {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.PREF_USER_DATA),
            Context.MODE_PRIVATE
        )

        sharedPreferences.edit()
            .putBoolean(context.getString(R.string.PREF_USER_LOGADO), status)
            .apply()
    }

    private fun put(context: Context, callback: (SharedPreferences.Editor) -> Unit) {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.PREF_USER_DATA),
            Context.MODE_PRIVATE
        )

        val editor = sharedPreferences.edit()
        callback(editor)
        editor.apply()
    }
}