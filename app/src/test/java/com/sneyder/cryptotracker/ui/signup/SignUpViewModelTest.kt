/*
 * Copyright (C) 2018 Sneyder Angulo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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