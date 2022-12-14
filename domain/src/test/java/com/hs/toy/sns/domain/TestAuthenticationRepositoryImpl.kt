package com.hs.toy.sns.domain

import com.hs.toy.sns.domain.repository.AuthenticationRepository

class TestAuthenticationRepositoryImpl : AuthenticationRepository<TestAuthenticationModel> {
    private val dataSet = HashSet<TestAuthenticationModel>()
    override suspend fun insertAuthentication(authentication: TestAuthenticationModel): TestAuthenticationModel {
        if (dataSet.add(authentication)) {
            authentication.id = "existid"
        } else {
            AuthenticationRepository.AlreadyExistAuthenticationException("already authentication")
        }
        return authentication
    }

    override suspend fun checkValidationOrThrow(authentication: TestAuthenticationModel) {
        if (dataSet.contains(authentication)) {
            dataSet.find { it.email == authentication.email && it.password == authentication.password }
                ?.run {
                    authentication.id = id
                }
                ?: throw AuthenticationRepository.AuthenticationException("$authentication is not contains in dataSet")
        } else {
            throw AuthenticationRepository.AuthenticationException("$authentication is not contains in dataSet")
        }
    }

    override suspend fun updateAuthenticationName(
        authentication: TestAuthenticationModel,
        name: String
    ) {
        dataSet.find { it.id == authentication.id }?.run {
            this.name = name
        }
    }
}