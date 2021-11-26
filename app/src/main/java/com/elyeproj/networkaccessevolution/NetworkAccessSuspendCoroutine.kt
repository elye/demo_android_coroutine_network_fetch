package com.elyeproj.networkaccessevolution

import kotlinx.coroutines.*
import okhttp3.HttpUrl
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
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        coroutineScope?.launch(errorHandler) {
            logOut("Suspend Fetch Started")
            val result: MyResult = suspendCancellableCoroutine { cancellableContinuation ->
                Network.fetchHttpResultAsync(
                    httpUrlBuilder,
                    searchText, { result ->
                        if (cancellableContinuation.isActive) {
                            logOut("Suspend Fetch Done")
                            cancellableContinuation.resumeWith(
                                Result.success(result))
                        } else {
                            logOut("Suspend Cancel Fetch Result")
                        }
                    }, { error ->
                        if (cancellableContinuation.isActive) {
                            logOut("Suspend Fetch Error")
                            cancellableContinuation.resumeWith(
                                Result.failure(error))
                        } else {
                            logOut("Suspend Cancel Error Result")
                        }
                    }
                )
            }
            launch(Dispatchers.Main) {
                when(result) {
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
    }

    override fun terminate() {
        coroutineScope?.cancel()
    }
}

