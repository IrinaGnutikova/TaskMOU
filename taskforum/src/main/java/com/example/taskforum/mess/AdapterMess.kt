package com.example.taskforum.mess

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.taskforum.ErrDialog
import com.example.taskforum.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AdapterMess(private val messList: ArrayList<Mess>, private val uid: String) :
    RecyclerView.Adapter<AdapterMess.MyViewHolder>() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_mess, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return messList.size
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = messList[position]

        if (uid == currentitem.email.toString()) {
            holder.btnDel1.isVisible = true
        }


        if (!ErrDialog().checkForInternet(holder.cont)) {
            ErrDialog().showDialog(holder.cont)
        } else {

            holder.mess1.text = currentitem.mess
            holder.from.text = currentitem.email

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss")
            val sdf2 = SimpleDateFormat("MM/dd/yyyy")

            val date2List = currentitem.dateAdd!!.split(".").toMutableList()
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
                    if (dateCurr.compareTo(dateSel) == 0) {
                        holder.time.setTextColor(Color.parseColor("#8B9455"))
                        holder.date1.setTextColor(Color.parseColor("#8B9455"))
                    }
                }

                cmp < 0 -> {}
                else -> {}
            }

        }

        holder.btnDel1.setOnClickListener {
            if (!ErrDialog().checkForInternet(holder.cont)) {
                ErrDialog().showDialog(holder.cont)
            } else {
                showAlert(holder.cont,
                    currentitem.mess.toString(),
                    currentitem.dateAdd.toString(),
                    currentitem.email.toString()
                )
            }
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mess1: TextView = itemView.findViewById(R.id.mess)
        val from: TextView = itemView.findViewById(R.id.email)
        val btnDel1: Button = itemView.findViewById(R.id.btnDel)
        val date1: TextView = itemView.findViewById(R.id.dateAdd)
        val cont: Context = itemView.context
        val time: TextView = itemView.findViewById(R.id.timeAdd)

    }

    fun deleteItem(mess: String, date: String, uid: String) {
        val dbref = FirebaseDatabase.getInstance().getReference("Forum")
        val query = dbref.orderByChild("email").equalTo(uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (cutterSnapshot in snapshot.children) {
                    val data = cutterSnapshot.getValue(Mess::class.java)
                    if (data != null && data.dateAdd == date && data.mess == mess) {
                        cutterSnapshot.ref.removeValue()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    private fun showAlert(context: Context, mess: String?, dateAdd: String?, email: String?) {
        AlertDialog.Builder(context)
            .setTitle("Удаление")
            .setMessage(
                "Вы точно хотите удалить данную запись? " +
                        "\n\"$mess\""

            ).setPositiveButton("Да") { _, _ ->
                deleteItem(
                    mess.toString(),
                    dateAdd.toString(),
                    email.toString()
                )
            }.setNegativeButton("Нет") { _, _ ->

            }.show()
    }

}