package com.example.sprytnebloczki

import android.view.MotionEvent
import android.view.View

open class DraggableItem: View.OnTouchListener {

    private var dX = 0f
    private var dY = 0f


    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                dX = view.x - event.rawX
                dY = view.y - event.rawY

            }
            MotionEvent.ACTION_MOVE -> {

                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
            }

            MotionEvent.ACTION_UP->{
                view.performClick()
            }
        }
        return true
    }
}

