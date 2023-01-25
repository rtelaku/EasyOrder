package com.telakuR.easyorder.services

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}