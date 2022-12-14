package com.hs.toy.sns.data.model

import com.hs.toy.sns.domain.model.User

data class UserResponse(
    override var id: String? = null,
    override var email: String? = null,
    override var name: String? = null
) : User