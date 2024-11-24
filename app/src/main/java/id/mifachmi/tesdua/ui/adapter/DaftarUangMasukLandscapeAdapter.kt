package id.mifachmi.tesdua.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.mifachmi.tesdua.R
import id.mifachmi.tesdua.databinding.FooterTableUangMasukBinding
import id.mifachmi.tesdua.databinding.HeaderTableUangMasukBinding
import id.mifachmi.tesdua.databinding.ItemInsideItemUangMasukBinding
import id.mifachmi.tesdua.databinding.ItemUangMasukBinding
import id.mifachmi.tesdua.model.IncomeViewItem
import id.mifachmi.tesdua.utils.formatRupiah

class DaftarUangMasukLandscapeAdapter(private val data: List<IncomeViewItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CHILD_ITEM = 1
        private const val TYPE_FOOTER = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = HeaderTableUangMasukBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                HeaderViewHolder(binding)
            }

            TYPE_CHILD_ITEM -> {
                val binding = ItemUangMasukBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ChildItemViewHolder(binding)
            }

            TYPE_FOOTER -> {
                val binding = FooterTableUangMasukBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FooterViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(data[position] as IncomeViewItem.Header)
            is ChildItemViewHolder -> holder.bind(data[position] as IncomeViewItem.Item)
            is FooterViewHolder -> holder.bind(data[position] as IncomeViewItem.Footer)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is IncomeViewItem.Header -> TYPE_HEADER
            is IncomeViewItem.Item -> TYPE_CHILD_ITEM
            is IncomeViewItem.Footer -> TYPE_FOOTER
        }
    }

    inner class HeaderViewHolder(private val binding: HeaderTableUangMasukBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: IncomeViewItem.Header) {
            binding.tvDate.text = header.date
        }
    }

    inner class ChildItemViewHolder(binding: ItemUangMasukBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(item: IncomeViewItem.Item) {
            val childRv: RecyclerView = itemView.findViewById(R.id.rvItem)
            childRv.apply {
                layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
            }
            childRv.adapter = ChildDaftarUangMasukLandscapeAdapter(listOf(item))
        }
    }

    inner class FooterViewHolder(private val binding: FooterTableUangMasukBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(footer: IncomeViewItem.Footer) {
            binding.tvTotal?.text = footer.total
        }
    }
}

class ChildDaftarUangMasukLandscapeAdapter(private val data: List<IncomeViewItem.Item>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemInsideItemUangMasukBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bind(data[position])
    }

    inner class ItemViewHolder(private val binding: ItemInsideItemUangMasukBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: IncomeViewItem.Item) {
            binding.tvTime?.text = item.time
            binding.tvToWho?.text = item.to
            binding.tvFromWho?.text = item.from
            binding.tvNotes?.text = item.description
            binding.tvAmount?.text = formatRupiah(item.amount)
        }
    }

}