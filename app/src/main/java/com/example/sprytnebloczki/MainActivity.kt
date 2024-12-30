package com.example.sprytnebloczki

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log // Do testów
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

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
    private lateinit var linia: TextView
    private lateinit var rootLayout: FrameLayout
    private val selectedBlocks = mutableListOf<Block>()
    private val activeBlocks = mutableListOf<Block>() // od tąd są nowe zmienne
    private lateinit var usun: TextView
    private var start = false
    private var stop = false

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

        rootLayout = findViewById(R.id.root)
        startBloc=findViewById(R.id.start)
        endBloc=findViewById(R.id.koniec)
        operationBloc=findViewById(R.id.operacja)
        ifBloc=findViewById(R.id.warunek)
        inputBloc=findViewById(R.id.io)
        instruction=findViewById(R.id.instruction)
        linia=findViewById(R.id.linia)

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
        linia.setOnClickListener{

            if (selectedBlocks.size == 2) { // Zaznaczanie dwóch bloczków pod linię
                val block1 = selectedBlocks[0]
                val block2 = selectedBlocks[1]

                val line= LineView(this)
                rootLayout.addView(line)
                block1.addLine(line)
                block2.addLine(line)
                line.setStartBlock(block1)

                line.setLinePoints(
                    block1.getImage().x + block1.getImage().width / 2,
                    block1.getImage().y + block1.getImage().height / 6*5,
                    block2.getImage().x + block2.getImage().width / 2,
                    block2.getImage().y + block2.getImage().height / 6
                )


                val draggable1 = DraggableBloc().apply {
                    setBlock(block1)
                    setLine(line)
                }
                val draggable2 = DraggableBloc().apply {
                    setBlock(block2)
                    setLine(line)
                }

                block1.getImage().setOnTouchListener(draggable1)
                block2.getImage().setOnTouchListener(draggable2)

                selectedBlocks.clear()
                block1.getImage().setBackgroundColor(Color.TRANSPARENT)
                block2.getImage().setBackgroundColor(Color.TRANSPARENT)
            } else {
                Toast.makeText(this, "Please select exactly two blocks!", Toast.LENGTH_SHORT).show()
            }
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

        usun=findViewById(R.id.usun)// to też dodałem

        usun.setOnClickListener{
            if(selectedBlocks.size>0) {
                for (i in selectedBlocks) {
                    for (j in i.getLine()) {
                        rootLayout.removeView(j)
                    }

                    if(i.getType()=="start"){//czy usuwany bloczek to start lub stop
                        start=false
                    }
                    if(i.getType()=="koniec"){
                        stop=false
                    }

                    rootLayout.removeView(i.getImage()) // Usunięcie z widoku
                    activeBlocks.remove(i) // Usunięcie z listy
                }
                selectedBlocks.clear()
            }
            else{
                Toast.makeText(this, "Wpierw zaznacz bloczek do usunięcia!", Toast.LENGTH_SHORT).show()
            }
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

                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                    gravity=Gravity.CENTER
            }


        }
        if((stop==true && type=="koniec")||(start==true && type=="start")){
            Toast.makeText(this, "Może istnieć tylko jeden bloczek start i stop!", Toast.LENGTH_SHORT).show()
        }
        else {

            if(type=="start"){
                start=true
            }
            if(type=="koniec"){
                stop=true
            }

            val editText = EditText(this).apply {
                hint = "Type..."
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                }
                textSize = 16f
            }

            val iconWithText = FrameLayout(this).apply {
                layoutParams =
                    FrameLayout.LayoutParams(360, if (type == "warunek") 360 else 140).apply {
                        leftMargin = 400
                        topMargin = 400
                    }

            }

            iconWithText.addView(icon)
            if (type != "start" && type != "koniec") {
                iconWithText.addView(editText)
            }
            val block = Block(iconWithText, type)
            rootLayout.addView(block.getImage())// Dodanie bloku do widoku
            activeBlocks.add(block) // Dodanie bloku do listy                                           to też

            block.getImage().setOnTouchListener(DraggableItem())


            block.getImage().setOnClickListener {
                selectBlock(block)
            }
        }
    }

    private fun selectBlock(block: Block){
        if (selectedBlocks.contains(block)) {
            selectedBlocks.remove(block)
            block.getImage().setBackgroundColor(Color.TRANSPARENT)
        } else {
            if (selectedBlocks.size < 2) {
                selectedBlocks.add(block)
                block.getImage().setBackgroundColor(Color.LTGRAY)
            }
        }
    }
}

