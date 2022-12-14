package com.hs.toy.sns.domain

import com.hs.toy.sns.domain.usecase.GetUserUseCase
import com.hs.toy.sns.domain.usecase.GetUserUseCaseImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetUserUseCaseTest {

    lateinit var useCase: GetUserUseCase<TestUserModel>

    @Before
    fun init() {
        val data = HashSet<TestUserModel>()
        data.add(
            TestUserModel(
                id = "0",
                email = "hsbaewa@gmail.com",
                name = "hsbaewa"
            )
        )
        data.add(
            TestUserModel(
                id = "1",
                email = "hsbaewa@naver.com",
                name = "hsbaewa"
            )
        )
        val repo = TestUserGetRepositoryImpl(data)
        useCase = GetUserUseCaseImpl(repo)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun 내_User_정보_조회() = runTest {
        val result = useCase().first()

        assertEquals("hsbaewa", result.name)
        return@runTest
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun ID_로_User_정보_조회() = runTest {
        val result = useCase("1").first()

        assertEquals("hsbaewa@naver.com", result.email)
        return@runTest
    }
}