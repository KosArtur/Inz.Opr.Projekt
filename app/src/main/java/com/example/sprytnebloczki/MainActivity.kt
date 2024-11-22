package com.example.sprytnebloczki

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {


    private lateinit var buttonRun : TextView
    private lateinit var buttonOptions : TextView
    private lateinit var buttonCode : TextView
    private lateinit var startBloc : ImageView
    private lateinit var endBloc : ImageView
    private lateinit var operationBloc : ImageView
    private lateinit var ifBloc : ImageView
    private lateinit var inputBloc : ImageView
    private lateinit var instruction: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




        startBloc=findViewById(R.id.start)
        endBloc=findViewById(R.id.koniec)
        operationBloc=findViewById(R.id.operacja)
        ifBloc=findViewById(R.id.warunek)
        inputBloc=findViewById(R.id.io)
        instruction=findViewById(R.id.instruction)

        startBloc.setOnClickListener{
           addBloc("start")
            instruction.visibility= View.GONE
        }
        endBloc.setOnClickListener{
            addBloc("koniec")
        }
        operationBloc.setOnClickListener{
            addBloc("operacja")
        }
        ifBloc.setOnClickListener{
            addBloc("warunek")
        }
        inputBloc.setOnClickListener{
            addBloc("input")
        }


        buttonRun=findViewById(R.id.buttonRun)
        buttonOptions=findViewById(R.id.buttonOptions)
        buttonCode=findViewById(R.id.buttonCode)

        buttonOptions.setOnClickListener{

            val popupMenu = PopupMenu(this, buttonOptions, 0,0, R.style.CustomPopupMenu)
            popupMenu.menuInflater.inflate(R.menu.toolbar, popupMenu.menu)


            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_newFile -> {
                        // nowy plik

                        true
                    }
                    R.id.action_saveFile -> {
                        // zapisanie pliku

                        true
                    }
                    R.id.action_readFile -> {
                        // wczytanie pliku

                        true
                    }
                    R.id.action_export -> {
                        // zapisanie do pdf

                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    fun addBloc(type: String ){
        val rootLayout = findViewById<FrameLayout>(R.id.root)
        val icon = ImageView(this).apply {

            val imageResource = when (type) {
                "koniec" -> R.drawable.koniec
                "operacja" -> R.drawable.operacja
                "input" -> R.drawable.input
                "warunek" -> R.drawable.warunek
                else -> R.drawable.start
            }
            setImageResource(imageResource)
            if(type=="start" || type=="koniec"){
                layoutParams = FrameLayout.LayoutParams(300, 300).apply {

                    leftMargin = 100
                    topMargin = 100
                }
            } else{
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {

                    gravity=Gravity.CENTER
                }
            }
        }

        if(type=="start" || type=="koniec"){
            rootLayout.addView(icon)
            icon.setOnTouchListener(DraggableItem())
        } else{

            val editText = EditText(this).apply {
                hint = "Type..."
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER
                }
                textSize = 16f
            }


            val iconWithText = FrameLayout(this).apply {
                layoutParams = FrameLayout.LayoutParams(360 , if(type == "warunek") 360 else 140).apply {
                    leftMargin =400
                    topMargin = 400
                }

            }
            iconWithText.addView(icon)
            iconWithText.addView(editText)
            rootLayout.addView(iconWithText)
            iconWithText.setOnTouchListener(DraggableItem())
        }
    }
}

