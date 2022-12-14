package com.hs.toy.sns.domain.repository

import com.hs.toy.sns.domain.model.User

interface UserRepository<T : User> : UserGetRepository<T>, UserPutRepository<T>