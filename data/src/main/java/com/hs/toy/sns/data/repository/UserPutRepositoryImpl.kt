package com.hs.toy.sns.data.repository

import com.hs.toy.sns.data.datasource.UserDatasource
import com.hs.toy.sns.data.datasource.UserDatasourceImpl
import com.hs.toy.sns.data.model.UserResponse
import com.hs.toy.sns.domain.repository.Result
import com.hs.toy.sns.domain.repository.UserPutRepository

class UserPutRepositoryImpl(private val userDatasource: UserDatasource<UserResponse> = UserDatasourceImpl()) :
    UserPutRepository<UserResponse> {
    override suspend fun putUser(user: UserResponse): Result<UserResponse> {
        return try {
            if (userDatasource.update(user))
                Result.Success(user)
            else throw UserPutRepository.UserPutException("user update failed")
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}