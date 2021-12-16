package br.uea.transirie.mypay.mypaytemplate2.repository.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import br.uea.transirie.mypay.mypaytemplate2.model.*
import br.uea.transirie.mypay.mypaytemplate2.repository.room.converters.TipoPagamentoConverter
import br.uea.transirie.mypay.mypaytemplate2.repository.room.dao.*
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.DATABASE_NAME
import br.uea.transirie.mypay.mypaytemplate2.repository.sqlite.DATABASE_VERSION
import org.jetbrains.anko.doAsync

@Database(entities = [
    Servico::class,
    Pagamento::class,
    ItemAtendimento::class,
    Usuario::class,
    Cliente::class,
    Atendimento::class,
    Estoque::class,
    Brinde::class,
    Meta::class,
    ItemVenda::class,
    Venda::class,
    Despesa::class,
    Estabelecimento::class
], version = DATABASE_VERSION, exportSchema = false)
@TypeConverters(TipoPagamentoConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun servicoDao(): ServicoDao
    abstract fun pagamentoDao(): PagamentoDao
    abstract fun itemAtendimentoDao(): ItemAtendimentoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun clienteDao(): ClienteDao
    abstract fun atendimentoDao(): AtendimentoDao
    abstract fun estoqueDao(): EstoqueDao
    abstract fun brindeDao(): BrindeDao
    abstract fun metaDao(): MetaDao
    abstract fun itemVendaDao(): ItemVendaDao
    abstract fun vendaDao(): VendaDao
    abstract fun despesaDao(): DespesaDao
    abstract fun estabelecimentoDao(): EstabelecimentoDao

    companion object {
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if(instance == null) {
                synchronized(this) {
                    instance =
                            Room.databaseBuilder(
                                    context.applicationContext,
                                    AppDatabase::class.java,
                                    DATABASE_NAME
                            )
                                    .addCallback(object: Callback(){
                                        override fun onCreate(db: SupportSQLiteDatabase) {
                                            super.onCreate(db)
                                            doAsync {
                                                PREPOPULATE_SERVICO.forEach {
                                                    getDatabase(context).servicoDao().insert(it)
                                                }
                                                PREPOPULATE_ATENDIMENTO.forEach {
                                                    getDatabase(context).atendimentoDao().insert(it)
                                                }
                                            }
                                        }
                                    }).build()
                }
            }
            return instance as AppDatabase
        }

        val PREPOPULATE_SERVICO = listOf(
                Servico(1, "Lavagem Simples", 15f),
                Servico(2, "Lavagem Especial", 20f),
        )


        val PREPOPULATE_ATENDIMENTO = listOf(
                Atendimento(
                        1, 1,
                        "5/9/2000", "8/4/7899",
                        "9/9/2020", 60.50f
                ),
                Atendimento(
                        2, 1,
                        "9/5/2000", "8/4/2021",
                        "19/9/2020", 100f
                )
        )


        fun destroyInstance() {
            instance = null
        }
    }
}