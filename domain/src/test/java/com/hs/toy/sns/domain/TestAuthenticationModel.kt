package com.hs.toy.sns.domain

import com.hs.toy.sns.domain.model.Authentication

data class TestAuthenticationModel(
    override val email: String,
    override val password: String,
    override var id: String? = null,
    override var token: String? = null,
    override var name: String? = null
) : Authentication {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Authentication -> other.email == email
            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}