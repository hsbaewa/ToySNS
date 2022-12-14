package com.hs.toy.sns.domain

import com.hs.toy.sns.domain.model.User

data class TestUserModel(
    override var id: String? = null,
    override var email: String? = null,
    override var name: String? = null
) : User