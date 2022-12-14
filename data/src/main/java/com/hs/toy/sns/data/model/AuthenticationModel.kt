package com.hs.toy.sns.data.model

import com.hs.toy.sns.domain.model.Authentication

data class AuthenticationModel(
    override val email: String,
    override val password: String,
    override var name: String? = null,
    override var id: String? = null,
    override var token: String? = null
) : Authentication