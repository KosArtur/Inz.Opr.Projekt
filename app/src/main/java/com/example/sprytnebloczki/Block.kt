package com.example.sprytnebloczki

import android.widget.FrameLayout

class Block(private val image:FrameLayout, private val type:String, private var fieldId: Int=0, private val action: String? = null, private val values: String?=null) {
    private var id: Int=0
    val connectedLines = mutableListOf<LineView>()
    private val Line: MutableList<LineView> = mutableListOf()

    companion object {
        private var idCounter: Int = 0
    }

    init{
        this.id= idCounter++
    }

    fun getType(): String{
        return this.type
    }

    fun getAction():String?{
        return this.action
    }

    fun getUserInput(): String?{
        return this.values
    }

    fun getId(): Int {
        return this.id
    }

    fun getFieldId():Int{
        return this.fieldId
    }
    fun getImage(): FrameLayout {
        return this.image
    }

    fun addLine(line: LineView) {
        Line.add(line)
        connectedLines.add(line)
    }

    fun getLine(): MutableList<LineView> {
        return Line
    }
}