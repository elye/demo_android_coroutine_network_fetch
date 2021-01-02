package com.elyeproj.networkaccessevolution

import okhttp3.HttpUrl

interface NetworkAccess {
    fun fetchData(httpUrlBuilder: HttpUrl.Builder, parameterName: String)
    fun terminate()
}