package com.hs.toy.sns.data.repository

import com.hs.toy.sns.data.datasource.UserDatasource
import com.hs.toy.sns.data.datasource.UserDatasourceImpl
import com.hs.toy.sns.data.model.UserResponse
import com.hs.toy.sns.domain.repository.UserGetRepository
import com.hs.toy.sns.domain.repository.UserPutRepository

class UserRepositoryImpl(
    private val userDatasource: UserDatasource<UserResponse> = UserDatasourceImpl(),
    private val userGetRepository: UserGetRepositoryImpl = UserGetRepositoryImpl(userDatasource),
    private val userPutRepository: UserPutRepositoryImpl = UserPutRepositoryImpl(userDatasource)
) : UserGetRepository<UserResponse> by userGetRepository,
    UserPutRepository<UserResponse> by userPutRepository