package com.jc.topstackoverflowusers.data.remote

import com.jc.topstackoverflowusers.data.remote.model.UsersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UsersService {

    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("pagesize") pageSize: Int,
        @Query("order") order: String = DEFAULT_ORDER,
        @Query("sort") sort: String = DEFAULT_SORT,
        @Query("site") site: String = DEFAULT_SITE
    ): UsersResponse

    companion object {
        const val DEFAULT_ORDER = "desc"
        const val DEFAULT_SORT = "reputation"
        const val DEFAULT_SITE = "stackoverflow"
    }
}