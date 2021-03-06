package com.elyeproj.networkaccessevolution

import kotlinx.coroutines.*
import okhttp3.HttpUrl

class NetworkAccessCoroutinesAsyncAwait(private val view: MainView) : NetworkAccess {
    private var coroutineScope: CoroutineScope? = null

    private val errorHandler = CoroutineExceptionHandler { context, error ->
        logOut("Async Exception")
        coroutineScope?.launch(Dispatchers.Main) {
            logOut("Async Exception Result")
            view.updateScreen(error.localizedMessage ?: "")
        }
    }

    override fun fetchData(httpUrlBuilder: HttpUrl.Builder, searchText: String) {
        coroutineScope?.cancel()
        coroutineScope = MainScope()
        coroutineScope?.launch(errorHandler) {
            try {
                val defer = async(Dispatchers.IO) {
                    logOut("Async Fetch Started")

                    Network.fetchHttpResult(httpUrlBuilder, searchText).apply {
                        logOut("Async Fetch Done")
                    }
                }
                when (val result = defer.await()) {
                    is Network.Result.NetworkError -> {
                        view.updateScreen(result.message)
                        logOut("Async Post Error Result")
                    }
                    is Network.Result.NetworkResult -> {
                        view.updateScreen(result.message)
                        logOut("Async Post Success Result")
                    }
                }
            } catch (e: CancellationException) {
                logOut("Async Cancel Result")
            }
        }
    }

    override fun terminate() {
        coroutineScope?.cancel()
    }
}

