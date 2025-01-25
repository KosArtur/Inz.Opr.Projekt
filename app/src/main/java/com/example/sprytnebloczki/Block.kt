package com.example.sprytnebloczki

import android.content.Context
import android.widget.FrameLayout

open class Block(private val image:FrameLayout, private val type:String) {
    private var id: Int=0
    var connectedLines = mutableListOf<LineView>()
    private var previousBlock: Block?= null
    private var nextBlock: Block?= null
    private var action: String? = null
    val context: Context = image.context // Tutaj używamy contextu z FrameLayoutu

    companion object {
        private var idCounter: Int = 0

        fun resetIdCounter() {
            idCounter = 0
        }
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

    open fun getImage(): FrameLayout {
        return this.image
    }

    open fun addLine(line: LineView) {
        connectedLines.add(line)
    }

    open fun getLine(): MutableList<LineView> {
        return connectedLines
    }

    open fun setPreviousBlock(block:Block){
        this.previousBlock = block
    }
    open fun setNextBlock(block: Block?){
        this.nextBlock = block
    }

    open fun getPreviousBlock(): Block?{
        return this.previousBlock
    }
    open fun getNextBlock(): Block?{
        return this.nextBlock
    }


    open fun getAction(): String? {
        return this.action
    }

    open fun setAction(action: String) {
        this.action = action
    }
}