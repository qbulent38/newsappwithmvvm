package com.example.mvvmnewsapp.util

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.mvvmnewsapp.R
import org.joda.time.PeriodType
import java.text.SimpleDateFormat
import java.util.*


fun ProgressBar.hide() {
    visibility = View.GONE
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ImageView.changeImage(@DrawableRes imgDrawable: Int) {
    setImageDrawable(
        ContextCompat.getDrawable(
            context,
            imgDrawable
        )
    )
}


//https://stackoverflow.com/questions/9277747/android-simpledateformat-how-to-use-it
@SuppressLint("SimpleDateFormat")
fun convertData(publishedAt: String): String {

    val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val today = simpleDataFormat.format(Calendar.getInstance().time)

    val date1 = simpleDataFormat.parse(today)
    val date2 = simpleDataFormat.parse(publishedAt)

    val period = org.joda.time.Period(
        date2.time, date1.time,
        PeriodType.yearMonthDayTime()
    )

    val minutes = period.minutes
    val hours = period.hours
    val days = period.days
    val month = period.months
    val year = period.years

    when {
        hours < 1 -> {
            return "$minutes dakika önce"
        }
        days < 1 -> {
            return "$hours saat önce"
        }
        month < 1 -> {
            return "$days gün önce"
        }
        year < 1 -> {
            return "$month ay önce"
        }
        else -> {
            return "$year yıl önce"
        }
    }
}