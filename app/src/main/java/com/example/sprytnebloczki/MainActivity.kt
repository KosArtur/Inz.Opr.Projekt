package com.example.sprytnebloczki

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var buttonRun : TextView;
    private lateinit var buttonOptions : TextView;
    private lateinit var buttonCode : TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonRun=findViewById(R.id.buttonRun)
        buttonOptions=findViewById(R.id.buttonOptions)
        buttonCode=findViewById(R.id.buttonCode)

        buttonOptions.setOnClickListener(){

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




}