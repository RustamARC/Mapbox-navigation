package com.rnd.mapbox.ui.queue

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rnd.mapbox.ui.queue.model.QueueModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QueueViewModel : ViewModel() {

    private var mQueueList: MutableLiveData<List<QueueModel>>? = null

    internal fun getQueueList(): MutableLiveData<List<QueueModel>> {
        if (mQueueList == null) {
            mQueueList = MutableLiveData()
            loadQueue()
        }
        return mQueueList as MutableLiveData<List<QueueModel>>
    }

    private fun loadQueue() {
        // do async operation to fetch users

        CoroutineScope(Dispatchers.IO).launch {

            mQueueList!!.postValue(fetchQueueData())
        }
        /* val myHandler = Handler(Looper.getMainLooper())
         myHandler.postDelayed({

             mQueueList!!.postValue(queueList)

         }, 5000)*/

    }

    private fun fetchQueueData(): ArrayList<QueueModel> {
        Thread.sleep(2000)
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
        queueList.add(
            QueueModel(
                "RGM 20, Zarda Bagan, Jyangra, Rajarhat, Kolkata, West Bengal 700059, India",
                "0.1 mile away"
            )
        )
        return queueList
    }
}