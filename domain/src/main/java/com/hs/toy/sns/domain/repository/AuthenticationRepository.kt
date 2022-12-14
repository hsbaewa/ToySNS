package com.hs.toy.sns.domain.repository

import com.hs.toy.sns.domain.model.Authentication

interface AuthenticationRepository<T : Authentication> {
    @Throws(InvalidAuthenticationException::class)
    suspend fun insertAuthentication(authentication: T): T

    @Throws(AuthenticationException::class)
    suspend fun checkValidationOrThrow(authentication: T)

    @Throws(InvalidAuthenticationException::class)
    suspend fun updateAuthenticationName(authentication: T, name: String)

    open class AuthenticationException(message: String?) : Exception(message)
    class AuthenticateFailed(message: String?) : AuthenticationException(message)
    class InvalidAuthenticationException(message: String?) : AuthenticationException(message)
    class AlreadyExistAuthenticationException(message: String?) : AuthenticationException(message)
}