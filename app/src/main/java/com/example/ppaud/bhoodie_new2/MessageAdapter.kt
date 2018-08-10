package com.example.ppaud.bhoodie_new2

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.sentmessagelayout.view.*
import kotlinx.android.synthetic.main.receivemessagelayout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val VIEW_TYPE_MY_MESSAGE:Int = 1
private const val VIEW_TYPE_OTHER_MESSAGE:Int = 2

class MessageAdapter (val context: Context): RecyclerView.Adapter<MessageViewHolder>(){
    private val messages: ArrayList<chatactivity.Message> = ArrayList()
    private var mAuth = FirebaseAuth.getInstance()

    fun addMessage(message: chatactivity.Message){
        messages.add(message)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages.get(position)

        return if(mAuth.currentUser?.email.toString()==message.sender) {
            VIEW_TYPE_MY_MESSAGE
        }
        else{
            VIEW_TYPE_OTHER_MESSAGE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if(viewType == VIEW_TYPE_MY_MESSAGE){
            MyMessageViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.sentmessagelayout,parent,false)
            )
        }else{
            OtherMessageViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.receivemessagelayout,parent,false)
            )
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages.get(position)
        holder?.bind(message)
    }
    inner class MyMessageViewHolder(view: View): MessageViewHolder(view){
        private var messageText: TextView = view.text_message_body_sent
        private var timeText: TextView = view.text_message_time_sent

        override fun bind(message: chatactivity.Message) {
            messageText.text = message.message
            timeText.text = message.time
        }
    }
    inner class OtherMessageViewHolder (view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.text_message_body_received
        private var userText: TextView = view.text_message_name
        private var timeText: TextView = view.text_message_time

        override fun bind(message: chatactivity.Message) {
            messageText.text = message.message
            userText.text = message.sender
            timeText.text = message.time
        }
    }

    object DateUtils{
        fun fromMillisToTimeString(millis: Long): String{
            val format = SimpleDateFormat("hh:mm a",Locale.getDefault())
            return format.format(millis)
        }
    }
}

open class MessageViewHolder (view: View): RecyclerView.ViewHolder(view){
    open fun bind(message: chatactivity.Message){}
}
