package com.anwesh.uiprojects.horizbarrotstepview

/**
 * Created by anweshmishra on 13/11/18.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.app.Activity
import android.content.Context

val nodes : Int = 5
val rects : Int = 3
val PARTS : Int = 2
val SC_GAP : Float = 0.1f / PARTS
val SIZE_FACTOR : Int = 3
val STROKE_FACTOR : Int = 60

class HorizBarRotStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}