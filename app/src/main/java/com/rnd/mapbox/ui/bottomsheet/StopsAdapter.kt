package com.rnd.mapbox.ui.bottomsheet

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.rnd.mapbox.R
import com.rnd.mapbox.ui.queue.model.QueueModel
import kotlinx.android.synthetic.main.queue_item.view.*
import kotlinx.android.synthetic.main.stop_item.view.*

class StopsAdapter(val ctx: Context, val data: List<StopsModel>) :
    RecyclerView.Adapter<ViewH>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewH {
        return ViewH(LayoutInflater.from(ctx).inflate(R.layout.stop_item, p0, false))
    }

    override fun getItemCount(): Int {
        return data.size

    }

    override fun onBindViewHolder(p0: ViewH, p1: Int) {

        p0.bindItems(data.get(p1))

    }
}

class ViewH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindItems(model: StopsModel) {
        val stopName = itemView.tvStopName
        val customerName = itemView.tvCustomerName
        stopName.text = model.stops
        customerName.text = model.custName


    }
}