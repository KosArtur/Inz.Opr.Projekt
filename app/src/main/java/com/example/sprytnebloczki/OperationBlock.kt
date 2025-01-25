package com.example.sprytnebloczki

import android.widget.FrameLayout

class OperationBlock(image: FrameLayout, type:String): Block(image, type)  {
        private var action: String? = null
    private var firstValue: String = ""
    private  var secondValue: String = ""
    private var thirdValue: String? =null

    fun getFirstValue():String{
        return this.firstValue
    }

    fun getSecondValue():String{
        return this.secondValue
    }

    fun getThirdValue():String?{
        return this.thirdValue
    }

    fun setFirstValue(value: String){
        this.firstValue = value
    }

    fun setSecondValue(value: String){
        this.secondValue = value
    }

    fun setThirdValue(value: String){
        this.thirdValue = value
    }

    override fun getAction(): String? {
        return super.getAction()
    }


}