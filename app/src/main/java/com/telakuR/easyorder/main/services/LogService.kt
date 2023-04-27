package com.telakuR.easyorder.main.services

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}