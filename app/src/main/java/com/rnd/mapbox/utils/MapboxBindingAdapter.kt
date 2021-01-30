package com.rnd.mapbox.utils

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("navigation:enable")
fun enableNavigationButton(view: View, enable: Boolean) {
    view.visibility = if (enable) View.VISIBLE else View.GONE
}