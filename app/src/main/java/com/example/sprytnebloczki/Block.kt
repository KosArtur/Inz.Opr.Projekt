package com.example.sprytnebloczki

import android.widget.FrameLayout

open class Block(private val image:FrameLayout, private val type:String, private var fieldId: Int=0) {
    private var id: Int=0
    val connectedLines = mutableListOf<LineView>()
    private val Line: MutableList<LineView> = mutableListOf()

    companion object {
        private var idCounter: Int = 0
    }

    init{
        this.id= idCounter++
    }

    open fun getType(): String{
        return this.type
    }



    fun getId(): Int {
        return this.id
    }

    fun getFieldId():Int{
        return this.fieldId
    }
    open fun getImage(): FrameLayout {
        return this.image
    }

    open fun addLine(line: LineView) {
        Line.add(line)
        connectedLines.add(line)
    }

    open fun getLine(): MutableList<LineView> {
        return Line
    }


}