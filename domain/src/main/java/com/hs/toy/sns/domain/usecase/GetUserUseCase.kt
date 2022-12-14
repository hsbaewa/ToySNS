package com.hs.toy.sns.domain.usecase

import com.hs.toy.sns.domain.model.User
import kotlinx.coroutines.flow.Flow

interface GetUserUseCase<T : User> {
    suspend operator fun invoke(): Flow<T>
    suspend operator fun invoke(id: String): Flow<T>
}