package com.sneyder.cryptotracker.ui.login

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.sneyder.cryptotracker.TestingSchedulerProvider
import com.sneyder.cryptotracker.blockingObserve
import com.sneyder.cryptotracker.data.hashGenerator.Hasher
import com.sneyder.cryptotracker.data.model.TypeLogin
import com.sneyder.cryptotracker.data.model.User
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.TestingCoroutineContextProvider
import com.sneyder.cryptotracker.ui.base.BaseViewModelTest
import com.sneyder.utils.Resource
import io.reactivex.Single
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoginViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: LogInViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var hasher: Hasher

    @Before
    fun setUp() {
        userRepository = mock()
        hasher = mock()
        viewModel = LogInViewModel(userRepository, hasher, TestingSchedulerProvider(), TestingCoroutineContextProvider())
    }

    @Test
    fun `login User successfully`() {
        runBlocking {
            // Given
            given(hasher.hash("21")).willReturn("dgdg")
            val user = User("12000", "Sneyder")
            given(userRepository.logInUser(any())).willReturn(Single.just(user))

            // When
            viewModel.logInUser(email = "1", password = "2", typeLogin = TypeLogin.GOOGLE.data)

            // Then
            assertEquals(Resource.success(user), viewModel.user.blockingObserve())
        }
    }

    @Test
    fun `login User failed`() {
        runBlocking {
            // Given
            given(hasher.hash("fhdfhmail")).willReturn("dgdg")
            given(userRepository.logInUser(any())).willReturn(Single.error(Throwable()))

            // When
            viewModel.logInUser(email = "mail", password = "fhdfh", typeLogin = TypeLogin.GOOGLE.data)

            // Then
            assertEquals(Resource.error<User>(), viewModel.user.blockingObserve())
        }
    }

}