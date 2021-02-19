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
import com.rnd.mapbox.R
import com.rnd.mapbox.databinding.FragmentQueueBinding
import com.rnd.mapbox.ui.queue.adapter.QueueAdapter
import com.rnd.mapbox.ui.queue.model.QueueModel

class QueueFragment : Fragment() {

    private lateinit var queueViewModel: QueueViewModel
    private lateinit var binding: FragmentQueueBinding
    var data = MutableLiveData<List<QueueModel>>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        queueViewModel =
            ViewModelProvider(this).get(QueueViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_queue, container, false)
        val queueList = root.findViewById<RecyclerView>(R.id.queuelist)
        val progress = root.findViewById<ProgressBar>(R.id.progress)
        queueViewModel.getQueueList().observe(viewLifecycleOwner, Observer {
            data.value = queueViewModel.getQueueList().value
            val adapter = QueueAdapter(requireContext(), data)
            queueList.adapter = adapter
            progress.visibility = View.GONE
        })
        return root
    }
}