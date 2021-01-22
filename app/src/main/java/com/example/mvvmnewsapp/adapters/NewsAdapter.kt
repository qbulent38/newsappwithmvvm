package com.example.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmnewsapp.models.Article
import com.bumptech.glide.Glide
import com.example.mvvmnewsapp.R
import com.example.mvvmnewsapp.util.changeImage
import com.example.mvvmnewsapp.util.convertData
import kotlinx.android.synthetic.main.item_article_preview.view.*

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    private var isPublishedAt = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this)
                .load(article.urlToImage)
                .placeholder(R.drawable.newspaper)
                .into(ivArticleImage)

            tvTitle.text = article.title
            if (isPublishedAt)
                tvPublishedAt.text = convertData(article.publishedAt!!)
            else
                tvPublishedAt.text = article.source!!.name

            /**
             * setAnimation
             */
            setAnimation(ivArticleImage, R.anim.item_image_anim)
            setAnimation(tvTitle, R.anim.item_image_anim)
            setAnimation(tvPublishedAt, R.anim.item_anim)

            if (article.isSaved) {
                ivFavorites.changeImage(R.drawable.ic_star_on)
            } else {
                ivFavorites.changeImage(R.drawable.ic_star_off)
            }

            /**
             * OnClickListeners
             */
            setOnClickListener {
                onItemClickListener?.let { it(article) }
            }

            ivFavorites.setOnClickListener {
                if (article.isSaved) {
                    ivFavorites.changeImage(R.drawable.ic_star_off)
                } else {
                    ivFavorites.changeImage(R.drawable.ic_star_on)
                }
                onItemSavedClickListener?.let { it(article) }
            }

            ivShare.setOnClickListener {
                onItemShareClickListener?.let { it(article) }
            }
        }
    }

    private fun setAnimation(v: View, anim: Int) {
        v.animation = AnimationUtils.loadAnimation(
            v.context,
            anim
        )
    }

    private var onItemClickListener: ((Article) -> Unit)? = null
    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemSavedClickListener: ((Article) -> Unit)? = null
    fun setOnItemSavedClickListener(listener: (Article) -> Unit) {
        onItemSavedClickListener = listener
    }

    private var onItemShareClickListener: ((Article) -> Unit)? = null
    fun setOnItemShareClickListener(listener: (Article) -> Unit) {
        onItemShareClickListener = listener
    }

    fun hidePublishedAt() {
        this.isPublishedAt = false
    }
}













