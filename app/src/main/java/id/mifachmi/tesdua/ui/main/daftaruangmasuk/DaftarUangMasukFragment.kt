package id.mifachmi.tesdua.ui.main.daftaruangmasuk

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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

        binding.btnCreateTransaction.setOnClickListener {
            goToFragmentInputUangMasuk()
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

            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
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

    private fun mapToViewLandscape(dataIncome: List<IncomeEntity>): List<IncomeViewItem> {
        if (dataIncome.isEmpty()) return emptyList()

        return dataIncome
            .groupBy { it.date }
            .flatMap { (date, transactions) ->
                buildTransactionGroup(date.toString(), transactions)
            }
    }

    private fun buildTransactionGroup(
        date: String?,
        transactions: List<IncomeEntity>
    ): List<IncomeViewItem> {
        if (date == null) return emptyList()

        val groupItems = mutableListOf<IncomeViewItem>()
        val totalAmount = transactions.sumOf { it.amount }

        // Add header
        groupItems.add(
            IncomeViewItem.Header(
                date = convertTimestampToString(date.toLong()),
                amount = totalAmount
            )
        )

        // Add transaction items
        transactions.forEach { transaction ->
            groupItems.add(
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
            )
        }

        // Add footer
        groupItems.add(
            IncomeViewItem.Footer(
                total = formatRupiah(totalAmount)
            )
        )

        return groupItems
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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}