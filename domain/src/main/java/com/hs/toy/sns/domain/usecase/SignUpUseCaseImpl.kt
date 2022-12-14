package com.hs.toy.sns.domain.usecase

import com.hs.toy.sns.domain.model.Authentication
import com.hs.toy.sns.domain.model.User
import com.hs.toy.sns.domain.repository.AuthenticationRepository
import com.hs.toy.sns.domain.repository.UserPutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SignUpUseCaseImpl<T : Authentication, U : User>(
    private val authenticationRepository: AuthenticationRepository<T>,
    private val userRepository: UserPutRepository<U>
) : SignUpUseCase<T, U> {

    override suspend fun invoke(authentication: T, user: U): Flow<U> {
        return flow {
            val authResult = authenticationRepository.insertAuthentication(authentication)
            user.id = authResult.id
                ?: throw AuthenticationRepository.AuthenticationException("auth result id is null")
            user.email = authResult.email
            user.name?.run {
                authenticationRepository.updateAuthenticationName(authentication, this)
                authentication.name = this
            }

            insertUser(user)
            emit(user)
        }
    }

    private suspend fun insertUser(user: U) {
        userRepository.putUser(user)
    }
}