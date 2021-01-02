package com.elyeproj.networkaccessevolution

import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
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
        val httpUrl = httpUrlBuilder
                .removeAllQueryParameters(SEARCH_KEY)
                .addQueryParameter(SEARCH_KEY, queryString)
                .build()
        val request = Request.Builder().get().url(httpUrl).build()
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful)
            return Result.NetworkError("Error ${response.code}:${response.message}")

        val raw = response.body?.string()
        val result = Gson().fromJson(raw, Model.Result::class.java)
        return Result.NetworkResult(result.query.searchinfo.totalhits.toString())
    }

    sealed class Result {
        class NetworkError(val message: String) : Result()
        class NetworkResult(val message: String) : Result()
    }
}
