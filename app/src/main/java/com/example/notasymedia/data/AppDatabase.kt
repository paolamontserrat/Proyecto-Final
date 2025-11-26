package com.example.notasymedia.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.example.notasymedia.data.dao.NotaDao
import com.example.notasymedia.data.dao.MultimediaDao
import com.example.notasymedia.data.dao.RecordatorioDao
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.MultimediaEntity
import com.example.notasymedia.data.entity.RecordatorioEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notas_table ADD COLUMN tipoMultimedia TEXT")
        db.execSQL("ALTER TABLE notas_table ADD COLUMN multimediaUri TEXT")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `multimedia_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `notaId` INTEGER NOT NULL, `tipo` TEXT NOT NULL, `uri` TEXT NOT NULL, `descripcion` TEXT NOT NULL, FOREIGN KEY(`notaId`) REFERENCES `notas_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_multimedia_table_notaId` ON `multimedia_table` (`notaId`)")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `recordatorios_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `notaId` INTEGER NOT NULL, `fechaHora` INTEGER NOT NULL, `activo` INTEGER NOT NULL, FOREIGN KEY(`notaId`) REFERENCES `notas_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recordatorios_table_notaId` ON `recordatorios_table` (`notaId`)")
    }
}

@Database(
    entities = [NotaEntity::class, MultimediaEntity::class, RecordatorioEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notaDao(): NotaDao
    abstract fun multimediaDao(): MultimediaDao
    abstract fun recordatorioDao(): RecordatorioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "notas_database"
                    )
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                    instance
                } catch (e: Exception) {
                    android.util.Log.e("AppDatabase", "Error creating DB", e)
                    throw e
                }
            }
        }
    }
}
