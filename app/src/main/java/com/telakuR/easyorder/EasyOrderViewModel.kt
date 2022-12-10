package com.telakuR.easyorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telakuR.easyorder.authentication.models.services.LogService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class EasyOrderViewModel(private val logService: LogService) : ViewModel() {
    fun launchCatching(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                logService.logNonFatalCrash(throwable)
            },
            block = block
        )
}