package com.jc.topstackoverflowusers.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserResponse(
    @SerialName("account_id")
    val accountId: Int,

    @SerialName("display_name")
    val displayName: String,

    @SerialName("profile_image")
    val profileImage: String?,

    @SerialName("reputation")
    val reputation: Int
)