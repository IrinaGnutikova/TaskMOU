package com.example.taskforum

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskforum.mess.AdapterMess
import com.example.taskforum.mess.Mess
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ActivityForum : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbref: DatabaseReference
    private lateinit var messRecyclerView: RecyclerView
    private lateinit var messArrayList: ArrayList<Mess>
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->

            if(isConnected){
                auth = Firebase.auth

                messRecyclerView = findViewById(R.id.task_recyclerView)
                messRecyclerView.layoutManager = LinearLayoutManager(this)
                messRecyclerView.setHasFixedSize(true)
                messArrayList = arrayListOf<Mess>()

                messArrayList.clear()
                getTaskData()
                val messText = findViewById<EditText>(R.id.etMess)
                val btnSave = findViewById<Button>(R.id.btnSave)
                btnSave.setOnClickListener {
                    val task = messText.text.toString()

                    if(task.replace(" ", "") !=""){
                        auth = Firebase.auth
                        var user = auth.currentUser
                        val db = FirebaseFirestore.getInstance()
                        if (user != null) {
                            db.collection("users").document(user.uid).get()

                            val useremail = user.email.toString()
                            val calendar = Calendar.getInstance()
                            val year = calendar.get(Calendar.YEAR)
                            val month = calendar.get(Calendar.MONTH)+1
                            val day = calendar.get(Calendar.DAY_OF_MONTH)


                            val hour = calendar.get(Calendar.HOUR_OF_DAY)
                            val min = calendar.get(Calendar.MINUTE)
                            val current = "$day.$month.$year.$hour.$min"

                            dbref = FirebaseDatabase.getInstance().getReference("Forum").push()
                            val fulltask = Mess(task,useremail ,current )

                            dbref.setValue(fulltask).addOnCompleteListener {
                                if (it.isSuccessful){

                                }
                                Toast.makeText(baseContext, "Запись добавлена!", Toast.LENGTH_SHORT).show()
                                messText.text.clear()
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

    private fun getTaskData() {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.email.toString()
            dbref = FirebaseDatabase.getInstance().getReference("Forum")
            dbref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messArrayList.clear()
                    if (snapshot.exists()) {
                        for (taskSnapshot in snapshot.children) {
                            val task = taskSnapshot.getValue(Mess::class.java)
                            messArrayList.add(task!!)
                        }
                        messArrayList.reverse()
                        messRecyclerView.adapter = AdapterMess(messArrayList, uid)
                    }
                    else{
                        messRecyclerView.adapter = AdapterMess(messArrayList, uid)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(baseContext, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}