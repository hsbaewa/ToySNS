package com.hs.toy.sns.domain.usecase

import com.hs.toy.sns.domain.model.Authentication
import com.hs.toy.sns.domain.model.User
import kotlinx.coroutines.flow.Flow

interface SignUpUseCase<T : Authentication, U : User> {
    suspend operator fun invoke(authentication: T, user: U): Flow<U>
}