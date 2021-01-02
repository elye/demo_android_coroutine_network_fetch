package com.elyeproj.networkaccessevolution

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.HttpUrl

class NetworkAccessCoroutinesAsyncAwait(private val view: MainView) : NetworkAccess {
    private var coroutineScope: CoroutineScope? = null

    private val errorHandler = CoroutineExceptionHandler { context, error ->
        Log.d("Track", "Async Exception")
        coroutineScope?.launch(Dispatchers.Main) {
            Log.d("Track", "Async Exception Result")
            view.updateScreen(error.localizedMessage)
        }
    }

    override fun fetchData(httpUrlBuilder: HttpUrl.Builder, searchText: String) {
        coroutineScope?.cancel()
        coroutineScope = MainScope()
        coroutineScope?.launch(errorHandler) {
            try {
                val defer = async(Dispatchers.IO) {
                    Log.d("Track", "Async Fetch Started")

                    Network.fetchHttpResult(httpUrlBuilder, searchText).apply {
                        Log.d("Track", "Async Fetch Done")
                    }
                }
                when (val result = defer.await()) {
                    is Network.Result.NetworkError -> {
                        view.updateScreen(result.message)
                        Log.d("Track", "Async Post Error Result")
                    }
                    is Network.Result.NetworkResult -> {
                        view.updateScreen(result.message)
                        Log.d("Track", "Async Post Success Result")
                    }
                }
            } catch (e: CancellationException) {
                Log.d("Track", "Async Cancel Result")
            }
        }
    }

    override fun terminate() {
        coroutineScope?.cancel()
    }
}

