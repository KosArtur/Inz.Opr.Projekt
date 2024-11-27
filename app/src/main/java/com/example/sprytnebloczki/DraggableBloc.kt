package com.example.sprytnebloczki

import android.view.MotionEvent
import android.view.View

class DraggableBloc: DraggableItem() {
    private lateinit var block: Block
    private lateinit var line: LineView
    private var dX = 0f
    private var dY = 0f

    fun setBlock(block: Block){
        this.block=block
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


                for(line in block.connectedLines)
                {
                    if(line.startBloc==block)
                    {
                        line.setLinePoints(
                            block.getImage().x + block.getImage().width/2,
                            block.getImage().y + block.getImage().height/6*5
                        )
                    }
                    else{
                        line.setLinePoints(
                            endX=block.getImage().x + block.getImage().width/2,
                            endY=block.getImage().y + block.getImage().height/6
                        )
                    }
                }
            }
            MotionEvent.ACTION_UP->{
                view.performClick()
            }
        }
        return true
    }
}