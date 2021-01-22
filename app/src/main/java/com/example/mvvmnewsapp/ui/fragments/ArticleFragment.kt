package com.example.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.mvvmnewsapp.R
import com.example.mvvmnewsapp.ui.NewsActivity
import com.example.mvvmnewsapp.ui.NewsViewModel
import com.example.mvvmnewsapp.util.changeImage
import com.example.mvvmnewsapp.util.hide
import com.example.mvvmnewsapp.util.show
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url!!)
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    try {
                        articleProgressBar.show()
                        articleProgressBar.progress = newProgress
                        if (newProgress == 100) {
                            articleProgressBar.hide()
                        }
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }

                    super.onProgressChanged(view, newProgress)
                }
            }
        }

        if (article.isSaved)
            fab.changeImage(R.drawable.ic_star_on)
        else
            fab.changeImage(R.drawable.ic_star_off)

        fab.setOnClickListener {
            viewModel.updateArticle(article) {
                article.mId = it
            }
            if (article.isSaved) {
                fab.changeImage(R.drawable.ic_star_on)
                Snackbar.make(view, getString(R.string.save_article), Snackbar.LENGTH_SHORT).show()
            } else
                fab.changeImage(R.drawable.ic_star_off)
        }
    }
}