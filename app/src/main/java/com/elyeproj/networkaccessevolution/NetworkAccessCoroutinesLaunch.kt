package com.elyeproj.networkaccessevolution

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.HttpUrl

class NetworkAccessCoroutinesLaunch(private val view: MainView) : NetworkAccess {
    private var coroutineScope: CoroutineScope? = null

    private val errorHandler = CoroutineExceptionHandler { context, error ->
        Log.d("Track", "Launch Exception")
        coroutineScope?.launch(Dispatchers.Main) {
            Log.d("Track", "Launch Exception Result")
            view.updateScreen(error.localizedMessage)
        }
    }

    override fun fetchData(httpUrlBuilder: HttpUrl.Builder, searchText: String) {
        coroutineScope?.cancel()
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        coroutineScope?.launch(errorHandler) {
            try {
                Log.d("Track", "Launch Fetch Started")
                val result = Network.fetchHttpResult(httpUrlBuilder, searchText)
                Log.d("Track", "Launch Fetch Done")

//                throw IllegalStateException("My Error")
//                if (!isActive) {
//                    Log.d("Elisha", "cancel launch")
//                }
                yield()
                launch(Dispatchers.Main) {
                    when(result) {
                        is Network.Result.NetworkError -> {
                            view.updateScreen(result.message)
                            Log.d("Track", "Launch Post Error Result")
                        }
                        is Network.Result.NetworkResult -> {
                            view.updateScreen(result.message)
                            Log.d("Track", "Launch Post Success Result")
                        }
                    }
                }
            } catch (e: CancellationException) {
                Log.d("Track", "Launch Cancel Result")
            }
        }
    }

    override fun terminate() {
        coroutineScope?.cancel()
    }
}

