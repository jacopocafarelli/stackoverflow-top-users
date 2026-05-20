package com.jc.topstackoverflowusers.data.remote

import com.jc.topstackoverflowusers.data.remote.model.UsersPageResponse
import javax.inject.Inject

class UsersRemoteDataSource @Inject constructor(
    private val service: UsersService
) {

    suspend fun getUsers(page: Int, pageSize: Int): UsersPageResponse {
        val response = service.getUsers(page = page, pageSize = pageSize)
        return UsersPageResponse(
            users = response.items,
            hasMore = response.hasMore == true
        )
    }
}