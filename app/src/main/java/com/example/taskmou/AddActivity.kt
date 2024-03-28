package com.example.taskmou

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.set
import com.example.taskmou.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class AddActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbref: DatabaseReference
    var selectedDateString = "Дата не установлена"
    var id = 1
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val pickDateButton = findViewById<Button>(R.id.btnData)
        val textview = findViewById<TextView>(R.id.textData)

        pickDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val current = "$month.$day.$year"

            val datePickerDialog = DatePickerDialog(this,

                DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    selectedDateString = "$dayOfMonth.${month + 1}.$year"

                    textview.setText(selectedDateString)
                }, year, month, day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }
            val saveText = findViewById<Button>(R.id.btnSave)

            saveText.setOnClickListener {
                val newtask = findViewById<EditText>(R.id.textTask)
                val task = newtask.text.toString()

                if(task.replace(" ", "") !=""){
                    auth = Firebase.auth
                    var user = auth.currentUser
                    val db = FirebaseFirestore.getInstance()
                    if (user != null) {
                        db.collection("users").document(user.uid)
                            .get()
                        val userid = user.uid
                        dbref = FirebaseDatabase.getInstance().getReference("Tasks").child(userid).push()
                        val fulltask = Task(task, selectedDateString)

                        dbref.setValue(fulltask).addOnCompleteListener {
                            if (it.isSuccessful){

                            }
                                Toast.makeText(baseContext, "Запись добавлена!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)

                            }
                        }

                        }else{
                    Toast.makeText(baseContext, "Запись не может быть пустой!", Toast.LENGTH_LONG).show()
                }

            }
        }
    }