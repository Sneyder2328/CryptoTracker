package com.sneyder.cryptotracker.ui.signup

import android.arch.lifecycle.MutableLiveData
import com.sneyder.cryptotracker.data.hashGenerator.Hasher
import com.sneyder.cryptotracker.data.model.User
import com.sneyder.cryptotracker.data.model.UserRequest
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.cryptotracker.utils.CoroutineContextProvider
import com.sneyder.utils.Resource
import com.sneyder.utils.schedulers.SchedulerProvider
import com.sneyder.utils.ui.base.BaseViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class SignUpViewModel
@Inject constructor(
        private val userRepository: UserRepository,
        private val hasher: Hasher,
        schedulerProvider: SchedulerProvider,
        private val coroutineContextProvider: CoroutineContextProvider
) : BaseViewModel(schedulerProvider) {

    val user: MutableLiveData<Resource<User>> by lazy { MutableLiveData<Resource<User>>() }

    fun signUp(userRequest: UserRequest) {
        user.value = Resource.loading()
        launch(coroutineContextProvider.UI) {
            add(userRepository.signUpUser(userRequest.apply { password = hasher.hash(password + email) })
                    .applySchedulers()
                    .subscribeBy(
                            onError = { user.value = Resource.error() },
                            onSuccess = { user.value = Resource.success(it) }
                    ))
        }
    }

}