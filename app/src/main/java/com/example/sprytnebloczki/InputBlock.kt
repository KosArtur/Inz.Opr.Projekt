package com.example.sprytnebloczki

import android.widget.FrameLayout

class InputBlock(image: FrameLayout, type:String, fieldId: Int=0, private val action: String, private val values: String):
    Block(image, type, fieldId) {
        private var inputType: String = ""
    fun getAction():String{
        return this.action
    }

    fun getUserInput(): String{
        return this.values
    }

    fun setInputType(type: String){
        this.inputType = type
    }

    fun getInputTYpe(): String{
        return this.inputType
    }
}