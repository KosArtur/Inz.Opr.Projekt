package com.example.sprytnebloczki

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.Log // Do testów
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {


    private lateinit var buttonRun: TextView
    private lateinit var buttonOptions: TextView
    private lateinit var buttonCode: TextView
    private lateinit var startBloc: ImageView
    private lateinit var endBloc: ImageView
    private lateinit var operationBloc: ImageView
    private lateinit var ifBloc: ImageView
    private lateinit var inputBloc: ImageView
    private lateinit var instruction: TextView
    private lateinit var linia: TextView
    private lateinit var rootLayout: FrameLayout
    private val selectedBlocks = mutableListOf<Block>()
    private val executionSequence = mutableListOf<Block>()
    private val activeBlocks = mutableListOf<Block>() // od tąd są nowe zmienne
    private lateinit var usun: TextView
    private var start = false
    private var stop = false
    private val inputMapNumber: MutableMap<String, Double?> = mutableMapOf()
    private val inputMapString: MutableMap<String, String?> = mutableMapOf()
    private val inputMapTabString: MutableMap<String, List<String>?> = mutableMapOf()
    private val inputMapTabNumber: MutableMap<String, List<Double>?> = mutableMapOf()

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
        startBloc = findViewById(R.id.start)
        endBloc = findViewById(R.id.koniec)
        operationBloc = findViewById(R.id.operacja)
        ifBloc = findViewById(R.id.warunek)
        inputBloc = findViewById(R.id.io)
        instruction = findViewById(R.id.instruction)
        linia = findViewById(R.id.linia)

        startBloc.setOnClickListener {
            addBloc("start")
            instruction.visibility = View.GONE
        }
        endBloc.setOnClickListener {
            addBloc("koniec")
        }
        operationBloc.setOnClickListener {
            addBloc("operacja")
        }
        ifBloc.setOnClickListener {
            addBloc("warunek")
        }
        inputBloc.setOnClickListener {
            addBloc("input")
        }
        linia.setOnClickListener {

            if (selectedBlocks.size == 2) { // Zaznaczanie dwóch bloczków pod linię
                val block1 = selectedBlocks[0]
                val block2 = selectedBlocks[1]


                if (block2.getType() == "start") {
                    Toast.makeText(this, "Bloczek Start musi byc pierwszy!", Toast.LENGTH_LONG)
                        .show()
                } else if (block1.getType() == "koniec") {
                    Toast.makeText(this, "Bloczek Koniec musi byc ostatni!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    if ((block1.getType() == "start" && block1.connectedLines.size == 1) || (block2.getType() == "koniec" && block2.connectedLines.size == 1)) {
                        Toast.makeText(
                            this,
                            "Bloczki Start i Koniec mogą mieć tylko jedno połączenie!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (((block1.getType() == "input" || block1.getType() == "operacja") && block1.connectedLines.size == 2)
                        || ((block2.getType() == "input" || block2.getType() == "operacja") && block2.connectedLines.size == 2)
                    ) {
                        Toast.makeText(
                            this,
                            "Bloczki Input i Operacja mogą mieć tylko dwa połączenia!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if ((block1.getType() == "warunek" && block1.connectedLines.size == 3) || (block2.getType() == "warunek" && block2.connectedLines.size == 3)) {
                        Toast.makeText(
                            this,
                            "Bloczek Warunkowy może mieć tylko trzy połączenia!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val line = LineView(this)

                        if (block1.getType() == "warunek") {
                            val ifBlock = block1 as IfBlock
                            if (ifBlock.getBlockTrue() == null && ifBlock.getBlockFalse() == null) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    val result = showAskDialog()
                                    if (result) {
                                        line.colorGreen()
                                        ifBlock.setBlockTrue(block2)
                                        println(true)
                                    } else {
                                        println("Użytkownik wybrał: Nie")
                                        line.colorRed()
                                        ifBlock.setBlockFalse(block2)
                                    }
                                    rootLayout.addView(line)
                                }
                            } else {
                                if (ifBlock.getBlockTrue() == null) {
                                    line.colorGreen()
                                    ifBlock.setBlockTrue(block2)
                                } else {
                                    line.colorRed()
                                    ifBlock.setBlockFalse(block2)
                                }
                                rootLayout.addView(line)
                            }

                        } else {
                            rootLayout.addView(line)
                        }

                        block1.addLine(line)
                        block2.addLine(line)
                        line.setStartBlock(block1)
                        line.setEndBlock(block2)
                        if (block1 in executionSequence) {
                            executionSequence.add(executionSequence.indexOf(block1) + 1, block2)

                        } else if (block2 in executionSequence) {
                            executionSequence.add(executionSequence.indexOf(block2), block1)
                        } else {
                            executionSequence.add(block1)
                            executionSequence.add(block2)
                        }


                        line.setLinePoints(
                            block1.getImage().x + block1.getImage().width / 2,
                            block1.getImage().y + block1.getImage().height / 6 * 5,
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
                    }
                }

            } else {
                Toast.makeText(this, "Please select exactly two blocks!", Toast.LENGTH_SHORT).show()
            }
        }


        buttonRun = findViewById(R.id.buttonRun)

        buttonRun.setOnClickListener {
            println(executionSequence[executionSequence.size - 1].getType())
            if (executionSequence.first()
                    .getType() != "start" || executionSequence[executionSequence.size - 1].getType() != "koniec"
            ) {
                Toast.makeText(this, "Nieprawidłowa kolejność bloczków", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    for (block in executionSequence) {
                        block.getImage().setBackgroundColor(Color.GREEN)
                        delay(3000)
                        if (block.getType() == "input") {
                            val inputBlock = block as? InputBlock
                            val values = inputBlock?.getUserInput()?.split(",")
                            if (inputBlock?.getAction() == "Read" && values!!.isNotEmpty()) {
                                //show dialog do wprowadzania
                                for (el in values) {
                                    val userInput = showInputDialog(el, inputBlock)
                                    if (inputBlock.getInputTYpe() == "Number") {
                                        inputMapNumber[el] = userInput?.toDouble()
                                    } else if (inputBlock.getInputTYpe() == "String") {
                                        inputMapString[el] = userInput
                                    } else {
                                        val temp = userInput?.split(",")
                                        if (inputBlock.getInputTYpe() == "Array Number") {
                                            inputMapTabNumber[el] = temp?.map { e -> e.toDouble() }
                                        } else if (inputBlock.getInputTYpe() == "Array String") {
                                            inputMapTabString[el] = temp
                                        }
                                    }
                                }
                            } else {
                                //show wartości
                                showOutputDialog(values)
                            }

                        } else if (block.getType() == "operation") {
                            val operationBlock = block as? OperationBlock
                            val value1 = operationBlock?.getFirstValue()
                            val value2 = operationBlock?.getSecondValue()
                            val value3 = operationBlock?.getThirdValue()

                        } else if (block.getType() == "warunek") {
                            val ifBlock = block as? IfBlock

                        }
                        block.getImage().setBackgroundColor(Color.TRANSPARENT)
                    }
                }
            }
        }
        buttonOptions = findViewById(R.id.buttonOptions)
        buttonCode = findViewById(R.id.buttonCode)

        buttonOptions.setOnClickListener {

            val popupMenu = PopupMenu(this, buttonOptions, 0, 0, R.style.CustomPopupMenu)
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
                        val layout = findViewById<View>(R.id.main)
                        CoroutineScope(Dispatchers.Main).launch {
                            val name = showPDFDialog()
                            saveViewAsPdf(this@MainActivity, layout, name!!)
                        }
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()

        }

        usun = findViewById(R.id.usun)// to też dodałem

        usun.setOnClickListener {
            if (selectedBlocks.size > 0) {
                for (i in selectedBlocks) {
                    for (j in i.getLine().toList()) {
                        if (i == j.startBloc) {
                            j.endBloc.connectedLines.remove(j)
                        } else {
                            j.startBloc.connectedLines.remove(j)
                        }
                        i.getLine().remove(j)
                        rootLayout.removeView(j)
                    }

                    if (i.getType() == "start") {//czy usuwany bloczek to start lub stop
                        start = false
                    }
                    if (i.getType() == "koniec") {
                        stop = false
                    }

                    rootLayout.removeView(i.getImage()) // Usunięcie z widoku
                    activeBlocks.remove(i) // Usunięcie z listy
                    executionSequence.remove(i)
                }
                selectedBlocks.clear()
            } else {
                Toast.makeText(this, "Wpierw zaznacz bloczek do usunięcia!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    fun addBloc(type: String) {
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

            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }
        if ((stop && type == "koniec") || (start && type == "start")) {
            Toast.makeText(
                this,
                "Może istnieć tylko jeden bloczek start i stop!",
                Toast.LENGTH_SHORT
            ).show()
        } else {

            if (type == "start") {
                start = true
            }
            if (type == "koniec") {
                stop = true
            }

            val iconWithText = FrameLayout(this).apply {
                layoutParams =
                    FrameLayout.LayoutParams(340, if (type == "warunek") 280 else 240).apply {
                        leftMargin = 400
                        topMargin = 400
                    }
            }

            iconWithText.addView(icon)

            if (type == "input") {
                var action: String
                var userInput: String

                val builder = AlertDialog.Builder(this)
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.custom_dialog, null)

                val userInputField = dialogLayout.findViewById<EditText>(R.id.values)
                val submitButton = dialogLayout.findViewById<Button>(R.id.submit_button)
                val spinner = dialogLayout.findViewById<Spinner>(R.id.spinnerOptions)
                val options = listOf("Read", "Write")
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                builder.setView(dialogLayout)
                builder.setCancelable(false)

                val dialog = builder.create()

                submitButton.setOnClickListener {
                    userInput =
                        userInputField.text.toString() //wymagane wpisanie w formacie a,b,c,d
                    action = spinner.selectedItem.toString()
                    if (userInput == "") {
                        Toast.makeText(this, "Puste pole!", Toast.LENGTH_LONG).show()
                    } else {
                        dialog.dismiss()

                        val values = TextView(this).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = Gravity.CENTER
                            }
                            text = "$action $userInput"
                            textSize = 16f
                        }

                        iconWithText.addView(values)
                        val bloc = InputBlock(iconWithText, type, action, userInput)
                        rootLayout.addView(bloc.getImage())// Dodanie bloku do widoku
                        activeBlocks.add(bloc)
                        bloc.getImage().setOnTouchListener(DraggableItem())

                        bloc.getImage().setOnClickListener {
                            selectBlock(bloc)
                        }
                    }
                }
                dialog.show()
            } else {
                if (type == "operacja") {
                    val block = OperationBlock(iconWithText, type)

                    rootLayout.addView(block.getImage())// Dodanie bloku do widoku
                    activeBlocks.add(block)
                    block.getImage().setOnTouchListener(DraggableItem())

                    block.getImage().setOnClickListener {
                        selectBlock(block)
                        showOperationDialog(block)
                    }
                } else if (type == "warunek") {
                    val block = IfBlock(iconWithText, type)

                    rootLayout.addView(block.getImage())// Dodanie bloku do widoku
                    activeBlocks.add(block)
                    block.getImage().setOnTouchListener(DraggableItem())

                    block.getImage().setOnClickListener {
                        selectBlock(block)
                        showIfDialog(block)
                    }
                } else {
                    val block = Block(iconWithText, type)

                    rootLayout.addView(block.getImage())// Dodanie bloku do widoku
                    activeBlocks.add(block)
                    block.getImage().setOnTouchListener(DraggableItem())

                    block.getImage().setOnClickListener {
                        selectBlock(block)
                    }
                }
            }
        }
    }

    private fun selectBlock(block: Block) {
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


    private suspend fun showInputDialog(zmienna: String, block: InputBlock): String? =
        suspendCancellableCoroutine { continuation ->
            val builder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val dialogLayout = inflater.inflate(R.layout.input_dialog, null)

            val spinner = dialogLayout.findViewById<Spinner>(R.id.spinnerOptions)
            val options = listOf("Number", "String", "Array Number", "Array String")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            val editText = dialogLayout.findViewById<EditText>(R.id.values)
            dialogLayout.findViewById<TextView>(R.id.zmienna).text = zmienna
            val submitButton = dialogLayout.findViewById<Button>(R.id.submit_button)

            builder.setView(dialogLayout)
            builder.setCancelable(false)

            val dialog = builder.create()

            submitButton.setOnClickListener {
                val type = spinner.selectedItem.toString()
                val userInput = editText.text.toString()
                block.setInputType(type)

                if (userInput == "") {
                    Toast.makeText(this, "Puste pole!", Toast.LENGTH_LONG).show()
                } else {
                    if (continuation.isActive) {
                        continuation.resume(userInput) // Wznowienie korutyny z wprowadzonym tekstem
                    }
                    dialog.dismiss()
                }

            }

            dialog.setOnCancelListener {
                continuation.resume(null)
            }

            dialog.show()
        }

    private suspend fun showOutputDialog(values: List<String>?): String? =
        suspendCancellableCoroutine { continuation ->
            val builder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val dialogLayout = inflater.inflate(R.layout.output_dialog, null)

            val valuesField = dialogLayout.findViewById<TextView>(R.id.values)
            val closeButton = dialogLayout.findViewById<Button>(R.id.close_button)

            var output = ""
            if (values!!.isNotEmpty()) {
                for (el in values) {
                    when (el) {
                        in inputMapNumber -> {
                            output += "$el = ${inputMapNumber[el]}\n"
                        }

                        in inputMapString -> {
                            output += "$el = ${inputMapString[el]}\n"
                        }

                        in inputMapTabNumber -> {
                            output += "$el = ${inputMapTabNumber[el]}\n"
                        }

                        in inputMapTabString -> {
                            output += "$el = ${inputMapTabString[el]}\n"
                        }

                        else -> {
                            Toast.makeText(this, "Niezdefiniowana zmienna: $el", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                }
            }

            valuesField.text = output

            builder.setView(dialogLayout)
            builder.setCancelable(false)

            val dialog = builder.create()

            closeButton.setOnClickListener {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
                dialog.dismiss()
            }

            dialog.setOnCancelListener {
                continuation.resume(null)
            }

            dialog.show()
        }

    private fun showIfDialog(block: IfBlock) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.if_dialog, null)

        val userInputField1 = dialogLayout.findViewById<EditText>(R.id.value1)
        val userInputField2 = dialogLayout.findViewById<EditText>(R.id.value2)
        val submitButton = dialogLayout.findViewById<Button>(R.id.submit_button)
        val dismissButton = dialogLayout.findViewById<Button>(R.id.dismiss_button)
        val spinner = dialogLayout.findViewById<Spinner>(R.id.spinnerOptions)
        val options = listOf("=", "!=", ">", ">=", "<", "<=")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        builder.setView(dialogLayout)
        builder.setCancelable(false)

        val dialog = builder.create()

        dismissButton.setOnClickListener {
            dialog.dismiss()
        }

        submitButton.setOnClickListener {
            val userInput1 = userInputField1.text.toString()
            val userInput2 = userInputField2.text.toString()
            val action = spinner.selectedItem.toString()
            if (userInput1 == "" || userInput2 == "") {
                Toast.makeText(this, "Puste pole!", Toast.LENGTH_LONG).show()
            } else {
                dialog.dismiss()

                val values = TextView(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                    text = "$userInput1 $action $userInput2"
                    textSize = 16f
                }
                if (block.getImage().childCount > 1) {
                    block.getImage().removeViewAt(1)
                }


                block.getImage().addView(values)
                block.setFirstValue(userInput1)
                block.setSecondValue(userInput2)
                block.setAction(action)
            }

        }
        dialog.show()
    }

    private fun showOperationDialog(block: OperationBlock) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.operation_dialog, null)

        val userInputField1 = dialogLayout.findViewById<EditText>(R.id.value1)
        val userInputField2 = dialogLayout.findViewById<EditText>(R.id.value2)
        val userInputField3 = dialogLayout.findViewById<EditText>(R.id.value3)
        val userInputField4 = dialogLayout.findViewById<EditText>(R.id.value4)
        val userInputField5 = dialogLayout.findViewById<EditText>(R.id.value5)
        userInputField1.isEnabled = false
        userInputField2.isEnabled = false
        userInputField3.isEnabled = false
        userInputField4.isEnabled = false
        userInputField5.isEnabled = false
        val submitButton = dialogLayout.findViewById<Button>(R.id.submit_button)
        val dismissButton = dialogLayout.findViewById<Button>(R.id.dismiss_button)
        val radioGroup = dialogLayout.findViewById<RadioGroup>(R.id.radioGroup)
        val spinner = dialogLayout.findViewById<Spinner>(R.id.spinnerOptions)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButton1 -> {
                    userInputField1.isEnabled = true
                    userInputField2.isEnabled = true

                    userInputField3.isEnabled = false
                    userInputField4.isEnabled = false
                    userInputField5.isEnabled = false
                    userInputField3.text.clear()
                    userInputField4.text.clear()
                    userInputField5.text.clear()
                }

                R.id.radioButton2 -> {
                    userInputField1.isEnabled = false
                    userInputField2.isEnabled = false
                    userInputField1.text.clear()
                    userInputField2.text.clear()

                    userInputField3.isEnabled = true
                    userInputField4.isEnabled = true
                    userInputField5.isEnabled = true
                }
            }
        }
        val options = listOf("+", "-", "*", "/", "%", "**")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        builder.setView(dialogLayout)
        builder.setCancelable(false)

        val dialog = builder.create()

        dismissButton.setOnClickListener {
            dialog.dismiss()
        }

        submitButton.setOnClickListener {
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioButton1 -> {
                    val userInput1 = userInputField1.text.toString()
                    val userInput2 = userInputField2.text.toString()

                    if (userInput1 == "" || userInput2 == "") {
                        Toast.makeText(this, "Puste pole!", Toast.LENGTH_LONG).show()
                    } else {
                        dialog.dismiss()

                        val values = TextView(this).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = Gravity.CENTER
                            }
                            text = "$userInput1 = $userInput2"
                            textSize = 16f
                        }
                        if (block.getImage().childCount > 1) {
                            block.getImage().removeViewAt(1)
                        }

                        block.getImage().addView(values)

                        block.setFirstValue(userInput1)
                        block.setSecondValue(userInput2)
                    }

                }

                R.id.radioButton2 -> {
                    val userInput3 = userInputField3.text.toString()
                    val userInput4 = userInputField4.text.toString()
                    val userInput5 = userInputField5.text.toString()
                    val action = spinner.selectedItem.toString()

                    if (userInput3 == "" || userInput4 == "" || userInput5 == "") {
                        Toast.makeText(this, "Puste pole!", Toast.LENGTH_LONG).show()
                    } else {
                        dialog.dismiss()

                        val values = TextView(this).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = Gravity.CENTER
                            }
                            text = "$userInput3 = $userInput4 $action $userInput5"
                            textSize = 16f
                        }
                        if (block.getImage().childCount > 1) {
                            block.getImage().removeViewAt(1)
                        }

                        block.getImage().addView(values)

                        block.setFirstValue(userInput3)
                        block.setSecondValue(userInput4)
                        block.setThirdValue(userInput5)
                        block.setAction(action)
                    }
                }
            }
        }
        dialog.show()
    }

    private suspend fun showAskDialog(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Opcje bloku warunkowego")
                .setMessage("Czy ta akcja ma się wykonać, gdy wynik instrukcji warunkowej w bloku będzie true?")
                .setPositiveButton("Tak") { dialog, _ ->
                    continuation.resume(true)
                    dialog.dismiss()
                }
                .setNegativeButton("Nie") { dialog, _ ->
                    continuation.resume(false)
                    dialog.dismiss()
                }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private suspend fun showPDFDialog(): String? = suspendCancellableCoroutine { continuation ->
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogLayout = inflater.inflate(R.layout.pdf_dialog, null)

        val editText = dialogLayout.findViewById<EditText>(R.id.name)

        val submitButton = dialogLayout.findViewById<Button>(R.id.submit_button)

        builder.setView(dialogLayout)
        builder.setCancelable(false)

        val dialog = builder.create()

        submitButton.setOnClickListener {

            val filename = editText.text.toString()

            if (filename == "") {
                Toast.makeText(this, "Puste pole!", Toast.LENGTH_LONG).show()
            } else {
                if (continuation.isActive) {
                    continuation.resume(filename)
                }
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun saveViewAsPdf(context: Context, view: View, fileName: String) {
        // bitmapa z widoku
        val bitmap = getBitmapFromView(view)

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        //zapisuje plik PDF do pamięciu zewnętzrnej, mozna go zobaczyc a aplikacji Files/Downloads
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "$fileName.pdf")
        try {
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()
            Toast.makeText(context, "PDF zapisany: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Błąd podczas zapisywania PDF: ${e.message}", Toast.LENGTH_LONG)
                .show()
        }
    }
}

