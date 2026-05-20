package com.jc.topstackoverflowusers.domain.usecase

import com.jc.topstackoverflowusers.domain.model.StackOverflowUser
import com.jc.topstackoverflowusers.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopUsersUseCase @Inject constructor(
    private val repository: UsersRepository
) {

    suspend operator fun invoke(
        page: Int = 1,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): Flow<List<StackOverflowUser>> = repository.getTopUsers(page, pageSize)

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}