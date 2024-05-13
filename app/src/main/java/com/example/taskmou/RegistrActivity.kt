package com.example.taskmou

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.taskmou.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegistrActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registr)

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->

            if(isConnected){
                val agree = findViewById<TextView>(R.id.tvAgree)
                agree.setOnClickListener {
                    val intent = Intent(this, AgreeActivity::class.java)
                    startActivity(intent)
                }

                auth = Firebase.auth
                val register = findViewById<Button>(R.id.btnRegister)

                register.setOnClickListener {

                        val email = findViewById<EditText>(R.id.email)
                        val pass = findViewById<EditText>(R.id.password)
                        val name = findViewById<EditText>(R.id.name)
                        val agree = findViewById<CheckBox>(R.id.cbAgree)
                        val pass2 = findViewById<EditText>(R.id.passwordtwo)

                        val email1 = email.text.toString()
                        val password = pass.text.toString()
                        val name1 = name.text.toString()

                        if (email.text.isEmpty() || pass.text.isEmpty() || !agree.isChecked || pass2.text.isEmpty()) {
                            Toast.makeText(
                                baseContext,
                                "Ошибка регистрации! Проверьте поля или примите соглашение",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }else if(pass.text.toString() != pass2.text.toString()){
                            Toast.makeText(
                                baseContext,
                                "Введенные пароли не совпадают!",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                        else {
                            auth.createUserWithEmailAndPassword(email1, password)
                                .addOnCompleteListener(this) { task ->

                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            baseContext, "Аккаунт успешно создан!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        auth = Firebase.auth
                                        val user = auth.currentUser
                                        val db = FirebaseFirestore.getInstance()
                                        if (user != null) {
                                            db.collection("users").document(user.uid)
                                                .get()
                                            val userid = user.uid
                                            dbRef = FirebaseDatabase.getInstance().getReference("Tasks")
                                                .child(userid).child("1")
                                            val hellotext1 = "Это заметки! Здесь ты можешь добавлять"
                                            val fulltask1 = Task(hellotext1, "Дата не установлена")
                                            val hellotext2 = "Или удалять записи, которые тебе не нужны!"
                                            val fulltask2 = Task(hellotext2, "Дата не установлена")
                                            dbRef.setValue(fulltask1).addOnCompleteListener {
                                            }
                                            dbRef = FirebaseDatabase.getInstance().getReference("Tasks")
                                                .child(userid).child("2")
                                            dbRef.setValue(fulltask2).addOnCompleteListener {
                                            }
                                        }
                                        val profileUpdates = UserProfileChangeRequest.Builder()
                                            .setDisplayName(name1)
                                            .build()

                                        user?.updateProfile(profileUpdates)
                                            ?.addOnCompleteListener { task ->
                                            }

                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                            baseContext, "Ошибка!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Ошибка: ${it.localizedMessage}",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                        }

                }
            }else{
                ErrDialog().showDialog(this)
            }
        })

    }
}
