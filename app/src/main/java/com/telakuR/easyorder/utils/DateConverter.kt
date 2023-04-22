package com.telakuR.easyorder.utils

import java.text.SimpleDateFormat
import java.util.*

private const val monthTimeFormat = "MMM dd, HH:MM"

fun getFormattedDate(milliSeconds: Long): String? {
    val formatter = SimpleDateFormat(monthTimeFormat, Locale.US)
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = milliSeconds
    return formatter.format(calendar.time)
}

