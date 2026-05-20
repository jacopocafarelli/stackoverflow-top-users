package com.jc.topstackoverflowusers.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersResponse(
    val items: List<UserResponse>,

    @SerialName("has_more")
    val hasMore: Boolean?,
)