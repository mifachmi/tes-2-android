package id.mifachmi.tesdua.utils

import android.annotation.SuppressLint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Format a number into Rupiah currency
fun formatRupiah(value: Int): String {
    val locale = Locale("id", "ID")
    return NumberFormat.getCurrencyInstance(locale).apply {
        maximumFractionDigits = 0
    }.format(value)
}

// Get the current time in HH:mm:ss format
@SuppressLint("NewApi")
fun getCurrentTime(): String {
    return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
}

// Convert a timestamp to a formatted date string
fun convertTimestampToString(timestamp: Long, format: String = "dd MMMM yyyy"): String {
    return SimpleDateFormat(format, Locale.US).format(Date(timestamp))
}

// Get the start of the day in milliseconds for a given date
fun getStartOfDayInMillis(date: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

// Get the end of the day in milliseconds for a given date
fun getEndOfDayInMillis(date: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = date
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}
