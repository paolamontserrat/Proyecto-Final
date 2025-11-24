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
import com.example.notasymedia.data.entity.NotaEntity
import com.example.notasymedia.data.entity.MultimediaEntity

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

@Database(
    entities = [NotaEntity::class, MultimediaEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notaDao(): NotaDao
    abstract fun multimediaDao(): MultimediaDao

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
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
