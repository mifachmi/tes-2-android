package id.mifachmi.tesdua.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "income")
@Parcelize
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "to")
    var to: String? = null,

    @ColumnInfo(name = "from")
    var from: String? = null,

    @ColumnInfo(name = "amount")
    var amount: Int = 0,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "type")
    var type: String? = null,

    @ColumnInfo(name = "image_uri")
    val imageUri: String? = null,

    @ColumnInfo(name = "time")
    var time: String? = null,

    @ColumnInfo(name = "date")
    var date: Long? = null
) : Parcelable
