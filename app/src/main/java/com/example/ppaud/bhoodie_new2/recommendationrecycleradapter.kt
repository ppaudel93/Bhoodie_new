package com.example.ppaud.bhoodie_new2


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


class recommendationrecycleradapter(val items: MutableList<Recommendation.mainobject>,val context: Context,val placeids: MutableList<String>)
    : RecyclerView.Adapter<recommendationrecycleradapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.recommendationcardview,parent,false)
        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.placenaam.text=items[position].name
        if(items[position].address.isNullOrBlank())
            holder.addressname.text=""
        else
            holder.addressname.text=items[position].address
        if (items[position].photos.isNotEmpty()){
            Glide.with(context).load(items[position].photos[0]).into(holder.photourl)
        }
        holder.buttons.setOnClickListener {
            val intent = Intent(context,PlaceInfo::class.java)
            intent.putExtra("placeid",placeids[position])
            val sendstring: String = "4"
            intent.putExtra("rating", sendstring)
            val sendopenornot: Boolean = true
            intent.putExtra("openornot",sendopenornot)
            //LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            startActivity(context,intent, Bundle())
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        internal var placenaam: TextView
        internal var addressname: TextView
        internal var photourl: ImageView
        internal var buttons: ImageButton
        init{
            placenaam = itemView.findViewById<View>(R.id.testtext) as TextView
            addressname=itemView.findViewById<View>(R.id.testsubtext) as TextView
            photourl = itemView.findViewById<View>(R.id.testphoto) as ImageView
            buttons = itemView.findViewById<View>(R.id.cardviewbutton) as ImageButton
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
}