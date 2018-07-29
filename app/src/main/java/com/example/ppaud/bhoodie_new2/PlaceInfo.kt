package com.example.ppaud.bhoodie_new2

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_place_info.*
import okhttp3.*
import org.jetbrains.anko.*
import org.w3c.dom.Text
import java.io.IOException

class PlaceInfo : AppCompatActivity() {
    private var defaulturl: String = "https://bhoodie.herokuapp.com"
    val temp: MutableList<String> = ArrayList()
    val temp2: MutableList<Menus> = ArrayList()
    val header: MutableList<String> = ArrayList()
    val tail: MutableList<MutableList<String>> = ArrayList()
    val header2: MutableList<String> = ArrayList()
    val tail2: MutableList<MutableList<Menus>> = ArrayList()
    val photourl: MutableList<String> = ArrayList()
    private val LOCATION_REQUEST_CODE=101
    private var imageurlcount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_info)
        var placeid = intent.getStringExtra("placeid")
        var rating = intent.getStringExtra("rating")
        val isitopen = intent.getBooleanExtra("openornot",false)
        val ratingtext = findViewById<TextView>(R.id.rating)
        ratingtext.text=rating
        getplaceinfo(placeid)
        val Openornot =findViewById<TextView>(R.id.istheplaceopen)
        val openicon = findViewById<ImageView>(R.id.opencloseicon)
        if (isitopen==true){
            openicon.backgroundResource=R.drawable.openicon
            Openornot.text=getText(R.string.open)
        }
        else{
            openicon.backgroundResource=R.drawable.closedicon
            Openornot.text=getText(R.string.closed)
        }
        val previous = findViewById<ImageButton>(R.id.previousimage)
        val next = findViewById<ImageButton>(R.id.nextimage)
        val backbutton = findViewById<Button>(R.id.placeinfoback)
        backbutton.setOnClickListener {
            finish()
        }
        previous.backgroundColor = Color.TRANSPARENT
        next.backgroundColor=Color.TRANSPARENT

        Log.i("placeid","the placeis is $placeid")
        //placeinfoname.setText()
       // val imageview = findViewById<ImageView>(R.id.placeimage)
        //Glide.with(this).load("https://lh3.googleusercontent.com/p/AF1QipOKmMiqTa5IuKLoF7C1aAJfoD5I5bGFxfPyedJj=s1600-w600").into(imageview)
    }

    fun getplaceinfo(placeid: String){
        doAsync {
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
                    val fooditems: MutableList<Menus> = ArrayList()
                    for (item in Mainobject.photos){
                        photourl.add(item)
                    }
                    for(item in Mainobject.opening_hours){
                        temp.add(item)
                    }
                    for(item in Mainobject.menu){
                        fooditems.add(item)
                    }
                    for(item in fooditems){
                        temp2.add(item)
                    }


            uiThread {
                val nameofplace = findViewById<TextView>(R.id.placename)
                val locationofplace = findViewById<TextView>(R.id.placelocation)
                val phoneofplace = findViewById<TextView>(R.id.placephoneno)
                nameofplace.text=Mainobject.name
                locationofplace.text=Mainobject.address
                //if (Mainobject.formatted_phone_number!=null)
                phoneofplace.text=Mainobject.formatted_phone_number
                phoneofplace.isClickable=true
                phoneofplace.setOnClickListener {
                    val permission = ContextCompat.checkSelfPermission(this@PlaceInfo,android.Manifest.permission.CALL_PHONE)
                    if (permission == PackageManager.PERMISSION_GRANTED){
                        makeCall(Mainobject.formatted_phone_number)
                    } else{
                        requestPermission(android.Manifest.permission.CALL_PHONE,LOCATION_REQUEST_CODE)
                    }

                }
                header.add("Opening Hours")
                tail.add(temp)
                header2.add("Menu")
                tail2.add(temp2)
                val imageview = findViewById<ImageView>(R.id.placeimage)

                expandablelist.setAdapter(ExpandableListAdapter(this@PlaceInfo,expandablelist,header,tail))
                menuexpandable.setAdapter(ExpandableListAdapterMenu(this@PlaceInfo,menuexpandable,header2,tail2))
                expandablelist.setOnGroupExpandListener {
                    expandablelist.layoutParams.height=dip(340)
                }
                expandablelist.setOnGroupCollapseListener {
                    expandablelist.layoutParams.height= wrapContent
                }
                menuexpandable.setOnGroupExpandListener {
                    menuexpandable.layoutParams.height= dip(200)
                }
                menuexpandable.setOnGroupCollapseListener {
                    menuexpandable.layoutParams.height= wrapContent
                }

                Glide.with(this@PlaceInfo).load(photourl[imageurlcount]).into(imageview)
                imageview.layoutParams.height= wrapContent
                val nextbutton = findViewById<ImageButton>(R.id.nextimage)
                val previousbutton = findViewById<ImageButton>(R.id.previousimage)
                nextbutton.setOnClickListener {
                    imageurlcount += 1
                    if (imageurlcount==photourl.size) {
                       imageurlcount=0
                    }
                    Glide.with(this@PlaceInfo).load(photourl[imageurlcount]).into(imageview)
                    imageview.layoutParams.height= wrapContent

                }
                previousbutton.setOnClickListener{
                    imageurlcount -= 1
                    if (imageurlcount==-1){
                        imageurlcount=photourl.size-1
                    }
                    Glide.with(this@PlaceInfo).load(photourl[imageurlcount]).into(imageview)
                    imageview.layoutParams.height= wrapContent
                }

                val bikebutton = findViewById<ImageView>(R.id.parkingbikecheck)
                val carbutton = findViewById<ImageView>(R.id.parkingcarcheck)
                val smokingbutton = findViewById<ImageView>(R.id.smokingcheck)
                val vatbutton = findViewById<ImageView>(R.id.vatcheck)
                val scbutton = findViewById<ImageView>(R.id.servicetaxcheck)
                val view = View.inflate(this@PlaceInfo,R.layout.moreplaceinfodialog,null)
                val view2=View.inflate(this@PlaceInfo,R.layout.menuitemadddialog,null)
                val builder = AlertDialog.Builder(this@PlaceInfo)
                val builder2 = AlertDialog.Builder(this@PlaceInfo)
                val addfoodbutton = findViewById<ImageView>(R.id.addfooditem)
                builder.setView(view)
                builder2.setView(view2)
                val dialog: AlertDialog = builder.create()
                val dialog2: AlertDialog = builder2.create()
                bikebutton.isClickable=true
                carbutton.isClickable=true
                smokingbutton.isClickable=true
                vatbutton.isClickable=true
                scbutton.isClickable=true
                addfoodbutton.isClickable=true

                bikebutton.setOnClickListener{
                    dialog.show()
                    dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
                carbutton.setOnClickListener {
                    dialog.show()
                    dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
                smokingbutton.setOnClickListener {
                    dialog.show()
                    dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
                vatbutton.setOnClickListener {
                    dialog.show()
                    dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
                scbutton.setOnClickListener {
                    dialog.show()
                    dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
                addfoodbutton.setOnClickListener {
                    dialog2.show()
                    dialog2.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }

            }
        }
            })

        }

    }

    private fun requestPermission(permissionType: String,requestCode: Int){
        ActivityCompat.requestPermissions(this, arrayOf(permissionType),requestCode)
    }

    class mainobject(val name: String,val address: String,val formatted_phone_number: String,val opening_hours: List<String>,val photos: List<String>,val review: List<Review>,val website: String, val menu: List<Menus>)

    class Menus(val item: String,val price: Int,val votes: Int)

    class Review(val auth1: String)
}
