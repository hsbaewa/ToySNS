package com.hs.toy.sns.domain.model

interface Authentication {
    val email: String
    val password: String
    var name: String?
    var id: String?
    var token: String?
}