package com.example.sprytnebloczki

import android.widget.FrameLayout

class InputBlock(image: FrameLayout, type:String, private val action: String, private var values: String):
    Block(image, type) {
        private var inputType: String = ""


    override fun getAction(): String? {
        return super.getAction()
    }

    fun getUserInput(): String{
        return this.values
    }

    fun setUserInput(values: String){
        this.values = values
    }

    fun getInputType(): String{
        return this.inputType
    }

    fun setInputType(type: String){
        this.inputType = type
    }

    fun getInputTYpe(): String{
        return this.inputType
    }
}