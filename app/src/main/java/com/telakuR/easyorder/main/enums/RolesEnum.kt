package com.telakuR.easyorder.main.enums

import com.telakuR.easyorder.R
import com.telakuR.easyorder.utils.EasyOrder

enum class RolesEnum(val role: String) {
    USER(EasyOrder.getInstance().getString(R.string.user)),
    COMPANY(EasyOrder.getInstance().getString(R.string.company))
}