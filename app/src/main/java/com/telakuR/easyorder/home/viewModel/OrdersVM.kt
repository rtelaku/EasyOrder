package com.telakuR.easyorder.home.viewModel

import com.telakuR.easyorder.home.models.FastFood
import com.telakuR.easyorder.home.repository.HomeRepository
import com.telakuR.easyorder.mainViewModel.EasyOrderViewModel
import com.telakuR.easyorder.modules.IoDispatcher
import com.telakuR.easyorder.services.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OrdersVM @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val homeRepository: HomeRepository,
    logService: LogService
) : EasyOrderViewModel(logService) {

    private val _fastFoods = MutableStateFlow<List<FastFood>>(emptyList())
    val fastFoods: StateFlow<List<FastFood>> get() = _fastFoods

    fun getFastFoods() {
        launchCatching {
            withContext(ioDispatcher) {
                homeRepository.getFastFoods()
            }.collect {
                _fastFoods.value = it
            }
        }
    }

}