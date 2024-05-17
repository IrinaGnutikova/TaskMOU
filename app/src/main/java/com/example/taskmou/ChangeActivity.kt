package com.example.taskmou

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmou.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ChangeActivity() : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change)
        auth = Firebase.auth
        val user = auth.currentUser

        var date = intent.getStringExtra("date")
        val oldtask = intent.getStringExtra("task")
        val etTask = findViewById<EditText>(R.id.textTask)
        val tvDate = findViewById<TextView>(R.id.textDate)
        etTask.setText(oldtask)
        if (date != "Дата не установлена") {
            val date2List = date!!.split(".").toMutableList()
            for (i in date2List.indices) {
                if (date2List[i].length == 1) {
                    date2List[i] = "0" + date2List[i]
                }
            }

            val newDate =
                date2List[0] + "." + date2List[1] + "." + date2List[2] + " " + date2List[3] + ":" + date2List[4]
            tvDate.setText(newDate)
        } else {
            tvDate.setText(date)
        }

        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            if (user != null) {
                val uid = user.uid
                val dbref = FirebaseDatabase.getInstance().getReference("Tasks").child(uid)
                val query = dbref.orderByChild("taskName").equalTo(oldtask)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val key = snapshot.children.first().key

                            if (etTask.text.toString().trim() !=""){
                                val fulltask = Task(etTask.text.toString(), date)
                                dbref.child(key!!).setValue(fulltask).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            baseContext,
                                            "Запись изменена!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent =
                                            Intent(this@ChangeActivity, MainActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        // Обработка ошибки сохранения данных
                                    }
                                }
                            }
                            else{
                                Toast.makeText(
                                    baseContext,
                                    "Запись не может быть пустой!",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        } else {
                            // Обработка ситуации, когда запись не найдена
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Обработка ошибки запроса
                    }
                })
            }
        }

    }
}