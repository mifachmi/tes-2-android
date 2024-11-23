package id.mifachmi.tesdua.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.mifachmi.tesdua.data.local.repository.IncomeReportRepository
import id.mifachmi.tesdua.di.Injection
import id.mifachmi.tesdua.ui.inputuangmasuk.InputUangMasukViewModel
import id.mifachmi.tesdua.ui.main.daftaruangmasuk.DaftarUangMasukViewModel

class ViewModelFactory private constructor(
    private val incomeReportRepository: IncomeReportRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            InputUangMasukViewModel::class.java -> InputUangMasukViewModel(incomeReportRepository) as T
            DaftarUangMasukViewModel::class.java -> DaftarUangMasukViewModel(incomeReportRepository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        fun getInstance(context: Context): ViewModelFactory =
            ViewModelFactory(Injection.provideIncomeReportRepository(context))
    }
}