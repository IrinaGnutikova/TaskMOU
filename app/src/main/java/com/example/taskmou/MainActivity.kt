package com.example.taskmou

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmou.tasks.MyAdapter
import com.example.taskmou.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbref: DatabaseReference
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskArrayList: ArrayList<Task>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        val logout = findViewById<Button>(R.id.btnExit)

        logout.setOnClickListener {

            auth.signOut()
            Toast.makeText(
                baseContext, "Вы вышли",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, EnterActivity::class.java)
            startActivity(intent)

        }

        val user = auth.currentUser
        val name = user?.displayName
        val userInfoTextView = findViewById<TextView>(R.id.textFIO)
        if(name != null){
            userInfoTextView.text = "Ваши заметки, $name!"
        }

        val addTask = findViewById<Button>(R.id.btnAdd)
        addTask.setOnClickListener{
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

        taskRecyclerView = findViewById(R.id.task_recyclerView)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.setHasFixedSize(true)
        taskArrayList = arrayListOf<Task>()

        taskArrayList.clear()
        getTaskData()


    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

        Toast.makeText(baseContext, "Чтобы выйти из аккаунта нажмите на кнопку в вехнем углу", Toast.LENGTH_LONG).show()
    }

    private fun getTaskData() {
        val user = auth.currentUser

        if (user != null) {

            val uid = user.uid
            dbref = FirebaseDatabase.getInstance().getReference("Tasks").child(uid)
            val db = FirebaseFirestore.getInstance()

            dbref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    taskArrayList.clear()
                    if (snapshot.exists()) {

                        for (taskSnapshot in snapshot.children) {

                            val task = taskSnapshot.getValue(Task::class.java)
                            taskArrayList.add(task!!)

                        }

                        taskRecyclerView.adapter = MyAdapter(taskArrayList, uid)
                    }
                    else{
                        taskRecyclerView.adapter = MyAdapter(taskArrayList, uid)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(baseContext, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }




    }

}