package id.mifachmi.tesdua.data.local.repository

import androidx.lifecycle.LiveData
import id.mifachmi.tesdua.data.local.entity.IncomeEntity
import id.mifachmi.tesdua.data.local.room.IncomeDao
import id.mifachmi.tesdua.utils.AppExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IncomeReportRepository private constructor(
    private val dao: IncomeDao,
    private val appExecutors: AppExecutor
) {
    suspend fun insert(entity: IncomeEntity) {
        withContext(Dispatchers.IO) {
            dao.insert(entity)
        }
    }

    fun getAllData(): LiveData<List<IncomeEntity>> {
        return dao.getAllData()
    }

    fun delete(id: Int) {
        appExecutors.diskIO.execute {
            dao.deleteDataById(id)
        }
    }

    suspend fun update(data: IncomeEntity) {
        withContext(Dispatchers.IO) {
            dao.update(data)
        }
    }

    fun getDataByDateRange(
        startDate: Long,
        endDate: Long
    ): LiveData<List<IncomeEntity>> {
        return dao.getDataByDateRange(startDate, endDate)
    }

    companion object {
        fun getInstance(
            dao: IncomeDao,
            appExecutors: AppExecutor,
        ): IncomeReportRepository {
            return IncomeReportRepository(dao, appExecutors)
        }
    }
}