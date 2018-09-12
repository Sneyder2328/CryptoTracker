package com.sneyder.cryptotracker.ui.main

import com.nhaarman.mockito_kotlin.*
import com.sneyder.cryptotracker.TestingSchedulerProvider
import com.sneyder.cryptotracker.blockingObserve
import com.sneyder.cryptotracker.ui.base.BaseViewModelTest
import com.sneyder.cryptotracker.data.model.CryptoCurrency
import com.sneyder.cryptotracker.data.model.User
import com.sneyder.cryptotracker.data.repository.CryptoCurrenciesRepository
import com.sneyder.cryptotracker.data.repository.UserRepository
import com.sneyder.utils.Resource
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MainViewModelTest : BaseViewModelTest() {

    private lateinit var viewModel: MainViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var cryptoCurrenciesRepository: CryptoCurrenciesRepository
    private val list = listOf(CryptoCurrency(45, "", "", "", "", "", "", "", "", "", "", "", "", ""))
    private val user = User("", "")

    @Before
    fun setUp() {
        cryptoCurrenciesRepository = mock()
        userRepository = mock()
        whenever(cryptoCurrenciesRepository.findCryptoCurrencies()).thenReturn(Flowable.just(list))
        viewModel = MainViewModel(userRepository, cryptoCurrenciesRepository, TestingSchedulerProvider())
    }

    @Test
    fun `load My User Successfully`(){
        // Given
        given(userRepository.findMyUser()).willReturn(Flowable.just(user))

        // Then
        assertEquals(Resource.success(user), viewModel.getMyUser().blockingObserve())
    }

    @Test
    fun `load My User Failed`(){
        // Given
        given(userRepository.findMyUser()).willReturn(Flowable.error(Throwable()))

        // Then
        assertEquals(Resource.error<User>(), viewModel.getMyUser().blockingObserve())
    }

    @Test
    fun `load My User Only Once`() {
        // Given
        given(userRepository.findMyUser()).willReturn(Flowable.just(user))

        // When
        viewModel.getMyUser().blockingObserve()
        viewModel.getMyUser().blockingObserve()

        // Then
        verify(userRepository, times(1)).findMyUser()
    }

    @Test
    fun `do not Update FirebaseTokenId If Null`(){
        // Given
        given(userRepository.updateFirebaseTokenId(any(), any())).willReturn(Single.just(""))

        // When
        val keepFirebaseTokenIdUpdated = viewModel.keepFirebaseTokenIdUpdated(null)

        // Then
        assertEquals(false, keepFirebaseTokenIdUpdated)
        verify(userRepository, times(0)).updateFirebaseTokenId(any(), any())
    }

    @Test
    fun `update FirebaseTokenId`(){
        // Given
        val sessionId = "jkasfgjklfghdalfhjkhjkgd"
        given(userRepository.getSessionId()).willReturn(sessionId)
        given(userRepository.updateFirebaseTokenId(any(), any())).willReturn(Single.just(""))

        // When
        val token = "sfjkldfjhklgdkl"
        val keepFirebaseTokenIdUpdated = viewModel.keepFirebaseTokenIdUpdated(token)

        // Then
        assertEquals(true, keepFirebaseTokenIdUpdated)
        verify(userRepository, times(1)).updateFirebaseTokenId(sessionId, token)
    }

}