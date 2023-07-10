package com.telakuR.easyorder.main.ext

import android.util.Patterns

private const val TWO_DECIMAL_FORMAT = "%.2f"

fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun twoDecimalNumber(number: Double): String {
    return String.format(TWO_DECIMAL_FORMAT, number)
}

