package com.example.taskmou

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.taskmou.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.type.TimeOfDay
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
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->

            if(isConnected){
                val pickDateButton = findViewById<Button>(R.id.btnData)
                val textview = findViewById<TextView>(R.id.textData)
                pickDateButton.setOnClickListener {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)


                    val minute = 0
                    val hour = 0

                    var date = ""

                    val timePickerDialog = TimePickerDialog(this,
                        TimePickerDialog.OnTimeSetListener { view: TimePicker, hour: Int, min: Int ->
                            selectedDateString = "$date.$hour.$min"

                            val dateList = selectedDateString.split(".").toMutableList()
                            for (i in dateList.indices){
                                if (dateList[i].length == 1){
                                    dateList[i] = "0"+dateList[i]
                                }
                            }
                            val dateTime = dateList[0]+"."+dateList[1]+"."+dateList[2] +" "+ dateList[3]+":"+dateList[4]
                            textview.setText(dateTime)
                        }
                        , hour, minute, true)

                    val datePickerDialog = DatePickerDialog(this,

                        DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                            date = "$dayOfMonth.${month + 1}.$year"
                            timePickerDialog.show()

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
            }else{
                ErrDialog().showDialog(this)
            }
        })

        }

    }
