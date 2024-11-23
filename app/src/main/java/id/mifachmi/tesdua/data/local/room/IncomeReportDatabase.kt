package id.mifachmi.tesdua.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.mifachmi.tesdua.data.local.entity.IncomeEntity

@Database(entities = [IncomeEntity::class], version = 1)
abstract class IncomeReportDatabase : RoomDatabase() {

    abstract fun dao(): IncomeDao

    companion object {
        @Volatile
        private var INSTANCE: IncomeReportDatabase? = null

        fun getInstance(context: Context): IncomeReportDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                IncomeReportDatabase::class.java,
                "income.db"
            ).build()
    }
}
