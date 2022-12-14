package com.hs.toy.sns.data.datasource

import com.hs.toy.sns.domain.model.User

interface UserDatasource<T : User> {
    suspend fun create(user: T): Boolean
    suspend fun update(user: T): Boolean
    suspend fun delete(id: String): Boolean
    suspend fun get(id: String): T

    open class UserDatasourceException(message: String?) : Exception(message)
}