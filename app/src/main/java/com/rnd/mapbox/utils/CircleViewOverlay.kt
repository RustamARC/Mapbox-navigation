package com.rnd.mapbox.utils

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import com.rnd.mapbox.R

class CircleOverlayView : LinearLayout {
    private var bitmap: Bitmap? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (bitmap == null) {
            createWindowFrame()
        }
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
    }

    private fun createWindowFrame() {
        bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
        val osCanvas = Canvas(bitmap!!)
        val outerRectangle = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = resources.getColor(R.color.white_transparent, resources.newTheme())
//        paint.alpha = 100
        osCanvas.drawRect(outerRectangle, paint)
        paint.color = Color.TRANSPARENT
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        val centerX = width / 2.toFloat()
        val centerY = height / 2.toFloat()
        val radius = 200.toFloat()
        osCanvas.drawCircle(centerX, centerY, radius, paint)
    }

    override fun isInEditMode(): Boolean {
        return true
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        super.onLayout(changed, l, t, r, b)
        bitmap = null
    }
}