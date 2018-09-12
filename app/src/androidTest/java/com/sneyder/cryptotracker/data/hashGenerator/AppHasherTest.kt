package com.sneyder.cryptotracker.data.hashGenerator

import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AppHasherTest {

    private lateinit var h: Hasher

    @Before
    fun setUp() {
        h = AppHasher()
    }

    @Test
    fun hashIsTheSame() {
        runBlocking {
            Assert.assertEquals(h.hash("sneyder"), h.hash("sneyder"))
        }
    }

    @Test
    fun hashDoesNotContainSpaces(){
        runBlocking {
            Assert.assertFalse(h.hash("u").contains(' '))
        }
    }
}