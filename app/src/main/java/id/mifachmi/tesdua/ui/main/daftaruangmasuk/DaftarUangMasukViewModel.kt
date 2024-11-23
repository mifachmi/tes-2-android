package id.mifachmi.tesdua.ui.main.daftaruangmasuk

import androidx.lifecycle.ViewModel
import id.mifachmi.tesdua.data.local.repository.IncomeReportRepository

class DaftarUangMasukViewModel(private val repository: IncomeReportRepository) : ViewModel() {
    fun getAllData() = repository.getAllData()

    fun delete(transactionId: Int) = repository.delete(transactionId)

    fun getDataByDateRange(startDate: Long, endDate: Long) =
        repository.getDataByDateRange(startDate, endDate)
}