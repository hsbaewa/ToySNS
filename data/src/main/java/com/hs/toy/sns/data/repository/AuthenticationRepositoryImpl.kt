package com.hs.toy.sns.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.hs.toy.sns.data.model.AuthenticationModel
import com.hs.toy.sns.domain.model.Authentication
import com.hs.toy.sns.domain.repository.AuthenticationRepository
import kotlinx.coroutines.tasks.await

class AuthenticationRepositoryImpl : AuthenticationRepository<AuthenticationModel> {
    override suspend fun insertAuthentication(authentication: AuthenticationModel): AuthenticationModel {
        val authResult = createUserWithEmailAndPassword(authentication)
        authResult.user?.run {
            authentication.id = uid
            authentication.name = displayName
        }
        return authentication
    }

    private suspend fun createUserWithEmailAndPassword(authentication: Authentication): AuthResult {
        return FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            authentication.email,
            authentication.password
        ).await()
    }

    override suspend fun updateAuthenticationName(
        authentication: AuthenticationModel,
        name: String
    ) {
        val user = FirebaseAuth.getInstance().currentUser ?: let {
            checkValidationOrThrow(authentication)
            FirebaseAuth.getInstance().currentUser
        }

        if (user != null) {
            val profileChangeRequest = userProfileChangeRequest {
                displayName = name
            }
            user.updateProfile(profileChangeRequest).await()
        }

    }

    override suspend fun checkValidationOrThrow(authentication: AuthenticationModel) {
        val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(
            authentication.email,
            authentication.password
        ).await()
        if (authResult.user == null) {
            throw AuthenticationRepository.AuthenticateFailed("user is null")
        }
        authResult.user?.run {
            authentication.id = uid
            authentication.name = displayName
        }
    }

}