package id.mifachmi.tesdua.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.mifachmi.tesdua.databinding.FooterTableUangMasukBinding
import id.mifachmi.tesdua.databinding.HeaderTableUangMasukBinding
import id.mifachmi.tesdua.databinding.ItemInsideItemUangMasukBinding
import id.mifachmi.tesdua.databinding.ItemUangMasukBinding
import id.mifachmi.tesdua.model.IncomeViewItem
import id.mifachmi.tesdua.utils.formatRupiah

class DaftarUangMasukLandscapeAdapter(private val data: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CHILD_ITEM = 1
        private const val TYPE_FOOTER = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("DaftarUangMasukLandscapeAdapter", "data: $data")
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
            is ChildItemViewHolder -> holder.bind(data[position] as List<IncomeViewItem.Item>)
            is FooterViewHolder -> holder.bind(data[position] as IncomeViewItem.Footer)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is IncomeViewItem.Header -> TYPE_HEADER
            is List<*> -> TYPE_CHILD_ITEM
            is IncomeViewItem.Footer -> TYPE_FOOTER
            else -> {
                throw IllegalArgumentException("Invalid type of data $position")
            }
        }
    }

    inner class HeaderViewHolder(private val binding: HeaderTableUangMasukBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: IncomeViewItem.Header) {
            binding.tvDate.text = header.date
        }
    }

    inner class ChildItemViewHolder(private val binding: ItemUangMasukBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: List<IncomeViewItem.Item>) {
            binding.apply {
                header?.root?.visibility = View.GONE
                rvItem?.apply {
                    layoutManager =
                        LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                    addItemDecoration(
                        DividerItemDecoration(
                            itemView.context,
                            LinearLayoutManager.VERTICAL
                        )
                    )
                    setHasFixedSize(false)
                    isNestedScrollingEnabled = false
                    adapter = ChildDaftarUangMasukLandscapeAdapter(item)
                }
                footer?.root?.visibility = View.GONE
            }
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
        (holder as ItemViewHolder).bind(data)
    }

    inner class ItemViewHolder(private val binding: ItemInsideItemUangMasukBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: List<IncomeViewItem.Item>) {
            bindItem(item[adapterPosition])
        }

        private fun bindItem(item: IncomeViewItem.Item) {
            binding.apply {
                tvTime?.text = item.time
                tvToWho?.text = item.to
                tvFromWho?.text = item.from
                tvNotes?.text = item.description
                tvAmount?.text = formatRupiah(item.amount)
            }
        }
    }

}