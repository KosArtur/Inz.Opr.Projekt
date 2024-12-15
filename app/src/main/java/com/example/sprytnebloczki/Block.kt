package com.example.sprytnebloczki

import android.widget.FrameLayout

class Block(private val image:FrameLayout) {
    private var id: Int=0
    val connectedLines = mutableListOf<LineView>()

    companion object {
        private var idCounter: Int = 0
    }

    init{
        this.id= idCounter++
    }

    fun getId(): Int {
        return this.id
    }
    fun getImage(): FrameLayout {
        return this.image
    }

    fun addLine(line: LineView) {
        connectedLines.add(line)
    }
}