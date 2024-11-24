package id.mifachmi.tesdua.model

data class Header(val date: String, var amount: Int?)
data class Item(
    var id: Int = 0,
    val from: String,
    val to: String,
    val time: String,
    val description: String,
    val amount: Int,

    val type: String,
    val imageUri: String?,
)

data class Footer(val total: String)

sealed class IncomeViewItem {
    data class Header(val date: String, var amount: Int) : IncomeViewItem()
    data class Item(
        val id: Int = 0,
        val time: String,
        val to: String,
        val from: String,
        val description: String,
        val amount: Int,
        val type: String,
        val imageUri: String?
    ) : IncomeViewItem()
    data class Footer(val total: String) : IncomeViewItem()
}