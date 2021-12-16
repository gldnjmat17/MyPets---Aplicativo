package br.uea.transirie.mypay.mypaytemplate2.repository.room

import br.uea.transirie.mypay.mypaytemplate2.model.Estabelecimento
import br.uea.transirie.mypay.mypaytemplate2.repository.EstabelecimentoDataSource


class EstabelecimentoRepository(database: AppDatabase): EstabelecimentoDataSource {
    private val estabelecimentoDao = database.estabelecimentoDao()

    //Função que recebe o valor do id do estabelecimento
    override fun estabelecimentoId(): Long =
        estabelecimentoDao.estabelecimentoIds()

    override fun estabelecimentoById(id: Long): Estabelecimento =
        estabelecimentoDao.estabelecimentoById(id)

    override fun estabelecimentoByEmailGerente(emailGerente: String): Estabelecimento? =
        estabelecimentoDao.estabelecimentoByEmailGerente(emailGerente)

    override fun estabelecimentoByCPFGerente(cpfGerente: String): Estabelecimento? {
        return estabelecimentoDao.estabelecimentoByCPFGerente(cpfGerente)
    }
    override fun getEstabelecimentos(): List<Estabelecimento> =
        estabelecimentoDao.getEstabelecimentos()

    override fun save(obj: Estabelecimento) {
        if(obj.id == 0L){
            val id = insert(obj)
            obj.id = id
        } else{
            update(obj)
        }
    }

    override fun insert(obj: Estabelecimento): Long {
        return estabelecimentoDao.insert(obj)
    }

    override fun update(obj: Estabelecimento) {
        return estabelecimentoDao.update(obj)
    }

    override fun delete(obj: Estabelecimento) {
        return estabelecimentoDao.delete(obj)
    }

    fun getAllEstabelecimentos(): List<Estabelecimento> {
        return estabelecimentoDao.getAllEstabelecimentos()
    }
}