package com.example.sprytnebloczki

import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout

class DraggableBloc: DraggableItem() {
    private lateinit var icon1: FrameLayout
    private lateinit var icon2: FrameLayout
    private lateinit var line: LineView
    private var dX = 0f
    private var dY = 0f

    fun setIcon1(icon: FrameLayout){
        this.icon1=icon
    }
    fun setIcon2(icon: FrameLayout){
        this.icon2=icon
    }
    fun setLine(l:LineView){
        this.line=l
    }

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

                line.setLinePoints(
                    icon1.x + icon1.width/2,
                    icon1.y + icon1.height/6*5,
                    icon2.x + icon2.width / 2,
                    icon2.y + icon2.height /6
                )
            }
            MotionEvent.ACTION_UP->{
                view.performClick()
            }
        }
        return true
    }
}