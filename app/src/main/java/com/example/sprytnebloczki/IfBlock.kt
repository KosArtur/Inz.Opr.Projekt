package com.example.sprytnebloczki

import android.widget.FrameLayout

class IfBlock(image: FrameLayout, type:String):
    Block(image, type)  {
        private var action: String = ""
    private var firstValue: String = ""
    private  var secondValue: String = ""
    private lateinit var lineTrue: LineView
    private lateinit var lineFalse: LineView

    fun setAction(action: String){
        this.action = action
    }

    fun getAction():String{
        return this.action
    }

    fun getFirstValue():String{
        return this.firstValue
    }

    fun getSecondValue():String{
        return this.secondValue
    }

    fun setFirstValue(value: String){
        this.firstValue = value
    }

    fun setSecondValue(value: String){
        this.secondValue = value
    }

    fun setLineTrue(line: LineView){
        this.lineTrue = line
    }

    fun setLineFalse(line: LineView){
        this.lineFalse = line
    }

    fun getLineTrue():LineView{
        return this.lineTrue
    }

    fun getLineFalse():LineView{
        return this.lineFalse
    }


}