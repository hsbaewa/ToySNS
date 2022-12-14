package com.hs.toy.sns.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.hs.toy.sns.data.datasource.UserDatasource
import com.hs.toy.sns.data.datasource.UserDatasourceImpl
import com.hs.toy.sns.data.model.UserResponse
import com.hs.toy.sns.domain.model.Authentication
import com.hs.toy.sns.domain.repository.Result
import com.hs.toy.sns.domain.repository.UserGetRepository

class UserGetRepositoryImpl(private val userDataSource: UserDatasource<UserResponse> = UserDatasourceImpl()) :
    UserGetRepository<UserResponse> {
    override suspend fun getUser(): Result<UserResponse> {
        return try {
            FirebaseAuth.getInstance().currentUser?.run {
                Result.Success(userDataSource.get(uid))
            } ?: Result.Error(UserGetRepository.UserNotFoundException("user not found"))
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    override suspend fun getUser(id: String): Result<UserResponse> {
        return try {
            Result.Success(userDataSource.get(id))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUser(authentication: Authentication): Result<UserResponse> {
        return try {
            val id = authentication.id
                ?: throw UserGetRepository.UserNotFoundException("authentication id is null")
            Result.Success(userDataSource.get(id))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}