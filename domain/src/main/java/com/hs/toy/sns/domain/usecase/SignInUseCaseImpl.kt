package com.hs.toy.sns.domain.usecase

import com.hs.toy.sns.domain.model.Authentication
import com.hs.toy.sns.domain.model.User
import com.hs.toy.sns.domain.repository.AuthenticationRepository
import com.hs.toy.sns.domain.repository.Result
import com.hs.toy.sns.domain.repository.UserGetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SignInUseCaseImpl<T : Authentication, U : User>(
    private val authenticationRepository: AuthenticationRepository<T>,
    private val userRepository: UserGetRepository<U>
) : SignInUseCase<T, U> {
    override suspend fun invoke(authentication: T): Flow<U> {
        return flow {
            authenticationRepository.checkValidationOrThrow(authentication)
            val user = getUser(authentication)
            emit(user)
        }
    }

    private suspend fun getUser(authentication: Authentication): U {
        return when (val result = userRepository.getUser(authentication)) {
            is Result.Error -> throw result.e
            is Result.Success -> result.data
        }
    }
}