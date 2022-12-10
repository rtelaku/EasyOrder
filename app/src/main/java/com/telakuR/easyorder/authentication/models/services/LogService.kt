package com.telakuR.easyorder.authentication.models.services

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}