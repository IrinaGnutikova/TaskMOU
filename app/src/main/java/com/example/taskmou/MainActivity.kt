package com.example.taskmou

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskforum.ActivityForum
import com.example.taskmou.tasks.MyAdapter
import com.example.taskmou.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() { // главная страница просмотра задач
    private lateinit var auth: FirebaseAuth
    private lateinit var dbref: DatabaseReference
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskArrayList: ArrayList<Task>
    lateinit var sharedPreferences: SharedPreferences
    val KEY_MODE ="nightMode"
    var light = AppCompatDelegate.MODE_NIGHT_NO
    var night = AppCompatDelegate.MODE_NIGHT_YES
    @SuppressLint("UseSwitchCompatOrMaterialCode", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->

            if(isConnected){
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

                sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                var editor = sharedPreferences.edit()

                val btnMode = findViewById<Button>(R.id.btnMode)
                btnMode.clearAnimation()
                val currMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currMode ==  Configuration.UI_MODE_NIGHT_YES) {
                    animButton(R.drawable.sun)
                } else{
                    animButton(R.drawable.moon)
                }

                btnMode.setOnClickListener {
    val currMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    if (currMode ==  Configuration.UI_MODE_NIGHT_YES) {
        editor.putInt(KEY_MODE, light)
        editor.apply()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    } else{

            editor.putInt(KEY_MODE, night)
            editor.apply()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        }
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

                val btnForum = findViewById<Button>(R.id.btnForum)
                btnForum.setOnClickListener {
                    val intent = Intent(applicationContext, ActivityForum::class.java)
                    startActivity(intent)
                }
            }else{
                ErrDialog().showDialog(this)
            }
        })



    }
 private fun animButton(drawable: Int){
     val btnMode = findViewById<Button>(R.id.btnMode)
     btnMode.clearAnimation()
     btnMode.setBackgroundResource(drawable)
     val scaleAnim = ObjectAnimator.ofFloat(btnMode, "rotation", 0f, 360f)
     scaleAnim.duration = 3000
     scaleAnim.start()
     btnMode.postDelayed({
         btnMode.setBackgroundResource(drawable)
     }, 3001)
 }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() { // запрет на возвращение на прошлую страницу

        Toast.makeText(baseContext, "Чтобы выйти из аккаунта нажмите на кнопку в вехнем углу", Toast.LENGTH_LONG).show()
    }

    private fun getTaskData() {
        val user = auth.currentUser

        if (user != null) {

            val uid = user.uid
            dbref = FirebaseDatabase.getInstance().getReference("Tasks").child(uid)

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