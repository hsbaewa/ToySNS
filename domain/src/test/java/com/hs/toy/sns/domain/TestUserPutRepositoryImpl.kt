package com.hs.toy.sns.domain

import com.hs.toy.sns.domain.repository.Result
import com.hs.toy.sns.domain.repository.UserPutRepository

class TestUserPutRepositoryImpl(private val dataSet: HashSet<TestUserModel>) :
    UserPutRepository<TestUserModel> {
    override suspend fun putUser(user: TestUserModel): Result<TestUserModel> {
        return try {
            dataSet.add(user)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}