package id.mifachmi.tesdua.di

import android.content.Context
import id.mifachmi.tesdua.data.local.repository.IncomeReportRepository
import id.mifachmi.tesdua.data.local.room.IncomeReportDatabase
import id.mifachmi.tesdua.utils.AppExecutor

object Injection {
    fun provideIncomeReportRepository(context: Context): IncomeReportRepository {
        val db = IncomeReportDatabase.getInstance(context)
        val dao = db.dao()
        val appExecutors = AppExecutor()

        return IncomeReportRepository.getInstance(dao, appExecutors)
    }
}