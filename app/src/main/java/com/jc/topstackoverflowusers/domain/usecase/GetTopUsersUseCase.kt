package com.jc.topstackoverflowusers.domain.usecase

import com.jc.topstackoverflowusers.domain.model.StackOverflowUser
import com.jc.topstackoverflowusers.domain.repository.UsersRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class GetTopUsersUseCase @Inject constructor(
    private val repository: UsersRepository
) {

    suspend operator fun invoke(
        page: Int = 1,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): Result<List<StackOverflowUser>> {
        return try {
            val users = repository.getTopUsers(page = page, pageSize = pageSize)
            Result.success(users)
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            Result.failure(e)
        }
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}