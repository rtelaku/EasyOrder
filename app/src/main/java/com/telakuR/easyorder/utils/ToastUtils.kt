package com.telakuR.easyorder.utils

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ToastUtils {
    fun showToast(
        context: Context = EasyOrder.getInstance().applicationContext,
        messageId: Int,
        length: Int
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val message = context.getString(messageId)
            Toast.makeText(context, message, length).show()
        }
    }
}