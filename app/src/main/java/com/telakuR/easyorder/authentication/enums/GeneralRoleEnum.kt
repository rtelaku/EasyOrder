package com.telakuR.easyorder.authentication.enums

import com.telakuR.easyorder.R
import com.telakuR.easyorder.utils.EasyOrder

enum class GeneralRoleEnum(val role: String) {
    USER(EasyOrder.getInstance().getString(R.string.user)),
    BUSINESS(EasyOrder.getInstance().getString(R.string.bussines))
}