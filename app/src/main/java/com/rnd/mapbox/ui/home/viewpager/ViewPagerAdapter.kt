package com.rnd.mapbox.ui.home.viewpager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rnd.mapbox.R
import kotlinx.android.synthetic.main.item_page.view.*

class ViewPagerAdapter : RecyclerView.Adapter<PagerVH>() {

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
        if (position == 0) {
            tvRate.text = "$0.00"
            lasttrip.text = "Today"
            tvDate.text = "0 trip completed"
            mode.visibility = View.INVISIBLE
            seeall.text = "SEE WEEKLY SUMMARY"
            container.setBackgroundResource(colors[position])
        }
        if (position == 1) {
            tvRate.text = "$15.68"
            lasttrip.text = "LAST TRIP"
            tvDate.text = "Yesterday at 8:30 pm"
            mode.text = "UberX"
            seeall.text = "SEE ALL TRIPS"
            container.setBackgroundResource(colors[position])
        }
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)