package com.jc.topstackoverflowusers.domain.usecase

import com.jc.topstackoverflowusers.domain.repository.UsersRepository
import javax.inject.Inject

class UnfollowUserUseCase @Inject constructor(
    private val repository: UsersRepository
) {

    suspend operator fun invoke(accountId: Int, ) = repository.unfollowUser(accountId)
}