package com.example.mvvmnewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mvvmnewsapp.NewsApplication
import com.example.mvvmnewsapp.R
import com.example.mvvmnewsapp.models.Article
import com.example.mvvmnewsapp.models.NewsResponse
import com.example.mvvmnewsapp.repository.NewsRepository
import com.example.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(
    private val app: Application,
    private val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<List<Article>>> = MutableLiveData()
    var breakingNewsPage = 1
    private var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null
    private var newSearchQuery: String? = null
    private var oldSearchQuery: String? = null

    fun getBreakingNews(countryCode: String, category: String = "general") = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(
                    countryCode,
                    breakingNewsPage,
                    category
                )
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error(getString(R.string.no_internet_message)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error(getString(R.string.network_failure)))
                else -> breakingNews.postValue(Resource.Error("Dönüştürme Hatası..."))
            }
        }
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error(getString(R.string.no_internet_message)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error(getString(R.string.network_failure)))
                else -> searchNews.postValue(Resource.Error("Dönüştürme Hatası..."))
            }
        }
    }

    private suspend fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<List<Article>> {
        when {
            response.isSuccessful -> {
                response.body()?.let { resultResponse ->
                    breakingNewsPage++
                    if (breakingNewsResponse == null) {
                        breakingNewsResponse = resultResponse
                    } else {
                        val oldArticles = breakingNewsResponse?.articles
                        val newArticles = resultResponse.articles
                        oldArticles?.addAll(newArticles)
                    }

                    val savedList = newsRepository.getSavedNewsNoLiveData()
                    val list = breakingNewsResponse!!.articles

                    list.forEachIndexed { index, article ->
                        savedList.forEachIndexed { _, savedArticle ->
                            if (article.url == savedArticle.url) {
                                list[index] = savedArticle
                            }
                        }
                    }
                    return Resource.Success(list)
                }
            }
            response.code() == 429 -> {
                return Resource.Error(getString(R.string.must_request, response.errorBody()!!))
            }
        }
        return Resource.Error(getString(R.string.error_code, response.errorBody()!!))
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        when {
            response.isSuccessful -> {
                response.body()?.let { resultResponse ->
                    if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                        searchNewsPage = 1
                        oldSearchQuery = newSearchQuery
                        searchNewsResponse = resultResponse
                    } else {
                        searchNewsPage++
                        val oldArticles = searchNewsResponse?.articles
                        val newArticles = resultResponse.articles
                        oldArticles?.addAll(newArticles)
                    }
                    return Resource.Success(searchNewsResponse ?: resultResponse)
                }
            }
            response.code() == 429 -> {
                return Resource.Error(getString(R.string.must_request, response.errorBody()!!))
            }
        }
        return Resource.Error(getString(R.string.error_code, response.errorBody()!!))
    }

    @Suppress("NAME_SHADOWING")
    fun updateArticle(article: Article, id: (Long) -> Unit) = viewModelScope.launch {
        article.isSaved = !article.isSaved
        if (!article.isSaved) {
            newsRepository.deleteArticle(article)
        } else {
            val id = newsRepository.upsert(article)
            id(id)
        }
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    @Suppress("DEPRECATION")
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    private fun getString(id: Int, vararg formatArgs: Any): String {
        return app.getString(id, formatArgs)
    }

}












