package id.mifachmi.tesdua.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.mifachmi.tesdua.databinding.HeaderTableUangMasukBinding
import id.mifachmi.tesdua.databinding.ItemUangMasukBinding
import id.mifachmi.tesdua.model.IncomeViewItem
import id.mifachmi.tesdua.utils.formatRupiah

class DaftarUangMasukPortraitAdapter(
    private val data: List<Any>,
    private val onEditClickListener: (IncomeViewItem.Item) -> Unit,
    private val onDeleteClickListener: (Int) -> Unit,
    private val onShowPhotoClickListener: (IncomeViewItem.Item) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
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

            TYPE_ITEM -> {
                val binding = ItemUangMasukBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ItemViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(data[position] as IncomeViewItem.Header)
            is ItemViewHolder -> holder.bind(data[position] as IncomeViewItem.Item)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position] is IncomeViewItem.Header) TYPE_HEADER else TYPE_ITEM
    }

    inner class HeaderViewHolder(private val binding: HeaderTableUangMasukBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(dataHeader: IncomeViewItem.Header) {
            println(dataHeader)
            binding.apply {
                tvDate.text = dataHeader.date
                tvAmount?.text = formatRupiah(dataHeader.amount)
            }
        }
    }

    inner class ItemViewHolder(private val binding: ItemUangMasukBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(dataItem: IncomeViewItem.Item) {
            println(dataItem)
            binding.apply {
                tvFromWhoToWho?.text = "Dari ${dataItem.from} ke ${dataItem.to}"
                tvTime?.text = dataItem.time
                tvNotes?.text = dataItem.description
                tvAmount?.text = formatRupiah(dataItem.amount)

                btnEdit?.setOnClickListener {
                    onEditClickListener(dataItem)
                }

                btnDelete?.setOnClickListener {
                    onDeleteClickListener(dataItem.id)
                    notifyItemRemoved(adapterPosition)
                }

                if (dataItem.imageUri == "null") {
                    btnShowPhoto?.visibility = View.GONE
                }

                btnShowPhoto?.setOnClickListener {
                    onShowPhotoClickListener(dataItem)
                }
            }
        }
    }
}