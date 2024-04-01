package com.example.taskmou.tasks

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmou.ErrDialog
import com.example.taskmou.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

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

        if(!ErrDialog().checkForInternet(holder.cont)){
            ErrDialog().showDialog(holder.cont)
        }else{

        holder.task1.text = currentitem.taskName
        holder.date1.text = currentitem.date

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        if(!currentitem.date.equals("Дата не установлена")){
            val date2List = currentitem.date!!.split(".")
            val sel = date2List[1]+"/"+date2List[0]+"/"+date2List[2]
            val current = "$month/$day/$year"
            val dateCurrent: Date = sdf.parse(current)
            val dateSelect: Date = sdf.parse(sel)

            val cmp = dateCurrent.compareTo(dateSelect)
            when{
                cmp > 0 ->{
                    holder.date1.setTextColor(Color.parseColor("#FFF44336")) //red
                }
                cmp < 0 ->{
                    holder.date1.setTextColor(Color.parseColor("#635D4C"))
                }
                else -> {
                    holder.date1.setTextColor(Color.parseColor("#FFFF9800"))
                }
            }

        }
        }
        holder.btnDel1.setOnClickListener{
            if(!ErrDialog().checkForInternet(holder.cont)){
                ErrDialog().showDialog(holder.cont)
            }else{
            deleteItem(currentitem.taskName.toString())
            deleteItem(currentitem.date.toString())
            }
        }

    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val task1 : TextView = itemView.findViewById(R.id.task)
        val btnDel1 : Button = itemView.findViewById(R.id.btnDel)
        val date1 : TextView = itemView.findViewById(R.id.dateTask)
        val cont : Context = itemView.context


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