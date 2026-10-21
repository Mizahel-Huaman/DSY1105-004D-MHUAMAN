package com.example.insertcode.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UsuarioEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        // @Volatile: los cambios son visibles de inmediato para todos los hilos
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton con doble verificación (thread-safe)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // MIGRATION (ejemplo para una versión 2 futura, p. ej. agregar columna "telefono"):
                    // val MIGRATION_1_2 = object : Migration(1, 2) {
                    //     override fun migrate(db: SupportSQLiteDatabase) {
                    //         db.execSQL("ALTER TABLE usuarios ADD COLUMN telefono TEXT")
                    //     }
                    // }
                    // y luego: .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
