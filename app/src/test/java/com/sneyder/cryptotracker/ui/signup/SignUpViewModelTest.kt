package com.sneyder.cryptotracker.ui.signup

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.sneyder.cryptotracker.TestingCoroutineContextProvider
import com.sneyder.cryptotracker.TestingSchedulerProvider
import com.sneyder.cryptotracker.blockingObserve
import com.sneyder.cryptotracker.data.hashGenerator.Hasher
import com.sneyder.cryptotracker.data.model.TypeLogin
import com.sneyder.cryptotracker.data.model.User
import com.sneyder.cryptotracker.data.model.UserRequest
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.ui.base.BaseViewModelTest
import com.sneyder.utils.Resource
import io.reactivex.Single
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class SignUpViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var hasher: Hasher

    @Before
    fun setUp() {
        hasher = mock()
        userRepository = mock()
        viewModel = SignUpViewModel(userRepository, hasher, TestingSchedulerProvider(), TestingCoroutineContextProvider())
    }

    @Test
    fun signUpUserSuccessfully() {
        runBlocking {
            // Given
            val user = User("12000", "Sneyder")
            given(hasher.hash("fhdfhmail")).willReturn("dgdg")
            given(userRepository.signUpUser(any())).willReturn(Single.just(user))

            // When
            viewModel.signUp(UserRequest(email = "mail", password = "fhdfh", typeLogin = TypeLogin.GOOGLE.data, userId = "12000"))

            // Then
            assertEquals(Resource.success(user), viewModel.user.blockingObserve())
        }
    }

    @Test
    fun signUpUserWithError() {
        runBlocking {
            // Given
            given(hasher.hash("fhdfhmail")).willReturn("dgdg")
            given(userRepository.signUpUser(any())).willReturn(Single.error(Throwable()))

            // When
            viewModel.signUp(UserRequest(email = "mail", password = "fhdfh", typeLogin = TypeLogin.GOOGLE.data, userId = "12000"))

            // Then
            assertEquals(Resource.error<User>(), viewModel.user.blockingObserve())
        }
    }
}