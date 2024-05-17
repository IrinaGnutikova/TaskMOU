package com.example.taskmou

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.taskmou.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AddActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbref: DatabaseReference
    var selectedDateString = "Дата не установлена"
    var id = 1
    val calendar = Calendar.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->

            if (isConnected) {
                val pickDateButton = findViewById<Button>(R.id.btnData)
                val textview = findViewById<TextView>(R.id.textData)
                pickDateButton.setOnClickListener {

                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                    val currentMin = calendar.get(Calendar.MINUTE)

                    val minute = 0
                    val hour = 0

                    var date = ""

                    val timePickerDialog = TimePickerDialog(this,
                        TimePickerDialog.OnTimeSetListener { view: TimePicker, hour: Int, min: Int ->
                            val sdf = SimpleDateFormat("dd.MM.yyyy")
                            val sdfTime = SimpleDateFormat("hh.mm")
                            val current = "$day.${month + 1}.$year"
                            val currentTime = "$currentHour.$currentMin"
                            val dateCurrent: Date = sdf.parse(current) as Date
                            val dateSelect: Date = sdf.parse(date) as Date
                            val timeCurrent: Date = sdfTime.parse(currentTime) as Date
                            val timeSelect: Date = sdfTime.parse("$hour.$min") as Date
                            val cmpTime = timeCurrent.compareTo(timeSelect)
                            val cmp = dateCurrent.compareTo(dateSelect)

                            if (cmp == 0) {
                                if (cmpTime > 0) {
                                    selectedDateString = "Дата не установлена"
                                    textview.setText(selectedDateString)
                                    showAlert()
                                    return@OnTimeSetListener
                                }
                            }


                            selectedDateString = "$date.$hour.$min"

                            val dateList = selectedDateString.split(".").toMutableList()
                            for (i in dateList.indices) {
                                if (dateList[i].length == 1) {
                                    dateList[i] = "0" + dateList[i]
                                }
                            }
                            val dateTime =
                                dateList[0] + "." + dateList[1] + "." + dateList[2] + " " + dateList[3] + ":" + dateList[4]
                            calendar.set(
                                dateList[2].toInt(),
                                dateList[1].toInt() - 1,
                                dateList[0].toInt(),
                                dateList[3].toInt(),
                                dateList[4].toInt()
                            )
                            textview.setText(dateTime)
                        }, hour, minute, true
                    )

                    val datePickerDialog = DatePickerDialog(
                        this,

                        DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                            date = "$dayOfMonth.${month + 1}.$year"
                            timePickerDialog.updateTime(currentHour, currentMin)
                            timePickerDialog.show()

                        }, year, month, day
                    )

                    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                    datePickerDialog.show()

                }
                createNotificationChanell()
                val saveText = findViewById<Button>(R.id.btnSave)

                saveText.setOnClickListener {

                    val newtask = findViewById<EditText>(R.id.textTask)
                    val task = newtask.text.toString()

                    if (selectedDateString != "Дата не установлена") {
                        scheduleNotification()
                    }


                    if (task.replace(" ", "") != "") {
                        auth = Firebase.auth
                        var user = auth.currentUser
                        val db = FirebaseFirestore.getInstance()
                        if (user != null) {
                            db.collection("users").document(user.uid)
                                .get()
                            val userid = user.uid
                            dbref =
                                FirebaseDatabase.getInstance().getReference("Tasks").child(userid)
                                    .push()
                            val fulltask = Task(task, selectedDateString)

                            dbref.setValue(fulltask).addOnCompleteListener {
                                if (it.isSuccessful) {

                                }
                                Toast.makeText(baseContext, "Запись добавлена!", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)

                            }
                        }

                    } else {
                        Toast.makeText(
                            baseContext,
                            "Запись не может быть пустой!",
                            Toast.LENGTH_LONG
                        ).show()
                    }


                }
            } else {
                ErrDialog().showDialog(this)
            }
        })

    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, Notification::class.java)
        val newtask = findViewById<EditText>(R.id.textTask)
        val title = "Закончился срок выполнения задачи!"
        val message = "У задачи \"${newtask.text.toString()}\" вышел срок ее выполнения!"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            hashCode(), // это должно быть разным
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = calendar.timeInMillis
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun showAlert() {
        AlertDialog.Builder(this)
            .setTitle("Неверное время!")
            .setMessage(
                "Выбранное время не может быть меньше текущего! "

            ).setPositiveButton("Okay") { _, _ ->

            }.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanell() {
        val name = "Уведомления"
        val desc = "Данные уведомления сообщают об окончании срока выполнения установленных задач"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}
