package com.rnd.mapbox.ui

import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.rnd.mapbox.utils.ConnectionStateMonitor
import com.rnd.mapbox.utils.snackbar

open class BaseActivity : AppCompatActivity(), ConnectionStateMonitor.OnNetworkAvailableCallbacks {

    protected var connectionStateMonitor: ConnectionStateMonitor? = null
    private var viewGroup: ViewGroup? = null

    override fun onResume() {
        super.onResume()
        if (viewGroup == null) viewGroup = findViewById(android.R.id.content)
        if (connectionStateMonitor == null)
            connectionStateMonitor = ConnectionStateMonitor(this, this)
        //Register
        connectionStateMonitor?.enable()
        // Recheck network status manually whenever activity resumes
        if (connectionStateMonitor?.hasNetworkConnection() == false) onNetworkDisconnected()
        else onNetworkConnected()
    }

    override fun onPause() {
        //Unregister
        connectionStateMonitor?.disable()
        connectionStateMonitor = null
        super.onPause()
    }

    override fun onNetworkConnected() {
//        viewGroup?.snackbar("Internet connected!")
    }

    override fun onNetworkDisconnected() {
        viewGroup?.snackbar("No internet connection!")
    }

}