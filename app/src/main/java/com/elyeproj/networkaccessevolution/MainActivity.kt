package com.elyeproj.networkaccessevolution

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elyeproj.networkaccessevolution.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var binding: ActivityMainBinding

    private val networkAccessCoroutinesLaunch = NetworkAccessCoroutinesLaunch(this)
    private val networkAccessCoroutinesAsyncAwait = NetworkAccessCoroutinesAsyncAwait(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSearchCoroutinesLaunch.setOnClickListener {
            beginSearch(::beginSearchCoroutinesLaunch)
        }
        binding.btnSearchCoroutinesLaunchError.setOnClickListener {
            beginSearch(::beginSearchCoroutinesLaunchError)
        }
        binding.btnSearchCoroutinesLaunchCrash.setOnClickListener {
            beginSearch(::beginSearchCoroutinesLaunchCrash)
        }
        binding.btnSearchCoroutinesAsyncAwait.setOnClickListener {
            beginSearch(::beginSearchCoroutinesAsyncAwait)
        }
        binding.btnSearchCoroutinesAsyncAwaitError.setOnClickListener {
            beginSearch(::beginSearchCoroutinesAsyncAwaitError)
        }
        binding.btnSearchCoroutinesAsyncAwaitCrash.setOnClickListener {
            beginSearch(::beginSearchCoroutinesAsyncAwaitCrash)
        }
        binding.btnCancelAll.setOnClickListener {
            cancelAllRequest()
        }
    }

    private fun beginSearch(searchFunc : (query: String) -> Unit) {
        if (binding.editSearch.text.toString().isNotEmpty()) {
            searchFunc(binding.editSearch.text.toString())
        }
    }

    private fun beginSearchCoroutinesLaunch(queryString: String) {
        networkAccessCoroutinesLaunch.fetchData(Network.httpUrlBuilder, queryString)
    }

    private fun beginSearchCoroutinesLaunchError(queryString: String) {
        networkAccessCoroutinesLaunch.fetchData(Network.errorHttpUrlBuilder, queryString)
    }

    private fun beginSearchCoroutinesLaunchCrash(queryString: String) {
        networkAccessCoroutinesLaunch.fetchData(Network.crashHttpUrlBuilder, queryString)
    }

    private fun beginSearchCoroutinesAsyncAwait(queryString: String) {
        networkAccessCoroutinesAsyncAwait.fetchData(Network.httpUrlBuilder, queryString)
    }

    private fun beginSearchCoroutinesAsyncAwaitError(queryString: String) {
        networkAccessCoroutinesAsyncAwait.fetchData(Network.errorHttpUrlBuilder, queryString)
    }

    private fun beginSearchCoroutinesAsyncAwaitCrash(queryString: String) {
        networkAccessCoroutinesAsyncAwait.fetchData(Network.crashHttpUrlBuilder, queryString)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAllRequest()
    }

    private fun cancelAllRequest() {
        networkAccessCoroutinesLaunch.terminate()
        networkAccessCoroutinesAsyncAwait.terminate()
    }

    override fun updateScreen(result: String) {
        binding.txtSearchResult.text = result
    }
}
