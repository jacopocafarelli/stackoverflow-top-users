package com.jc.topstackoverflowusers.data.remote.model

data class UsersPageResponse(
    val users: List<UserResponse>,
    val hasMore: Boolean
)