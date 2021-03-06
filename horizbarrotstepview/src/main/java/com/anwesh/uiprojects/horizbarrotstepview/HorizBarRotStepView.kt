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
import android.util.Log

val nodes : Int = 5
val rects : Int = 3
val PARTS : Int = 2
val SC_GAP : Float = 0.1f / PARTS
val SIZE_FACTOR : Int = 3
val STROKE_FACTOR : Int = 90
val DELAY : Long = 25

fun Int.getInverse() : Float = 1f / this

fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.getInverse(), Math.max(0f, this - i * n.getInverse())) * n

fun Float.getScaleFactor() : Float = Math.floor(this / PARTS.getInverse().toDouble()).toFloat()

fun Float.updateScale(dir : Float) : Float = SC_GAP * dir * ((1 - getScaleFactor()) * rects.getInverse() + getScaleFactor())

fun Canvas.drawHBRSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / SIZE_FACTOR
    val sc1 : Float = scale.divideScale(0, 2)
    val sc2 : Float = scale.divideScale(1, 2)
    paint.strokeWidth = Math.min(w, h) / STROKE_FACTOR
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = Color.parseColor("#4527A0")
    save()
    translate(gap * (i + 1), h/2)
    rotate(90f * sc2)
    for (j in 0..(rects - 1)) {
        val wr : Float = 2 * size / rects
        val sc : Float = sc1.divideScale(j, rects)
        save()
        translate(-size + j * wr, -wr/2)
        paint.style = Paint.Style.STROKE
        drawRect(0f, 0f, wr, wr, paint)
        paint.style = Paint.Style.FILL
        drawRect(RectF(0f, 0f, wr * sc, wr * sc), paint)
        restore()
    }
    restore()
}

class HorizBarRotStepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            val k : Float = scale.updateScale(dir)
            scale += k
            Log.d("update scale by", "$k")
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(DELAY)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class HBRSNode(var i : Int, val state : State = State()) {
        private var prev : HBRSNode? = null

        private var next : HBRSNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = HBRSNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawHBRSNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : HBRSNode {
            var curr : HBRSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }

    }

    data class HorizBarRotStep(var i : Int) {

        private val root : HBRSNode = HBRSNode(0)

        private var curr : HBRSNode = root

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : HorizBarRotStepView) {

        private val animator : Animator = Animator(view)

        private val hrbs : HorizBarRotStep = HorizBarRotStep(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            hrbs.draw(canvas, paint)
            animator.animate {
                hrbs.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            hrbs.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : HorizBarRotStepView {
            val view : HorizBarRotStepView = HorizBarRotStepView(activity)
            activity.setContentView(view)
            return view
        }
    }
}