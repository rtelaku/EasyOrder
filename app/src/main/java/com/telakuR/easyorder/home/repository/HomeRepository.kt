package com.telakuR.easyorder.home.repository

import com.telakuR.easyorder.models.User
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getEmployeeEmails(): List<String>

    suspend fun getEmployees(requests: List<String>): Flow<List<User>>

    suspend fun removeEmployee(email: String)

    suspend fun getRequestsEmails(): List<String>

    suspend fun getRequests(requestsEmails: List<String>): Flow<List<User>>

    suspend fun acceptRequest(email: String)

    suspend fun removeRequest(email: String)
}