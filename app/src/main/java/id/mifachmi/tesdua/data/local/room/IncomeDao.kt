package id.mifachmi.tesdua.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import id.mifachmi.tesdua.data.local.entity.IncomeEntity

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(data: IncomeEntity)

    @Update
    suspend fun update(data: IncomeEntity)

    @Query("DELETE FROM `income` WHERE id = :id")
    fun deleteDataById(id: Int)

    @Query("SELECT * from `income` ORDER BY date ASC")
    fun getAllData(): LiveData<List<IncomeEntity>>

    @Query("SELECT * FROM `income` WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getDataByDateRange(
        startDate: Long,
        endDate: Long
    ): LiveData<List<IncomeEntity>>
}