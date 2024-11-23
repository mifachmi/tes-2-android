package id.mifachmi.tesdua.ui.inputuangmasuk.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.mifachmi.tesdua.databinding.SheetIncomeTypeInfoBinding

class IncomeTypeBottomSheet : BottomSheetDialogFragment() {
    private var _binding: SheetIncomeTypeInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SheetIncomeTypeInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}