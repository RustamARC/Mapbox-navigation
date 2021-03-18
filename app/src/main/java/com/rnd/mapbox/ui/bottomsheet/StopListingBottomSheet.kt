package com.rnd.mapbox.ui.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rnd.mapbox.R
import kotlinx.android.synthetic.main.open_stops_listing.view.*

class StopListingBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.open_stops_listing, container, false)
        view.ivDismiss.setOnClickListener {
            dismissAllowingStateLoss()
        }
        val adapter = StopsAdapter(requireContext(), getStops())
        view.rvStopList.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val dialog = dialog as BottomSheetDialog?
                val bottomSheet =
                    dialog!!.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                val behavior: BottomSheetBehavior<*> =
                    BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
            }
        })
    }


    private fun getStops(): List<StopsModel> {
        val stopsList = ArrayList<StopsModel>()
        for (i in 0..10) {
            stopsList.add(StopsModel("Pickup-Taxi", "Joe"))
        }
        return stopsList
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /* if (context is OnSafetyOptionClickListener) {
             safetyOptionClickListener = context
         } else {
             throw ClassCastException("$context must implement OnSafetyOptionClickListener")
         }*/
    }

    interface OnSafetyOptionClickListener {
        fun onSafetyOptionClick(reason: String)
    }
}