package com.example.ppaud.bhoodie_new2

import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_place_info.*
import okhttp3.*
import java.io.IOException

class PlaceInfo : AppCompatActivity() {
    private var defaulturl: String = "https://bhoodie.herokuapp.com"
    val temp: MutableList<String> = ArrayList()
    val header: MutableList<String> = ArrayList()
    val tail: MutableList<MutableList<String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_info)
        var placeid = intent.getStringExtra("placeid")
        Handler().postDelayed({
            header.add("Opening Hours")
            tail.add(temp)
            val title: String = "Opening Hours"
            expandablelist.setAdapter(ExpandableListAdapter(this@PlaceInfo,expandablelist,header,tail))
            Log.i("listcreated","LIST HAS BEEN CREATED")
        },5000)

        Log.i("placeid","the placeis is $placeid")
        //placeinfoname.setText()
       // val imageview = findViewById<ImageView>(R.id.placeimage)
        //Glide.with(this).load("https://lh3.googleusercontent.com/p/AF1QipOKmMiqTa5IuKLoF7C1aAJfoD5I5bGFxfPyedJj=s1600-w600").into(imageview)
        getplaceinfo(placeid)
    }

    fun getplaceinfo(placeid: String){
        val request = Request.Builder().url(defaulturl+"/api/pdetail/id=$placeid").build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                Log.i("requeststatus","Request Failed")

            }

            override fun onResponse(call: Call?, response: Response?) {

                Log.i("requeststatus","Request Success")
                val body = response?.body()?.string()
                val gson = GsonBuilder().create()
                val Mainobject = gson.fromJson(body,mainobject::class.java)
                placeinfoname.text=Mainobject.name
                for(item in Mainobject.opening_hours){
                    temp.add(item)
                }
                placename.text=Mainobject.name
                placelocation.text=Mainobject.address
                placephoneno.text=Mainobject.formatted_phone_number

                val imageview = findViewById<ImageView>(R.id.placeimage)
                //Glide.with(this@PlaceInfo).load(Mainobject.photos[0]).into(imageview)
            }

        })

    }

    class mainobject(val name: String,val address: String,val formatted_phone_number: String,val opening_hours: List<String>,val photos: List<String>,val review: List<Review>)

    class Review(val auth1: String)
}
