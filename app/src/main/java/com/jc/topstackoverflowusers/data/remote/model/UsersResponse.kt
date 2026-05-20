package com.jc.topstackoverflowusers.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UsersResponse(
    val items: List<UserResponse>,
    val hasMore: Boolean?,
)