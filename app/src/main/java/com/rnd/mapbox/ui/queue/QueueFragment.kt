package com.rnd.mapbox.ui.queue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rnd.mapbox.R
import com.rnd.mapbox.databinding.FragmentQueueBinding
import com.rnd.mapbox.ui.home.viewpager.ViewPagerAdapter
import com.rnd.mapbox.ui.queue.adapter.QueueAdapter
import com.rnd.mapbox.ui.queue.model.QueueModel
import com.rnd.mapbox.ui.queue.viewpager.QueueViewPagerAdapter

class QueueFragment : Fragment() {

    private lateinit var queueViewModel: QueueViewModel

    //    private lateinit var binding: FragmentQueueBinding
    var data = MutableLiveData<List<QueueModel>>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        queueViewModel =
            ViewModelProvider(this).get(QueueViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_queue, container, false)

        val queueTabs = root.findViewById<TabLayout>(R.id.queueTabs)


        val viewpager = root.findViewById<ViewPager2>(R.id.queueViewPager)

        viewpager.adapter = QueueViewPagerAdapter(requireContext())

        TabLayoutMediator(queueTabs, viewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Taxi"
                }
                1 -> {
                    tab.text = "Food"
                }
                2 -> {
                    tab.text = "Both"
                }
            }
        }.attach()

        return root
    }
}