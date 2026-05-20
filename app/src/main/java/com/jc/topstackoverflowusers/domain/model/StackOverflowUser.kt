package com.jc.topstackoverflowusers.domain.model

data class StackOverflowUser(
    val id: Int,
    val name: String,
    val profileImageUrl: String?,
    val reputation: Int,
    val isFollowed: Boolean
)