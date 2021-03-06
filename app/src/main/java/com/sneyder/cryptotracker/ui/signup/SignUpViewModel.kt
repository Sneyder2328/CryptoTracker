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

import android.arch.lifecycle.MutableLiveData
import com.sneyder.cryptotracker.data.hashGenerator.Hasher
import com.sneyder.cryptotracker.data.model.User
import com.sneyder.cryptotracker.data.model.UserRequest
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.utils.CoroutineContextProvider
import com.sneyder.utils.Resource
import com.sneyder.utils.schedulers.SchedulerProvider
import com.sneyder.utils.ui.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val hasher: Hasher,
        schedulerProvider: SchedulerProvider,
        coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(schedulerProvider, coroutineContextProvider) {

    val user: MutableLiveData<Resource<User>> by lazy { MutableLiveData<Resource<User>>() }

    fun signUp(userRequest: UserRequest) {
        user.value = Resource.loading()
        GlobalScope.launch(Main) {
            add(userRepository.signUpUser(userRequest.apply { password = hasher.hash(password + email) })
                    .applySchedulers()
                    .subscribeBy(
                            onError = { user.value = Resource.error() },
                            onSuccess = { user.value = Resource.success(it) }
                    ))
        }
    }

}