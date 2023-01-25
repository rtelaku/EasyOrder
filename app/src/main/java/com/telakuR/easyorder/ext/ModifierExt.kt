package com.telakuR.easyorder.ext

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.spacer(): Modifier {
    return this.fillMaxWidth().padding(20.dp)
}