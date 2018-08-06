package com.example.ppaud.bhoodie_new2

import android.content.Context
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.recommendationcardview.view.*
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException

class recommendationrecycleradapter(val items: MutableList<Recommendation.mainobject>,val context: Context)
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
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        internal var placenaam: TextView
        internal var addressname: TextView
        internal var photourl: ImageView
        init{
            placenaam = itemView.findViewById<View>(R.id.testtext) as TextView
            addressname=itemView.findViewById<View>(R.id.testsubtext) as TextView
            photourl = itemView.findViewById<View>(R.id.testphoto) as ImageView
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
}