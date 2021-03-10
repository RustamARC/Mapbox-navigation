package com.rnd.mapbox.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
//        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    snackbar.setTextColor(Color.RED)
    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}

/**
 * Make a View Blink for a desired duration
 *
 * @param view     view that will be animated
 * @param duration for how long in ms will it blink
 * @param offset   start offset of the animation
 * @return returns the same view with animation properties
 */
fun View.blink(duration: Int, offset: Int): View? {
    val anim: Animation = AlphaAnimation(0.0f, 1.0f)
    anim.duration = duration.toLong()
    anim.startOffset = offset.toLong()
    anim.repeatMode = Animation.REVERSE
    anim.repeatCount = Animation.INFINITE
    this.startAnimation(anim)
    return this
}


fun String.isValidPhoneNumber() =
    this.let { android.util.Patterns.PHONE.matcher(it).matches() }

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Activity.toast(message: String) = this.applicationContext.toast(message)
fun Fragment.toast(message: String) = this.requireActivity().toast(message)