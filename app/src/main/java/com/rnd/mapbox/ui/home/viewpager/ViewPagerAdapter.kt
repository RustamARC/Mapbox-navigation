package com.rnd.mapbox.ui.home.viewpager

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rnd.mapbox.R
import kotlinx.android.synthetic.main.item_page.view.*

class ViewPagerAdapter(context: Context) : RecyclerView.Adapter<PagerVH>() {

    //array of colors to change the background color of screen
    private val colors = intArrayOf(
        android.R.color.white,
        android.R.color.white
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false))

    //get the size of color array
    override fun getItemCount(): Int = 2

    //binding the screen with view
    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {

        val font = Typeface.createFromAsset(context.assets, "fonts/icomoon.ttf")

        tvEye.typeface = font
        tvQuery.typeface = font

        val ssb = SpannableStringBuilder("$0.00")
        val dollarColor = ForegroundColorSpan(resources.getColor(R.color.colorDollar))
        ssb.setSpan(dollarColor, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        if (position == 0) {
            val ssb = SpannableStringBuilder("$0.00")
            val dollarColor = ForegroundColorSpan(resources.getColor(R.color.colorDollar))
            ssb.setSpan(dollarColor, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvRate.text = ssb
            lasttrip.text = "Today"
            tvDate.text = "0 trip completed"
            mode.visibility = View.INVISIBLE
            seeall.text = "SEE WEEKLY SUMMARY"
            container.setBackgroundResource(colors[position])
        }
        if (position == 1) {
            val ssb = SpannableStringBuilder("$15.68")
            val dollarColor = ForegroundColorSpan(resources.getColor(R.color.colorDollar))
            ssb.setSpan(dollarColor, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvRate.text = ssb
            lasttrip.text = "LAST TRIP"
            tvDate.text = "Yesterday at 8:30 pm"
            mode.text = "UberX"
            seeall.text = "SEE ALL TRIPS"
            container.setBackgroundResource(colors[position])
        }
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)