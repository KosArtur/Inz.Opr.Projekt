package com.example.sprytnebloczki

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LineView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }


    private var startX: Float = 0f
    private var startY: Float = 0f
    private var endX: Float = 0f
    private var endY: Float = 0f
    private var id: Int=0
    lateinit var startBloc: Block
    lateinit var endBloc: Block

    fun colorRed(){
        paint.color = Color.RED
    }

    fun colorGreen(){
        paint.color = Color.GREEN
    }

    fun setStartBlock(block:Block){
        this.startBloc=block
    }

    fun setEndBlock(block:Block){
        this.endBloc = block
    }

    companion object {
        private var idCounter: Int = 0
    }

        init{
            this.id= idCounter++
        }

    override fun getId(): Int {
        return this.id
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(startX, startY, endX, endY, paint)
    }

    fun setLinePoints(startX: Float=this.startX, startY: Float=this.startY, endX: Float=this.endX, endY: Float=this.endY) {
        this.startX = startX
        this.startY = startY
        this.endX = endX
        this.endY = endY
        invalidate()
    }
}
