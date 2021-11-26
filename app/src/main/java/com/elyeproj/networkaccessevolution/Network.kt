package com.elyeproj.networkaccessevolution

import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.time.Duration

object Network {
    private val httpClient = OkHttpClient
            .Builder()
            .connectTimeout(Duration.ofSeconds(1L))
            .build()

    val httpUrlBuilder = HttpUrl.Builder()
            .host("en.wikipedia.org")
            .addPathSegment("w")
            .addOthers()

    val errorHttpUrlBuilder = HttpUrl.Builder()
            .host("en.wikipedia.org")
            .addPathSegment("x")
            .addOthers()

    val crashHttpUrlBuilder = HttpUrl.Builder()
            .host("en.wikipedia.or")
            .addPathSegment("w")
            .addOthers()

    private fun HttpUrl.Builder.addOthers() = this
            .scheme("https")
            .addPathSegment("api.php")
            .addQueryParameter("action", "query")
            .addQueryParameter("format", "json")
            .addQueryParameter("list", "search")

    private const val SEARCH_KEY = "srsearch"

    fun fetchHttpResult(httpUrlBuilder: HttpUrl.Builder, queryString: String): Result {
        val request = setupHttpRequest(httpUrlBuilder, queryString)
        val response = httpClient.newCall(request).execute()
        return deserializeResponse(response)
    }

    fun fetchHttpResultAsync(
        httpUrlBuilder: HttpUrl.Builder,
        queryString: String,
        onResult: (result: Result) -> Unit,
        onFailure: (error: IOException) -> Unit
    ) {
        val request = setupHttpRequest(httpUrlBuilder, queryString)
//        while(true) {
//            Thread.sleep(5000)
//            val response = httpClient.newCall(request).execute()
//            onResult(deserializeResponse(response))
//        }
        httpClient.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, excpetion: IOException) {
                onFailure(excpetion)
            }

            override fun onResponse(call: Call, response: Response) {
                onResult(deserializeResponse(response))
            }
        })
    }

    private fun deserializeResponse(response: Response): Result {
        if (!response.isSuccessful) {
            return Result.NetworkError("Error ${response.code}:${response.message}")
        }
        val raw = response.body?.string()
        val result = Gson().fromJson(raw, Model.Result::class.java)
        return Result.NetworkResult(result.query.searchinfo.totalhits.toString())
    }

    private fun setupHttpRequest(
        httpUrlBuilder: HttpUrl.Builder,
        queryString: String
    ): Request {
        val httpUrl = httpUrlBuilder
            .removeAllQueryParameters(SEARCH_KEY)
            .addQueryParameter(SEARCH_KEY, queryString)
            .build()
        return Request.Builder().get().url(httpUrl).build()
    }

    sealed class Result {
        class NetworkError(val message: String) : Result()
        class NetworkResult(val message: String) : Result()
    }
}
