package com.hs.toy.sns.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.*
import com.hs.toy.sns.data.model.AuthenticationModel
import com.hs.toy.sns.data.model.UserResponse
import com.hs.toy.sns.data.repository.AuthenticationRepositoryImpl
import com.hs.toy.sns.data.repository.UserGetRepositoryImpl
import com.hs.toy.sns.data.repository.UserPutRepositoryImpl
import com.hs.toy.sns.domain.usecase.GetUserUseCaseImpl
import com.hs.toy.sns.domain.usecase.SignInUseCaseImpl
import com.hs.toy.sns.domain.usecase.SignUpUseCaseImpl
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.util.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private val authenticationStorage = HashMap<String, AuthenticationModel>()
    private val userStorage = HashMap<String, UserResponse?>()
    private var currentAuthentication: AuthenticationModel? = null

    @Before
    fun initFirebaseAuth() {
        mockkStatic(FirebaseAuth::class)

        every {
            FirebaseAuth.getInstance()
        } answers {
            mockk<FirebaseAuth>().apply {

                every {
                    currentUser
                } answers {
                    if (currentAuthentication == null) {
                        null
                    } else {
                        mockk<FirebaseUser>().apply {
                            every { displayName } returns currentAuthentication?.name
                            every { email } returns currentAuthentication?.email
                            every { uid } returns currentAuthentication?.id!!

                            val slotProfileRequest = slot<UserProfileChangeRequest>()
                            every {
                                updateProfile(capture(slotProfileRequest))
                            } answers {
                                val userModel = authenticationStorage[uid]
                                if (userModel != null) {
                                    userModel.name = slotProfileRequest.captured.displayName
                                }
                                mockk<Task<Void>>().apply {
                                    every { isComplete } returns true
                                    every { isCanceled } returns false
                                    every { exception } returns null
                                    every { result } returns null
                                }
                            }
                        }
                    }
                }


                val slotEmail = slot<String>()
                val slotPassword = slot<String>()
                every {
                    createUserWithEmailAndPassword(capture(slotEmail), capture(slotPassword))
                } answers {
                    val email = slotEmail.captured
                    val password = slotPassword.captured
                    if (authenticationStorage.filter { it.value.email == email }.isNotEmpty()) {
                        mockk<Task<AuthResult>>().apply {
                            every { isComplete } returns true
                            every { isCanceled } returns false
                            every { exception } returns Exception("already email")
                            every { result } returns null
                        }
                    } else {
                        val uid = Random().nextInt(Int.MAX_VALUE).toString()
                        authenticationStorage[uid] =
                            AuthenticationModel(email, password, email, uid).apply {
                                currentAuthentication = this
                            }
                        mockk<Task<AuthResult>>().apply {
                            every { isComplete } returns true
                            every { isCanceled } returns false
                            every { exception } returns null
                            every { result } returns mockk<AuthResult>().apply {
                                every { user } returns mockk<FirebaseUser>().apply {
                                    every { this@apply.email } returns email
                                    every { displayName } returns email
                                    every { this@apply.uid } returns uid
                                }
                            }
                        }
                    }
                }

                every {
                    signInWithEmailAndPassword(capture(slotEmail), capture(slotPassword))
                } answers {
                    val email = slotEmail.captured
                    val password = slotPassword.captured
                    val queryResult =
                        authenticationStorage.filter { it.value.email == email && it.value.password == password }.values.firstOrNull()
                    if (queryResult != null) {
                        currentAuthentication = queryResult
                        mockk<Task<AuthResult>>().apply {
                            every { isComplete } returns true
                            every { isCanceled } returns false
                            every { exception } returns null
                            every { result } returns mockk<AuthResult>().apply {
                                every { user } returns mockk<FirebaseUser>().apply {
                                    every { this@apply.email } returns queryResult.email
                                    every { displayName } returns queryResult.email
                                    every { this@apply.uid } returns (queryResult.id ?: "")
                                }
                            }
                        }
                    } else {
                        mockk<Task<AuthResult>>().apply {
                            every { isComplete } returns true
                            every { isCanceled } returns false
                            every { exception } returns Exception("login failed")
                            every { result } returns null
                        }
                    }
                }

                every {
                    signOut()
                } answers {
                    currentAuthentication = null
                }

            }
        }
    }

    @Before
    fun initFireStore() {
        mockkStatic(FirebaseFirestore::class)

        val slotDocumentId = slot<String>()
        val slotDocumentData = slot<Any>()

        every {
            FirebaseFirestore.getInstance().collection("users")
        } answers {
            mockk<CollectionReference>().apply {
                every { document(capture(slotDocumentId)) } returns mockk<DocumentReference>().apply {
                    every { id } answers {
                        if (slotDocumentId.isCaptured) {
                            slotDocumentId.captured
                        } else {
                            Random().nextInt(Int.MAX_VALUE).toString()
                        }
                    }

                    every {
                        set(capture(slotDocumentData), SetOptions.merge())
                    } answers {
                        mockk<Task<Void>>().apply {
                            every { isComplete } returns true
                            every { isCanceled } returns false
                            every { exception } returns null
                            every { result } returns null
                        }
                    }
                    every {
                        set(capture(slotDocumentData))
                    } answers {
                        mockk<Task<Void>>().apply {
                            if (userStorage.contains(id)) {
                                every { isComplete } returns true
                                every { isCanceled } returns false
                                every { exception } returns Exception("already user")
                                every { result } returns null
                            } else {
                                userStorage[id] = slotDocumentData.captured as? UserResponse
                                every { isComplete } returns true
                                every { isCanceled } returns false
                                every { exception } returns null
                                every { result } returns null
                            }
                        }
                    }

                    every {
                        get()
                    } answers {
                        mockk<Task<DocumentSnapshot>>().apply {

                            if (slotDocumentData.isCaptured) {
                                if (userStorage.contains(id)) {
                                    every { isComplete } returns true
                                    every { isCanceled } returns false
                                    every { exception } returns null
                                    every { result } answers {
                                        mockk<DocumentSnapshot>().apply {
                                            every { toObject(UserResponse::class.java) } answers {
                                                val pickUser = userStorage[slotDocumentId.captured]
                                                pickUser
                                            }
                                        }
                                    }
                                } else {
                                    every { isComplete } returns true
                                    every { isCanceled } returns false
                                    every { exception } returns Exception("user is not exists")
                                    every { result } returns null
                                }
                            } else {
                                every { isComplete } returns true
                                every { isCanceled } returns false
                                every { exception } returns null
                                every { result } returns null
                            }


                        }
                    }
                }

            }
        }

    }

    @Test
    fun testMockkAuth() = runBlocking {
        val result = FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword("hsbaewa@gmail.com", "1234")
            .await()
        assertEquals(result.user?.email, "hsbaewa@gmail.com")
        val uid = result.user?.uid

        try {
            val error = FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword("hsbaewa@gmail.com", "12345")
                .await()
            assertFalse(true)
        } catch (e: Exception) {
            assertEquals(e.message, "already email")
        }

        val signInResult =
            FirebaseAuth.getInstance().signInWithEmailAndPassword("hsbaewa@gmail.com", "1234")
                .await()
        assertNotNull(signInResult.user?.uid)
        assertEquals(signInResult.user?.uid, uid)

        try {
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword("hsbaewa@gmail.com", "12345")
                .await()
            assertFalse(true)
        } catch (e: Exception) {
            assertEquals(e.message, "login failed")
        }

        FirebaseAuth.getInstance().signOut()

        assertNull(FirebaseAuth.getInstance().currentUser)

        return@runBlocking
    }

    @Test
    fun testMockUser() = runBlocking {
        val result = FirebaseFirestore.getInstance()
            .collection("users")
            .document("1")
            .set(UserResponse("id", "email", "name"))
            .await()

        val data1 = FirebaseFirestore.getInstance()
            .collection("users")
            .document("1")
            .get()
            .await()
            .toObject(UserResponse::class.java)

        assertNotNull(data1)
        assertEquals(data1?.email, "email")

        return@runBlocking
    }

    @Test
    fun doAuthenticationTest() = runBlocking {
        val authenticationRepository = AuthenticationRepositoryImpl()
        val userGetRepository = UserGetRepositoryImpl()
        val userPutRepository = UserPutRepositoryImpl()

        val signUpUseCase = SignUpUseCaseImpl(authenticationRepository, userPutRepository)
        signUpUseCase(
            AuthenticationModel(email = "hsbaewa@gmail.com", password = "1234"),
            UserResponse(name = "Bae H.S")
        ).first().run {
            assertNotNull(this)
            assertEquals(email, "hsbaewa@gmail.com")
            assertEquals(name, "Bae H.S")
        }

        var naverId: String? = null
        signUpUseCase(
            AuthenticationModel(email = "hsbaewa@naver.com", password = "4321"),
            UserResponse(name = "Bae Naver")
        ).first().run {
            assertNotNull(this)
            assertEquals(email, "hsbaewa@naver.com")
            assertEquals(name, "Bae Naver")
            naverId = id
        }

        signUpUseCase(
            AuthenticationModel(email = "hsbaewa@nate.com", password = "nate"),
            UserResponse(name = "Bae nate")
        ).first().run {
            assertNotNull(this)
            assertEquals(email, "hsbaewa@nate.com")
            assertEquals(name, "Bae nate")
        }

        assertNotNull(naverId)

        val signInUseCase = SignInUseCaseImpl(authenticationRepository, userGetRepository)
        signInUseCase(AuthenticationModel("hsbaewa@gmail.com", "1234")).first().run {
            assertNotNull(this)
            assertEquals(this.email, "hsbaewa@gmail.com")
            assertEquals(this.name, "Bae H.S")
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        assertNotNull(currentUser)
        assertEquals(currentUser?.email, "hsbaewa@gmail.com")

        val getUserUseCase = GetUserUseCaseImpl(userGetRepository)
        val user = getUserUseCase().first()
        assertNotNull(user)
        assertEquals(user.email, "hsbaewa@gmail.com")
        assertEquals(user.name, "Bae H.S")

        val naverUser = getUserUseCase(naverId!!).first()
        assertNotNull(naverUser)
        assertEquals(naverUser.email, "hsbaewa@naver.com")
        assertEquals(naverUser.name, "Bae Naver")
    }

}