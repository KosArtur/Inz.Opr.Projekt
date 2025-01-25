package com.example.sprytnebloczki

import android.widget.FrameLayout

class IfBlock(image: FrameLayout, type:String): Block(image, type)  {
        private var action: String = ""
    private var firstValue: String = ""
    private  var secondValue: String = ""
    private lateinit var lineTrue: LineView
    private lateinit var lineFalse: LineView
    private var blockTrue:Block? = null
    private var blockFalse: Block? = null

    fun setBlockTrue(block: Block?){
        this.blockTrue=block
    }

    fun setBlockFalse(block: Block?){
        this.blockFalse=block
    }

    fun getBlockTrue(): Block?{
        return this.blockTrue
    }

    fun getBlockFalse(): Block?{
        return this.blockFalse
    }

    override fun getAction(): String? {
        return super.getAction()
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

    fun getLineTrue(): LineView? {
        return if (::lineTrue.isInitialized) lineTrue else null
    }

    fun getLineFalse(): LineView? {
        return if (::lineFalse.isInitialized) lineFalse else null
    }


}