package com.hs.toy.sns.domain.repository

import com.hs.toy.sns.domain.model.User

interface UserPutRepository<T : User> {
    @Throws(UserPutException::class)
    suspend fun putUser(user: T): Result<T>

    open class UserPutException(message: String?) : Exception(message)
}