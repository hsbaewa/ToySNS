package com.hs.toy.sns.domain.usecase

import com.hs.toy.sns.domain.model.User
import com.hs.toy.sns.domain.repository.Result
import com.hs.toy.sns.domain.repository.UserGetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetUserUseCaseImpl<T : User>(
    private val userRepository: UserGetRepository<T>
) : GetUserUseCase<T> {
    override suspend fun invoke(): Flow<T> {
        return flow {
            when (val result = userRepository.getUser()) {
                is Result.Error -> throw result.e
                is Result.Success -> emit(result.data)
            }
        }
    }

    override suspend fun invoke(id: String): Flow<T> {
        return flow {
            when (val result = userRepository.getUser(id)) {
                is Result.Error -> throw result.e
                is Result.Success -> emit(result.data)
            }
        }
    }
}