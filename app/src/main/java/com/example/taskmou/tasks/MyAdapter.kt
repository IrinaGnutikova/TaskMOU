package com.example.taskmou.tasks

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmou.ChangeActivity
import com.example.taskmou.ErrDialog
import com.example.taskmou.MainActivity
import com.example.taskmou.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MyAdapter(private val taskList: ArrayList<Task>, private val uid: String) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = taskList[position]

        if (!ErrDialog().checkForInternet(holder.cont)) {
            ErrDialog().showDialog(holder.cont)
        } else {

            holder.task1.text = currentitem.taskName
            holder.time.text = currentitem.date

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss")
            val sdf2 = SimpleDateFormat("MM/dd/yyyy")
            if (!currentitem.date.equals("Дата не установлена")) {
                val date2List = currentitem.date!!.split(".").toMutableList()
                val sel =
                    date2List[1] + "/" + date2List[0] + "/" + date2List[2] + " " + date2List[3] + ":" + date2List[4] + ":00"
                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                val currentMin = calendar.get(Calendar.MINUTE)
                val currentSec = calendar.get(Calendar.SECOND)
                val current = "$month/$day/$year $currentHour:$currentMin:$currentSec"
                val dateCurrent: Date = sdf.parse(current) as Date
                val dateSelect: Date = sdf.parse(sel) as Date

                val date2 = date2List[1] + "/" + date2List[0] + "/" + date2List[2]
                val dateSel: Date = sdf2.parse(date2) as Date
                val datecurr = "$month/$day/$year"
                val dateCurr: Date = sdf2.parse(datecurr) as Date

                for (i in date2List.indices) {
                    if (date2List[i].length == 1) {
                        date2List[i] = "0" + date2List[i]
                    }
                }
                val dateText = date2List[0] + "." + date2List[1] + "." + date2List[2]
                val timeText = date2List[3] + ":" + date2List[4]
                holder.date1.text = dateText.toString()
                holder.time.text = timeText.toString()

                val cmp = dateCurrent.compareTo(dateSelect)
                when {
                    cmp > 0 -> {
                        holder.date1.setTextColor(Color.parseColor("#A03E36")) //red
                        holder.time.setTextColor(Color.parseColor("#A03E36"))
                    }

                    cmp < 0 -> {
                        holder.time.setTextColor(Color.parseColor("#635D4C"))
                        if (dateCurr.compareTo(dateSel) == 0) {
                            holder.date1.setTextColor(Color.parseColor("#AC6701"))
                        }
                    }

                    else -> {
                        holder.date1.setTextColor(Color.parseColor("#AC6701"))
                    }
                }


            }
        }
        holder.btnDel1.setOnClickListener {
            if (!ErrDialog().checkForInternet(holder.cont)) {
                ErrDialog().showDialog(holder.cont)
            } else {
                showAlert(holder.cont, currentitem.taskName.toString(), currentitem.date.toString())
            }
        }
        holder.btnCh1.setOnClickListener {
            val intent = Intent(holder.cont, ChangeActivity::class.java)
            intent.putExtra("task", currentitem.taskName.toString())
            intent.putExtra("date", currentitem.date.toString())
            holder.cont.startActivity(intent)
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val task1: TextView = itemView.findViewById(R.id.task)
        val btnDel1: Button = itemView.findViewById(R.id.btnDel)
        val date1: TextView = itemView.findViewById(R.id.dateTask)
        val cont: Context = itemView.context
        val time: TextView = itemView.findViewById(R.id.timeTask)
        val btnCh1: Button = itemView.findViewById(R.id.btnCh)

    }

    fun deleteItem(item: String) {


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

    private fun showAlert(context: Context, task: String?, date: String?) {
        AlertDialog.Builder(context)
            .setTitle("Удаление")
            .setMessage(
                "Вы точно хотите удалить данную запись? " +
                        "\n $task"

            ).setPositiveButton("Да") { _, _ ->
                deleteItem(task.toString())
                deleteItem(date.toString())
            }.setNegativeButton("Нет") { _, _ ->

            }.show()
    }

}