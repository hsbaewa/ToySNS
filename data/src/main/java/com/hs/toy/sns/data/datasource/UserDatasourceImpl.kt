package com.hs.toy.sns.data.datasource

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hs.toy.sns.data.model.UserResponse
import kotlinx.coroutines.tasks.await

internal class UserDatasourceImpl : UserDatasource<UserResponse> {
    override suspend fun create(user: UserResponse): Boolean {
        val userDocument = getUserDocument()
        user.id = userDocument.id
        userDocument.set(user).await()
        return true
    }

    private fun getUserCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("users")
    }

    private fun getUserDocument(id: String? = null): DocumentReference {
        return id?.run {
            getUserCollection().document(this)
        } ?: getUserCollection().document()
    }

    override suspend fun update(user: UserResponse): Boolean {
        val userDocument = getUserDocument(user.id)
        user.id = userDocument.id
        userDocument.set(user).await()
        return true
    }

    override suspend fun delete(id: String): Boolean {
        val userDocument = getUserDocument(id)
        userDocument.delete().await()
        return true
    }

    override suspend fun get(id: String): UserResponse {
        val userDocument = getUserDocument(id)
        return userDocument.get().await().toObject(UserResponse::class.java)
            ?: throw UserDatasource.UserDatasourceException("document is cannot cast to UserResponse")
    }

}