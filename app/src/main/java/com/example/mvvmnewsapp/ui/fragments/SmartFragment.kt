package com.example.mvvmnewsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mvvmnewsapp.R
import com.example.mvvmnewsapp.ui.NewsActivity
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.fragment_smart.*


class SmartFragment : Fragment(R.layout.fragment_smart) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundleBusiness = Bundle()
        bundleBusiness.putString("category", "business")
        val bundleEntertainment = Bundle()
        bundleEntertainment.putString("category", "entertainment")
        val bundleGeneral = Bundle()
        bundleGeneral.putString("category", "general")
        val bundleHealth = Bundle()
        bundleHealth.putString("category", "health")
        val bundleSciene = Bundle()
        bundleSciene.putString("category", "science")
        val bundleSports = Bundle()
        bundleSports.putString("category", "sports")
        val bundleTechnology = Bundle()
        bundleTechnology.putString("category", "technology")

        val adapter = FragmentPagerItemAdapter(
            childFragmentManager, FragmentPagerItems.with(context)
                .add("Genel", BreakingNewsFragment()::class.java, bundleGeneral)
                .add("Bilim", BreakingNewsFragment()::class.java, bundleSciene)
                .add("Sağlık", BreakingNewsFragment()::class.java, bundleHealth)
                .add("İş", BreakingNewsFragment()::class.java, bundleBusiness)
                .add("Spor", BreakingNewsFragment()::class.java, bundleSports)
                .add("Teknoloji", BreakingNewsFragment()::class.java, bundleTechnology)
                .add("Eğlence", BreakingNewsFragment()::class.java, bundleEntertainment)
                .create()
        )

        viewPager.adapter = adapter
        viewPagerTab.setViewPager(viewPager)

    }


}