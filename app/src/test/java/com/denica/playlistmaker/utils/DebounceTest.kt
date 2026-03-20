package com.denica.playlistmaker.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DebounceTest {

    @Test
    fun `debounce with useLastParam true emits only last param`() = runTest {
        val calls = mutableListOf<Int>()
        val debounced = debounce(
            delayMillis = 30L,
            coroutineScope = this,
            useLastParam = true
        ) { value: Int ->
            calls += value
        }

        debounced(1)
        debounced(2)
        debounced(3)

        advanceTimeBy(30L)
        runCurrent()

        assertEquals(listOf(3), calls)
    }

    @Test
    fun `debounce with useLastParam false emits first param only`() = runTest {
        val calls = mutableListOf<Int>()
        val debounced = debounce(
            delayMillis = 30L,
            coroutineScope = this,
            useLastParam = false
        ) { value: Int ->
            calls += value
        }

        debounced(1)
        debounced(2)
        debounced(3)

        advanceTimeBy(30L)
        runCurrent()

        assertEquals(listOf(1), calls)
    }

    @Test
    fun `throttleFirst executes immediately and blocks within window`() = runTest {
        val calls = mutableListOf<Int>()
        val throttled = throttleFirst(
            windowMillis = 50L,
            coroutineScope = this
        ) { value: Int ->
            calls += value
        }

        throttled(1)
        throttled(2)
        throttled(3)
        advanceTimeBy(60L)
        runCurrent()
        throttled(4)

        assertEquals(listOf(1, 4), calls)
    }
}
