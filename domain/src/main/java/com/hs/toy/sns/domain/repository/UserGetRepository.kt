package com.hs.toy.sns.domain.repository

import com.hs.toy.sns.domain.model.Authentication
import com.hs.toy.sns.domain.model.User

interface UserGetRepository<T : User> {
    @Throws(UserGetException::class)
    suspend fun getUser(): Result<T>

    @Throws(UserGetException::class)
    suspend fun getUser(id: String): Result<T>

    @Throws(UserGetException::class)
    suspend fun getUser(authentication: Authentication): Result<T>

    open class UserGetException(message: String?) : Exception(message)
    class UserNotFoundException(message: String?) : UserGetException(message)
}