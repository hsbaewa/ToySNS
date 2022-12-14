package com.hs.toy.sns.domain

import com.hs.toy.sns.domain.model.Authentication
import com.hs.toy.sns.domain.repository.Result
import com.hs.toy.sns.domain.repository.UserGetRepository

class TestUserGetRepositoryImpl(private val dataSet: HashSet<TestUserModel>) :
    UserGetRepository<TestUserModel> {
    override suspend fun getUser(): Result<TestUserModel> {
        return if (dataSet.isEmpty()) {
            Result.Error(UserGetRepository.UserNotFoundException("data set is empty"))
        } else {
            Result.Success(dataSet.first())
        }
    }

    override suspend fun getUser(id: String): Result<TestUserModel> {
        return try {
            if (dataSet.isEmpty())
                throw UserGetRepository.UserNotFoundException("data set is empty")
            val found = dataSet.find { it.id == id }
                ?: throw UserGetRepository.UserNotFoundException("$id is not found")
            Result.Success(found)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUser(authentication: Authentication): Result<TestUserModel> {
        return try {
            if (dataSet.isEmpty())
                throw UserGetRepository.UserNotFoundException("data set is empty")
            when (authentication) {
                is TestAuthenticationModel -> {
                    val found = dataSet.find { it.id == authentication.id }
                        ?: throw UserGetRepository.UserNotFoundException("$authentication is not found")
                    Result.Success(found)
                }
                else -> throw UserGetRepository.UserGetException("unkown authentication")
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}