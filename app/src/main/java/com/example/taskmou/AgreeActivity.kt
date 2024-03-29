package com.example.taskmou

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream


class AgreeActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agree)
        val filePath = "soglasie.docx"
        val text = findViewById<TextView>(R.id.textSoglasie)

        val file = File("app/src/main/java/com/example/taskmou/soglasie.docx")


    }
    /* fun readTxt(){
        try {
            FileInputStream("Javatpoint.docx").use { fis ->
                val file = XWPFDocument(OPCPackage.open(fis))
                val ext = XWPFWordExtractor(file)
                System.out.println(ext.getText())
            }
        } catch (e: Exception) {
            println(e)
        }
    } */

}