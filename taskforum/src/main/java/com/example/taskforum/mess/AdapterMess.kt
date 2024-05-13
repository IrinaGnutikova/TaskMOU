package com.example.taskforum.mess

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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

class AdapterMess (private val messList: ArrayList<Mess>, private val uid: String): RecyclerView.Adapter<AdapterMess.MyViewHolder>() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_mess, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return messList.size
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = messList[position]

            if(uid == currentitem.email.toString()){
                holder.btnDel1.isVisible = true
            }


        if(!ErrDialog().checkForInternet(holder.cont)){
            ErrDialog().showDialog(holder.cont)
        }else{

            holder.mess1.text = currentitem.mess
            holder.date1.text = currentitem.dateAdd
            holder.from.text = currentitem.email

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)+1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val sdf = SimpleDateFormat("MM/dd/yyyy")

            val date2List = currentitem.dateAdd!!.split(".")
            val sel = date2List[1]+"/"+date2List[0]+"/"+date2List[2]
            val current = "$month/$day/$year"
            val dateCurrent: Date = sdf.parse(current)
            val dateSelect: Date = sdf.parse(sel)

            val cmp = dateCurrent.compareTo(dateSelect)
            when{
                cmp > 0 ->{}
                cmp < 0 ->{}
                else -> {
                    holder.date1.setTextColor(Color.parseColor("#8B9455"))
                }
            }
        }

        holder.btnDel1.setOnClickListener{
            if(!ErrDialog().checkForInternet(holder.cont)){
                ErrDialog().showDialog(holder.cont)
            }else{
                deleteItem(currentitem.mess.toString(),currentitem.dateAdd.toString(), currentitem.email.toString())
            }
        }

    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val mess1 : TextView = itemView.findViewById(R.id.mess)
        val from : TextView = itemView.findViewById(R.id.email)
        val btnDel1 : Button = itemView.findViewById(R.id.btnDel)
        val date1 : TextView = itemView.findViewById(R.id.dateAdd)
        val cont : Context = itemView.context

    }

    fun deleteItem(mess: String,date: String,uid: String){
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
}