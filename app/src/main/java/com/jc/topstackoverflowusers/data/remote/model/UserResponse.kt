package com.jc.topstackoverflowusers.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class UserResponse(
    val accountId: Int,
    val displayName: String,
    val profileImage: String?,
    val reputation: Int
)