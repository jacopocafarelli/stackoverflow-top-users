package com.jc.topstackoverflowusers.domain.usecase

import com.jc.topstackoverflowusers.domain.repository.UsersRepository
import javax.inject.Inject

class FollowUserUseCase @Inject constructor(
    private val repository: UsersRepository
) {

    suspend operator fun invoke(accountId: Int, ) = repository.followUser(accountId)
}