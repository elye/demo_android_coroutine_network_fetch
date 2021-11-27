package com.elyeproj.networkaccessevolution

import kotlinx.coroutines.*
import okhttp3.HttpUrl
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.elyeproj.networkaccessevolution.Network.Result as MyResult

class NetworkAccessSuspendCoroutine(private val view: MainView) : NetworkAccess {
    private var coroutineScope: CoroutineScope? = null

    private val errorHandler = CoroutineExceptionHandler { context, error ->
        logOut("Suspend Exception")
        coroutineScope?.launch(Dispatchers.Main) {
            logOut("Suspend Exception Result")
            view.updateScreen(error.localizedMessage ?: "")
        }
    }

    override fun fetchData(httpUrlBuilder: HttpUrl.Builder, searchText: String) {
        coroutineScope?.cancel()
        coroutineScope = MainScope()
        coroutineScope?.launch(errorHandler) {
            logOut("Suspend Fetch Started")
            val result: MyResult = suspendCancellableCoroutine { cancellableContinuation ->
                Network.fetchHttpResultAsync(
                    httpUrlBuilder,
                    searchText, { result ->
                        logOut("Suspend Fetch Done")
                        cancellableContinuation.resume(result)
                    }, { error ->
                        logOut("Suspend Fetch Error")
                        cancellableContinuation.resumeWithException(error)
                    }
                )
                cancellableContinuation.invokeOnCancellation {
                    logOut("Suspend Cancel Result")
                }
            }
            when (result) {
                is Network.Result.NetworkError -> {
                    view.updateScreen(result.message)
                    logOut("Suspend Post Error Result")
                }
                is Network.Result.NetworkResult -> {
                    view.updateScreen(result.message)
                    logOut("Suspend Post Success Result")
                }
            }
        }
    }

    override fun terminate() {
        coroutineScope?.cancel()
    }
}

