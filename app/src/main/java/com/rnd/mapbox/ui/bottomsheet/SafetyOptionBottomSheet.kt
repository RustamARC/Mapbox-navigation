package com.rnd.mapbox.ui.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rnd.mapbox.R
import kotlinx.android.synthetic.main.safety_option_bottomsheet.view.*
import java.lang.ClassCastException

class SafetyOptionBottomSheet : BottomSheetDialogFragment() {

    lateinit var safetyOptionClickListener: OnSafetyOptionClickListener
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.safety_option_bottomsheet, container, false)
        view.safety1.setOnClickListener {
            safetyOptionClickListener.onSafetyOptionClick("Taxi punctured")
            dismissAllowingStateLoss()
        }
        view.safety2.setOnClickListener {
            safetyOptionClickListener.onSafetyOptionClick("Driver not feeling well")
            dismissAllowingStateLoss()
        }
        view.safety3.setOnClickListener {
            safetyOptionClickListener.onSafetyOptionClick("Other")
            dismissAllowingStateLoss()
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSafetyOptionClickListener) {
            safetyOptionClickListener = context
        } else {
            throw ClassCastException("$context must implement OnSafetyOptionClickListener")
        }
    }

    interface OnSafetyOptionClickListener {
        fun onSafetyOptionClick(reason: String)
    }
}