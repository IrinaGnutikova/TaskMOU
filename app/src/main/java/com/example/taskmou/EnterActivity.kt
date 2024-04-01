package com.example.taskmou

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EnterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter)
        val reg = findViewById<TextView>(R.id.tvGoReg)
        if(!ErrDialog().checkForInternet(this)){
            ErrDialog().showDialog(this)
        }

        reg.setOnClickListener {
            val intent = Intent(this, RegistrActivity::class.java)
            startActivity(intent)
        }
        auth = Firebase.auth
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
                Toast.makeText(baseContext, "С возвращением",
                    Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
        }

        val login = findViewById<Button>(R.id.btnEnter)

        login.setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail)
            val pass = findViewById<EditText>(R.id.etPass)
            val email1 = email.text.toString()
            val password = pass.text.toString()

            if (email.text.isEmpty() || pass.text.isEmpty()){
                Toast.makeText(baseContext, "Пожалуйста, заполните все поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email1, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        Toast.makeText(baseContext, "Вы вошли успешно!",
                            Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    }
                    else {

                        Toast.makeText(baseContext, "Ошибка входа. Попробуйте еще раз",
                            Toast.LENGTH_SHORT).show()

                    }
                }
                .addOnFailureListener{

                    Toast.makeText(baseContext, "Ошибка входа. ${it.localizedMessage}",
                        Toast.LENGTH_SHORT).show()

                }
        }
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }

}