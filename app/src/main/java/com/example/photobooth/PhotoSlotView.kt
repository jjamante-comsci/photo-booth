package com.example.photobooth

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import kotlin.math.max

/** A movable, pinch-resizable photo placeholder used by the frame editor. */
class PhotoSlotView(context: Context, val position: Int) : ImageView(context) {
    private var lastX = 0f; private var lastY = 0f
    private val scale = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val lp = layoutParams as android.widget.FrameLayout.LayoutParams
            lp.width = max(90, (lp.width * detector.scaleFactor).toInt())
            lp.height = max(90, (lp.height * detector.scaleFactor).toInt())
            layoutParams = lp; return true
        }
    })
    init { scaleType = ScaleType.CENTER_CROP; setBackgroundColor(Color.argb(80, 255, 255, 255)); setPadding(3,3,3,3) }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scale.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { lastX = event.rawX; lastY = event.rawY; parent.requestDisallowInterceptTouchEvent(true); return true }
            MotionEvent.ACTION_MOVE -> if (!scale.isInProgress) {
                val lp = layoutParams as android.widget.FrameLayout.LayoutParams
                lp.leftMargin += (event.rawX - lastX).toInt(); lp.topMargin += (event.rawY - lastY).toInt()
                layoutParams = lp; lastX = event.rawX; lastY = event.rawY
            }
        }
        return true
    }
    fun setImage(image: Drawable?) { setImageDrawable(image) }
}
