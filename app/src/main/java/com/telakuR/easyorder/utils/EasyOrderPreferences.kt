package com.telakuR.easyorder.utils

import android.content.Context

object EasyOrderPreferences {

    private val prefs = EasyOrder.getInstance().getSharedPreferences("EasyOrderPrefs", Context.MODE_PRIVATE)

    fun saveRequestedCompanyId(companyId: String) {
        prefs.edit()?.putString(Constants.REQUESTED_COMPANY_ID, companyId)?.apply()
    }

    fun getRequestedCompanyId(): String {
        return prefs.getString(Constants.REQUESTED_COMPANY_ID, "")!!
    }

    fun saveCurrentDeviceToken(token: String) {
        prefs.edit()?.putString(Constants.TOKEN, token)?.apply()
    }

    fun getCurrentDeviceToken(): String {
        return prefs.getString(Constants.TOKEN, "")!!
    }
}