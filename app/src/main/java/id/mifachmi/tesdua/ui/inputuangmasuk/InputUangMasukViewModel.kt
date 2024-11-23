package id.mifachmi.tesdua.ui.inputuangmasuk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.mifachmi.tesdua.data.local.entity.IncomeEntity
import id.mifachmi.tesdua.data.local.repository.IncomeReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InputUangMasukViewModel(private val repository: IncomeReportRepository) : ViewModel() {
    fun insertData(
        time: String,
        to: String,
        from: String,
        description: String,
        amount: Int,
        date: Long,
        type: String,
        imageUri: String,
    ) {
        val incomeEntity = IncomeEntity(
            time = time,
            to = to,
            from = from,
            description = description,
            amount = amount,
            date = date,
            type = type,
            imageUri = imageUri,
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(incomeEntity)
        }
    }

    fun updateData(data: IncomeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(data)
        }
    }
}