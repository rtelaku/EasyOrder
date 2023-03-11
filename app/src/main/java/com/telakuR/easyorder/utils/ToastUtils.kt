package com.telakuR.easyorder.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {
    fun showToast(
        context: Context = EasyOrder.getInstance().applicationContext,
        messageId: Int,
        length: Int
    ) {
        val message = context.getString(messageId)
        Toast.makeText(context, message, length).show()
    }
}