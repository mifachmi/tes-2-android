package id.mifachmi.tesdua.ui.main.daftaruangmasuk

import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.util.Pair
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.mifachmi.tesdua.R
import id.mifachmi.tesdua.data.local.entity.IncomeEntity
import id.mifachmi.tesdua.databinding.FragmentDaftarUangMasukBinding
import id.mifachmi.tesdua.model.IncomeViewItem
import id.mifachmi.tesdua.ui.adapter.DaftarUangMasukLandscapeAdapter
import id.mifachmi.tesdua.ui.adapter.DaftarUangMasukPortraitAdapter
import id.mifachmi.tesdua.ui.inputuangmasuk.InputUangMasukFragment
import id.mifachmi.tesdua.utils.ViewModelFactory
import id.mifachmi.tesdua.utils.convertTimestampToString
import id.mifachmi.tesdua.utils.formatRupiah

class DaftarUangMasukFragment : Fragment() {

    private var _binding: FragmentDaftarUangMasukBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DaftarUangMasukViewModel> {
        ViewModelFactory.getInstance(
            requireContext()
        )
    }

    private var startDate: Long = 0
    private var endDate: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDaftarUangMasukBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateData()
        setupDatePicker()

        binding.btnCreateTransaction.setOnClickListener {
            goToFragmentInputUangMasuk()
        }
    }

    private fun setupDatePicker() {
        binding.ivDatePicker.setOnClickListener {
            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select dates")
                    .setSelection(
                        Pair(
                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
                            MaterialDatePicker.todayInUtcMilliseconds()
                        )
                    )
                    .build()

            dateRangePicker.show(parentFragmentManager, dateRangePicker.toString())
            dateRangePicker.addOnPositiveButtonClickListener {
                startDate = dateRangePicker.selection?.first ?: 0
                endDate = dateRangePicker.selection?.second ?: 0

                val strStartDate = convertTimestampToString(dateRangePicker.selection?.first ?: 0)
                val strEndDate = convertTimestampToString(dateRangePicker.selection?.second ?: 0)
                binding.tvDateRange.text = "$strStartDate - $strEndDate"

                filterDataByDateRange()
            }
        }
    }

    private fun filterDataByDateRange() {
        viewModel.getDataByDateRange(startDate, endDate)
            .observe(viewLifecycleOwner) { data ->
                if (data.isEmpty()) {
                    binding.rvIncomeList.visibility = View.GONE
                    binding.tvEmptyData.visibility = View.VISIBLE
                } else {
                    binding.rvIncomeList.visibility = View.VISIBLE
                    binding.tvEmptyData.visibility = View.GONE
                    showUangMasukRvPortrait(data)
                }
            }
    }

    private fun populateData() {
        viewModel.getAllData().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.rvIncomeList.visibility = View.GONE
                binding.tvEmptyData.visibility = View.VISIBLE
            } else {
                binding.rvIncomeList.visibility = View.VISIBLE
                binding.tvEmptyData.visibility = View.GONE
                showUangMasukRvPortrait(it)
            }
        }
    }

    private fun showUangMasukRvPortrait(data: List<IncomeEntity>) {
        val orientation = resources.configuration.orientation

        binding.rvIncomeList.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                val mappedData = mapToView(data)
                val rvAdapter = DaftarUangMasukPortraitAdapter(
                    mappedData,
                    onEditClickListener = { item ->
                        goToDetail(item)
                    },
                    onDeleteClickListener = { itemId ->
                        viewModel.delete(itemId)
                    },
                    onShowPhotoClickListener = { item ->
                        showImageDialog(item.imageUri!!)
                    },
                )

                adapter = rvAdapter
            } else {
                val mappedData = mapToViewLandscape(data)
                val rvAdapter = DaftarUangMasukLandscapeAdapter(mappedData)
                adapter = rvAdapter
            }
        }
    }

    private fun mapToView(dataIncome: List<IncomeEntity>): List<IncomeViewItem> {
        return dataIncome.groupBy { convertTimestampToString(it.date!!) }
            .flatMap { (date, transactionsForDate) ->
                val totalAmount = transactionsForDate.sumOf { it.amount }
                listOf(IncomeViewItem.Header(date, totalAmount)) +
                        transactionsForDate.map { transaction ->
                            IncomeViewItem.Item(
                                id = transaction.id,
                                time = transaction.time!!,
                                to = transaction.to!!,
                                from = transaction.from!!,
                                description = transaction.description!!,
                                amount = transaction.amount,
                                type = transaction.type!!,
                                imageUri = transaction.imageUri,
                            )
                        }
            }
    }

    private fun mapToViewLandscape(dataIncome: List<IncomeEntity>): List<Any> {
        if (dataIncome.isEmpty()) return emptyList()

        val groupedByDate = dataIncome.groupBy { convertTimestampToString(it.date!!) }
        val keyData = groupedByDate.keys.first()
        val valueData = groupedByDate.values.flatten()
        val mappedValueData = buildTransactionGroup(keyData, valueData)
        Log.d("mapToViewLandscape 3", "mapToViewLandscape 3: $mappedValueData")

        val newMappedValueData = mapToNestedStructure(mappedValueData)
        Log.d("mapToViewLandscape 4", "mapToViewLandscape 4: $newMappedValueData")

        return newMappedValueData
//        return dataIncome
//            .groupBy { it.date }
//            .flatMap { (date, transactions) ->
//                buildTransactionGroup(date.toString(), transactions)
//            }
    }

    private fun buildTransactionGroup(
        date: String?,
        transactions: List<IncomeEntity>
    ): List<IncomeViewItem> {
        if (date == null) return emptyList()

        val combinedData = mutableListOf<IncomeViewItem>()
        val groupItems = mutableListOf<IncomeViewItem.Item>()
        val totalAmount = transactions.sumOf { it.amount }

        // Add header
        val headerData = IncomeViewItem.Header(
            date = date,
            amount = transactions.sumOf { it.amount }
        )

        // Add transaction items
        groupItems.addAll(transactions.map { transaction ->
            IncomeViewItem.Item(
                id = transaction.id,
                time = transaction.time ?: "",
                to = transaction.to ?: "",
                from = transaction.from ?: "",
                description = transaction.description ?: "",
                amount = transaction.amount,
                type = transaction.type ?: "",
                imageUri = transaction.imageUri
            )
        })

        // Add footer
        val footerData = IncomeViewItem.Footer(
            total = formatRupiah(totalAmount)
        )

        combinedData.apply {
            add(headerData)
            addAll(groupItems)
            add(footerData)
        }
        return combinedData
    }

    private fun mapToNestedStructure(data: List<Any>): List<Any> {
        val header = data.firstOrNull { it is IncomeViewItem.Header } as? IncomeViewItem.Header
        val items = data.filterIsInstance<IncomeViewItem.Item>()
        val footer = data.lastOrNull { it is IncomeViewItem.Footer } as? IncomeViewItem.Footer

        return listOfNotNull(header, items.ifEmpty { null }, footer)
    }

    private fun goToDetail(item: IncomeViewItem.Item) {
        val fragment = InputUangMasukFragment(isEdit = true).apply {
            arguments = Bundle().apply {
                putInt("id", item.id)
                putString("time", item.time)
                putString("to", item.to)
                putString("from", item.from)
                putString("description", item.description)
                putInt("amount", item.amount)
                putString("type", item.type)
                putString("imageUri", item.imageUri)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container_view,
                fragment,
                InputUangMasukFragment::class.java.simpleName
            )
            .addToBackStack(null)
            .commit()
    }

    private fun goToFragmentInputUangMasuk() {
        val fragment = InputUangMasukFragment(isEdit = false)
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_view, fragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun showImageDialog(imageUri: String) {
        println("imageUri: $imageUri")
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_image, null)
        val ivPhoto = dialogView.findViewById<ImageView>(R.id.ivImage)
        val resolver = requireContext().contentResolver
        val uri = Uri.parse(imageUri)

        resolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val bitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        ivPhoto.setImageBitmap(bitmap)

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}