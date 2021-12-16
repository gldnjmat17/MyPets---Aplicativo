package br.uea.transirie.mypay.mypaytemplate2.repository.room.dao

import androidx.room.Dao
import androidx.room.Query
import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_CPF_GERENTE
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_EMAIL_GERENTE
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.COLUMN_ID
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.TABLE_ESTABELECIMENTO

@Dao
interface EstabelecimentoDao: BaseDao<Estabelecimento>{

    @Query("""SELECT * FROM $TABLE_ESTABELECIMENTO WHERE $COLUMN_ID = :id""")
    fun estabelecimentoById(id: Long): Estabelecimento

    @Query("""SELECT * FROM $TABLE_ESTABELECIMENTO WHERE $COLUMN_EMAIL_GERENTE = :emailGerente""")
    fun estabelecimentoByEmailGerente(emailGerente: String): Estabelecimento?

    @Query("""SELECT * FROM $TABLE_ESTABELECIMENTO WHERE $COLUMN_CPF_GERENTE = :cpfGerente""")
    fun estabelecimentoByCPFGerente(cpfGerente: String): Estabelecimento?

    @Query("""SELECT * FROM $TABLE_ESTABELECIMENTO""")
    fun getEstabelecimentos(): List<Estabelecimento>

    @Query("SELECT * FROM $TABLE_ESTABELECIMENTO")
    fun getAllEstabelecimentos(): List<Estabelecimento>

    //Faz a consulta do id na tabela
    @Query("SELECT _id FROM $TABLE_ESTABELECIMENTO LIMIT 1")
    fun estabelecimentoIds(): Long
}