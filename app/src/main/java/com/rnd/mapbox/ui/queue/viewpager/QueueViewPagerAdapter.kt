package com.rnd.mapbox.ui.queue.viewpager

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.rnd.mapbox.R
import com.rnd.mapbox.ui.queue.adapter.QueueAdapter
import com.rnd.mapbox.ui.queue.model.QueueModel
import kotlinx.android.synthetic.main.item_page.view.*
import kotlinx.android.synthetic.main.queue_list.view.*

class QueueViewPagerAdapter(private val context: Context) : RecyclerView.Adapter<PagerVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(context).inflate(R.layout.queue_list, parent, false))

    //get the size of color array
    override fun getItemCount(): Int = 3

    //binding the screen with view
    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {

        /* val font = Typeface.createFromAsset(context.assets, "fonts/icomoon.ttf")

         tvEye.typeface = font
         tvQuery.typeface = font*/

        if (position == 0) {
            val adapter = QueueAdapter(context, getData())
            queuelist.adapter = adapter
            progress.visibility = View.GONE
        }
        if (position == 1) {
            val adapter = QueueAdapter(context, getData())
            queuelist.adapter = adapter
            progress.visibility = View.GONE
        }
        if (position == 2) {
            val adapter = QueueAdapter(context, getData())
            queuelist.adapter = adapter
            progress.visibility = View.GONE
        }
    }

    private fun getData(): List<QueueModel> {

        val queueList = ArrayList<QueueModel>()
        queueList.add(
            QueueModel(
                "RGM 20, Zarda Bagan, Jyangra, Rajarhat, Kolkata, West Bengal 700059, India",
                "0.1 mile away"
            )
        )
        queueList.add(
            QueueModel(
                "RGM 20, Zarda Bagan, Jyangra, Rajarhat, Kolkata, West Bengal 700059, India",
                "0.1 mile away"
            )
        )
        queueList.add(
            QueueModel(
                "RGM 20, Zarda Bagan, Jyangra, Rajarhat, Kolkata, West Bengal 700059, India",
                "0.1 mile away"
            )
        )
        queueList.add(
            QueueModel(
                "RGM 20, Zarda Bagan, Jyangra, Rajarhat, Kolkata, West Bengal 700059, India",
                "0.1 mile away"
            )
        )
        queueList.add(
            QueueModel(
                "RGM 20, Zarda Bagan, Jyangra, Rajarhat, Kolkata, West Bengal 700059, India",
                "0.1 mile away"
            )
        )
        queueList.add(
            QueueModel(
                "RGM 20, Zarda Bagan, Jyangra, Rajarhat, Kolkata, West Bengal 700059, India",
                "0.1 mile away"
            )
        )
        queueList.add(
            QueueModel(
                "RGM 20, Zarda Bagan, Jyangra, Rajarhat, Kolkata, West Bengal 700059, India",
                "0.1 mile away"
            )
        )
        queueList.add(
            QueueModel(
                "RGM 20, Zarda Bagan, Jyangra, Rajarhat, Kolkata, West Bengal 700059, India",
                "0.1 mile away"
            )
        )
        return queueList
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)