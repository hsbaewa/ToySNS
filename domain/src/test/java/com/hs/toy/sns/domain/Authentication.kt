package com.hs.toy.sns.domain

import com.hs.toy.sns.domain.repository.AuthenticationRepository
import com.hs.toy.sns.domain.usecase.SignInUseCase
import com.hs.toy.sns.domain.usecase.SignInUseCaseImpl
import com.hs.toy.sns.domain.usecase.SignUpUseCase
import com.hs.toy.sns.domain.usecase.SignUpUseCaseImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class Authentication {
    lateinit var signUpUseCase: SignUpUseCase<TestAuthenticationModel, TestUserModel>
    lateinit var signInUseCase: SignInUseCase<TestAuthenticationModel, TestUserModel>

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun init() = runTest {
        val authenticationRepository = TestAuthenticationRepositoryImpl()
        val userModelSet = HashSet<TestUserModel>()
        val userGetRepository = TestUserGetRepositoryImpl(userModelSet)
        val userPutRepository = TestUserPutRepositoryImpl(userModelSet)
        signUpUseCase = SignUpUseCaseImpl(authenticationRepository, userPutRepository)
        signInUseCase = SignInUseCaseImpl(authenticationRepository, userGetRepository)

        signUpUseCase(TestAuthenticationModel("hsbaewa@gmail.com", "1234"), TestUserModel()).first()

        return@runTest
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doSignInSuccess() = runTest {
        val userModel = try {
            signInUseCase(TestAuthenticationModel("hsbaewa@gmail.com", "1234")).first()
        } catch (e: AuthenticationRepository.AuthenticationException) {
            null
        }
        assertNotNull(userModel)
        return@runTest
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doSignInFailed() = runTest {
        val userModel = try {
            signInUseCase(TestAuthenticationModel("hsbaewa@gmail.com", "12345")).first()
        } catch (e: AuthenticationRepository.AuthenticationException) {
            null
        }
        assertNull(userModel)
        return@runTest
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doSignUpAlreadyId() = runTest {
        val userModel = try {
            signUpUseCase(
                TestAuthenticationModel("hsbaewa@gmail.com", "0987"),
                TestUserModel()
            ).first()
        } catch (e: AuthenticationRepository.AuthenticationException) {
            null
        }
        assertNull(userModel)
        return@runTest
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun doSignUpSuccess() = runTest {
        val userModel = try {
            signUpUseCase(
                TestAuthenticationModel("hsbaewa@naver.com", "1111"),
                TestUserModel(name = "name")
            ).first()
        } catch (e: AuthenticationRepository.AuthenticationException) {
            null
        }

        assertNotNull(userModel)
        assertNotNull(userModel?.name)

        return@runTest
    }
}