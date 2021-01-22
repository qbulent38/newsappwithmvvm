package com.example.mvvmnewsapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmnewsapp.adapters.NewsAdapter
import com.example.mvvmnewsapp.ui.NewsActivity
import com.example.mvvmnewsapp.ui.NewsViewModel
import com.example.mvvmnewsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.mvvmnewsapp.util.Resource
import com.example.mvvmnewsapp.R
import com.example.mvvmnewsapp.db.ArticleDatabase
import com.example.mvvmnewsapp.repository.NewsRepository
import com.example.mvvmnewsapp.ui.NewsViewModelProviderFactory
import com.example.mvvmnewsapp.util.Constants.Companion.COUNTRY
import com.example.mvvmnewsapp.util.hide
import com.example.mvvmnewsapp.util.show
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.itemErrorMessage
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.android.synthetic.main.item_error_message.*

class BreakingNewsFragment : BaseFragment(R.layout.fragment_breaking_news) {

    private val args: BreakingNewsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(rvBreakingNews)

        val newsRepository = NewsRepository(ArticleDatabase(context as NewsActivity))
        val viewModelProviderFactory =
            NewsViewModelProviderFactory((activity as NewsActivity).application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)


        val category = args.category
        viewModel.getBreakingNews(countryCode = COUNTRY, category = category)

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_smartFragment_to_articleFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    onSuccess()
                    response.data?.let { newsResponse ->
                        submitList(newsResponse)
                    }
                }
                is Resource.Error -> {
                   onError()
                    response.message?.let { message ->
                        errorMessage(message)
                    }
                }
                is Resource.Loading -> {
                   onLoading()
                }
            }
        })

        btnRetry.setOnClickListener {
            viewModel.getBreakingNews("tr")
        }
    }



}








