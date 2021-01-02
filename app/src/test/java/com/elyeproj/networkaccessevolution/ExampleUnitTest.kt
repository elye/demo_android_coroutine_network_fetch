package com.elyeproj.networkaccessevolution

import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private var coroutineScope: CoroutineScope? = null
    private val mainThreadSurrogate = newSingleThreadContext("Test Main")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        // reset main dispatcher to the original Main dispatcher       
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    private val errorHandler = CoroutineExceptionHandler { context, error ->
        println("Launch Exception ${Thread.currentThread()}")
        coroutineScope?.launch(Dispatchers.Main) {
            println("Launch Exception Result ${Thread.currentThread()}")
        }
    }

    @Test
    fun fetchData() {
        runBlocking {
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            coroutineScope?.launch(errorHandler) {
                println("Launch Fetch Started ${Thread.currentThread()}")
                throw IllegalStateException("error")
            }?.join()
        }
    }
}
