package com.example.taskmou.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmou.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyAdapter (private val taskList: ArrayList<Task>, private val uid: String): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = taskList[position]


        holder.task1.text = currentitem.taskName
        holder.date1.text = currentitem.date

        holder.btnDel1.setOnClickListener{
            deleteItem(currentitem.taskName.toString())
            deleteItem(currentitem.date.toString())
        }
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val task1 : TextView = itemView.findViewById(R.id.task)
        val btnDel1 : Button = itemView.findViewById(R.id.btnDel)
        val date1 : TextView = itemView.findViewById(R.id.dateTask)


    }

    fun deleteItem(item: String){


        val dbref = FirebaseDatabase.getInstance().getReference("Tasks").child(uid)
        val query = dbref.orderByChild("taskName").equalTo(item)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (cuttersSnapshot in snapshot.children) {
                    cuttersSnapshot.ref.removeValue()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}