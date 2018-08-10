package com.example.ppaud.bhoodie_new2

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.transition.Slide
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_place_info.*
import kotlinx.android.synthetic.main.menuitemadddialog.view.*
import kotlinx.android.synthetic.main.moreplaceinfodialog.view.*
import kotlinx.android.synthetic.main.preferences.view.*
import kotlinx.android.synthetic.main.reviewdialog.view.*
import okhttp3.*
import org.jetbrains.anko.*
import org.json.JSONArray
import org.w3c.dom.Text
import java.io.IOException

class PlaceInfo : AppCompatActivity() {
    private var defaulturl: String = "https://bhoodie.herokuapp.com"
    private var mAuth = FirebaseAuth.getInstance()
    val temp: MutableList<String> = ArrayList()
    val temp2: MutableList<Menus> = ArrayList()
    val temp3: MutableList<Review> = ArrayList()
    val header: MutableList<String> = ArrayList()
    val tail: MutableList<MutableList<String>> = ArrayList()
    val header2: MutableList<String> = ArrayList()
    val tail2: MutableList<MutableList<Menus>> = ArrayList()
    val header3: MutableList<String> = ArrayList()
    val tail3: MutableList<MutableList<Review>> = ArrayList()
    val photourl: MutableList<String> = ArrayList()
    private val LOCATION_REQUEST_CODE=101
    private var imageurlcount: Int = 0
    private lateinit var dialog2: ProgressDialog
    var Getresinfo: ResInfoGet = ResInfoGet("","","","","","")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window){
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition=Slide()
            exitTransition= Slide()

        }
        setContentView(R.layout.activity_place_info)
        dialog2 = indeterminateProgressDialog(message = "Please Wait...",title="Fetching Data")
        Log.i("useremailaddress",mAuth.currentUser?.email)
        var placeid = intent.getStringExtra("placeid")
        Log.i("requeststatus",placeid)
        var rating = intent.getStringExtra("rating")
        Log.i("requeststatus",rating)
        if (rating.isNullOrBlank())
            rating="4"
        val isitopen = intent.getBooleanExtra("openornot",false)
        val ratingtext = findViewById<TextView>(R.id.rating)
        ratingtext.text=rating
        getplaceinfo(placeid)
        val Openornot =findViewById<TextView>(R.id.istheplaceopen)
        val openicon = findViewById<ImageView>(R.id.opencloseicon)
        if (isitopen!=null){
        if (isitopen==true){
            openicon.backgroundResource=R.drawable.openicon
            Openornot.text=getText(R.string.open)
        }
        else{
            openicon.backgroundResource=R.drawable.closedicon
            Openornot.text=getText(R.string.closed)
        }}else{
            openicon.backgroundResource=R.drawable.closedicon
            Openornot.text="Missing Data"
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

                    Log.i("requeststatus","Request Success 123123123")
                    val body = response?.body()?.string()
                    val gson = GsonBuilder().create()
                    Log.i("requeststatus",body)
                    val Mainobject = gson.fromJson(body,mainobject::class.java)
                    Log.i("requeststatus",Mainobject.name)

                    val fooditems: MutableList<Menus> = ArrayList()
                    val reviewitems: MutableList<Review> = ArrayList()
                    for (item in Mainobject.photos){
                        photourl.add(item)
                    }
                    for(item in Mainobject.opening_hours){
                        temp.add(item)
                    }
                    for(item in Mainobject.menu){
                        fooditems.add(item)
                    }
                    for(item in Mainobject.review){
                        reviewitems.add(item)
                    }
                    for(item in fooditems){
                        temp2.add(item)
                    }
                    for(item in reviewitems){
                        temp3.add(item)
                    }
                    val request3 = Request.Builder().url(defaulturl+"/api/resinfo/id=$placeid").build()
                    val client3 = OkHttpClient()
                    Log.i("requeststatus",defaulturl+"/api/resinfo/id=$placeid")
                    client3.newCall(request3).enqueue(object: Callback{
                        override fun onFailure(call: Call?, e: IOException?) {
                            Log.i("requeststatus","Request Failed for restaurant info")

                        }

                        override fun onResponse(call: Call?, response: Response?) {
                            Log.i("requeststatus","Request Success for restaurant info")
                            val body3 = response?.body()?.string()
                            val gson3 = GsonBuilder().create()
                            Getresinfo = gson3.fromJson(body3,ResInfoGet::class.java)

                        }

                    })

//                    val request2 = Request.Builder().url(defaulturl+"/api/review/id=$placeid").build()
//                    val client2 = OkHttpClient()
//                    client2.newCall(request2).enqueue(object: Callback{
//                        override fun onFailure(call: Call?, e: IOException?) {
//                            Log.i("requeststatus","Request Failed")
//
//                        }
//
//                        override fun onResponse(call: Call?, response: Response?) {
//                            Log.i("requeststatus","Request Success")
//                            val body = response?.body()?.string()
//                            val gson = GsonBuilder().create()
//                            val receiveReview = gson.fromJson(body,ReceiveReview::class.java)
//                            for (item in receiveReview.reviews){
//                                val review = Review(item.email,item.comment)
//                                temp3.add(review)
//                            }
//                        }
//
//                    })


            uiThread {
                placeinfoname.text=Mainobject.name
                val nameofplace = findViewById<TextView>(R.id.placename)
                val locationofplace = findViewById<TextView>(R.id.placelocation)
                val phoneofplace = findViewById<TextView>(R.id.placephoneno)
                val website = findViewById<TextView>(R.id.placewebsite)
                if (Mainobject.name.isNullOrBlank()){
                    nameofplace.text=""
                }else
                    nameofplace.text=Mainobject.name
                if(Mainobject.address.isNullOrBlank())
                    locationofplace.text=""
                else
                    locationofplace.text=Mainobject.address
                //if (Mainobject.formatted_phone_number!=null)
                var content = SpannableString("")
                content.setSpan(UnderlineSpan(),0,content.length,0)
                if (Mainobject.website.isNullOrBlank())
                    website.text="Unlisted"
                else
                {
                    content = SpannableString(Mainobject.website)
                    website.text=content
                    website.setOnClickListener {
                        browse(Mainobject.website)
                    }
                }
                if (Mainobject.formatted_phone_number.isNullOrBlank())
                    phoneofplace.text=""
                else {
                    phoneofplace.text=Mainobject.formatted_phone_number
                    phoneofplace.isClickable=true
                    phoneofplace.setOnClickListener {
                        val permission = ContextCompat.checkSelfPermission(this@PlaceInfo,android.Manifest.permission.CALL_PHONE)
                        if (permission == PackageManager.PERMISSION_GRANTED){
                            makeCall(Mainobject.formatted_phone_number)
                        } else{
                            requestPermission(android.Manifest.permission.CALL_PHONE,LOCATION_REQUEST_CODE)
                            if (permission == PackageManager.PERMISSION_GRANTED)
                                makeCall(Mainobject.formatted_phone_number)
                        }

                    }
                }
                header.add("Opening Hours")
                tail.add(temp)
                header2.add("Menu")
                tail2.add(temp2)
                header3.add("Reviews")
                tail3.add(temp3)
                val imageview = findViewById<ImageView>(R.id.placeimage)
                expandablelist.setAdapter(ExpandableListAdapter(this@PlaceInfo,expandablelist,header,tail))
                menuexpandable.setAdapter(ExpandableListAdapterMenu(this@PlaceInfo,menuexpandable,header2,tail2,placeid))
                expandablereview.setAdapter(ExpandableListAdapterReview(this@PlaceInfo,expandablereview,header3,tail3))
                expandablereview.isNestedScrollingEnabled=true
                menuexpandable.isNestedScrollingEnabled=true
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
                expandablereview.setOnGroupExpandListener {
                   expandablereview.layoutParams.height= dip(300)
                }
                expandablereview.setOnGroupCollapseListener {
                    expandablereview.layoutParams.height= wrapContent
                }

                dialog2.dismiss()
                if (photourl.isNotEmpty()){
                    //imageview.layoutParams.height= wrapContent
                    Glide.with(this@PlaceInfo).load(photourl[imageurlcount]).into(imageview)
                    //Glide.with(this@PlaceInfo).load(photourl[imageurlcount]).into(imageview)
                    //imageview.layoutParams.height= wrapContent
                    val nextbutton = findViewById<ImageButton>(R.id.nextimage)
                    val previousbutton = findViewById<ImageButton>(R.id.previousimage)
                    nextbutton.setOnClickListener {
                        imageurlcount += 1
                        if (imageurlcount==photourl.size) {
                            imageurlcount=0
                        }
                        Glide.with(this@PlaceInfo).load(photourl[imageurlcount]).into(imageview)
                        //imageview.layoutParams.height= wrapContent

                    }
                    previousbutton.setOnClickListener{
                        imageurlcount -= 1
                        if (imageurlcount==-1){
                            imageurlcount=photourl.size-1
                        }
                        Glide.with(this@PlaceInfo).load(photourl[imageurlcount]).into(imageview)
                        //imageview.layoutParams.height= wrapContent
                    }
                }
                val bikebutton = findViewById<ImageView>(R.id.parkingbikecheck)
                val carbutton = findViewById<ImageView>(R.id.parkingcarcheck)
                val smokingbutton = findViewById<ImageView>(R.id.smokingcheck)
                val vatbutton = findViewById<ImageView>(R.id.vatcheck)
                val deliverybutton = findViewById<ImageView>(R.id.deliverycheck)
                val pricelow = findViewById<ImageView>(R.id.checklow)
                val pricemid = findViewById<ImageView>(R.id.checkmid)
                val pricehigh = findViewById<ImageView>(R.id.checkhigh)
                val view = View.inflate(this@PlaceInfo,R.layout.moreplaceinfodialog,null)
                val view2=View.inflate(this@PlaceInfo,R.layout.menuitemadddialog,null)
                val view3 = View.inflate(this@PlaceInfo,R.layout.reviewdialog,null)
                val builder = AlertDialog.Builder(this@PlaceInfo)
                val builder2 = AlertDialog.Builder(this@PlaceInfo)
                val builder3 = AlertDialog.Builder(this@PlaceInfo)
                val addfoodbutton = findViewById<ImageView>(R.id.addfooditem)
                builder.setView(view)
                builder2.setView(view2)
                builder3.setView(view3)
                val dialog: AlertDialog = builder.create()
                val dialog2: AlertDialog = builder2.create()
                val dialog3: AlertDialog = builder3.create()
                bikebutton.isClickable=true
                carbutton.isClickable=true
                smokingbutton.isClickable=true
                vatbutton.isClickable=true
                deliverybutton.isClickable=true
                pricelow.isClickable = true
                pricemid.isClickable = true
                pricehigh.isClickable = true
                addfoodbutton.isClickable=true

                placeratingbar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    dialog3.show()
                    dialog3.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    view3.revieweditdialog
                    view3.sendreview.setOnClickListener {
                        doAsync {
                            val sendReview = SendReview(mAuth.currentUser?.email.toString(),placeid,rating.toInt()
                            ,view3.revieweditdialog.text.toString())
                            AndroidNetworking.post(defaulturl+"/api/review/").addBodyParameter(sendReview)
                                    .setPriority(Priority.MEDIUM).setTag("placereview").build()
                                    .getAsJSONArray(object: JSONArrayRequestListener{
                                        override fun onResponse(response: JSONArray?) {

                                        }

                                        override fun onError(anError: ANError?) {

                                        }

                                    })
                            uiThread {
                                dialog3.dismiss()
                                finish()
                                startActivity(intent)
                            }
                        }




                    }
                }

                val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this@PlaceInfo,R.array.vatoption, android.R.layout.simple_spinner_dropdown_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.vatselectplaceinfo.adapter=adapter

                val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this@PlaceInfo,R.array.priceoption, android.R.layout.simple_spinner_dropdown_item)
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.priceselectplaceinfo.adapter=adapter2
                Handler().postDelayed({
                    Log.i("getrescheck","Checking Checking"+Getresinfo.bike_parking+Getresinfo.car_parking+Getresinfo.delivery+Getresinfo.prange)
                    if (Getresinfo.bike_parking=="") {
                        bikebutton.setOnClickListener {
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
                        deliverybutton.setOnClickListener {
                            dialog.show()
                            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        }
                        pricelow.setOnClickListener {
                            dialog.show()
                            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        }
                        pricemid.setOnClickListener {
                            dialog.show()
                            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        }
                        pricehigh.setOnClickListener {
                            dialog.show()
                            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        }
                    } else{
                        bikebutton.isClickable = false
                        carbutton.isClickable = false
                        smokingbutton.isClickable = false
                        vatbutton.isClickable = false
                        pricelow.isClickable = false
                        pricemid.isClickable = false
                        pricehigh.isClickable = false

                        if (Getresinfo.bike_parking=="YES")
                            bikebutton.background = getDrawable(R.drawable.tickmarkgreen)
                        if (Getresinfo.bike_parking=="NO")
                            bikebutton.background = getDrawable(R.drawable.crossmarkred)
                        if (Getresinfo.car_parking=="YES")
                            carbutton.background = getDrawable(R.drawable.tickmarkgreen)
                        if (Getresinfo.car_parking=="NO")
                            carbutton.background = getDrawable(R.drawable.crossmarkred)
                        if (Getresinfo.smoking=="YES")
                            smokingbutton.background = getDrawable(R.drawable.tickmarkgreen)
                        if (Getresinfo.smoking=="NO")
                            smokingbutton.background = getDrawable(R.drawable.crossmarkred)
                        if (Getresinfo.vat=="ANY")
                            vatbutton.background = getDrawable(R.drawable.tickmarkgreen)
                        if (Getresinfo.vat=="NO")
                            vatbutton.background = getDrawable(R.drawable.crossmarkred)
                        if (Getresinfo.prange=="LOW") {
                            pricelow.background = getDrawable(R.drawable.greenmoney)
                            pricemid.visibility = View.INVISIBLE
                            pricehigh.visibility = View.INVISIBLE
                        }
                        if (Getresinfo.prange=="MED") {
                            pricelow.background = getDrawable(R.drawable.bluemoney)
                            pricemid.background = getDrawable(R.drawable.bluemoney)
                            pricehigh.visibility = View.INVISIBLE
                        }
                        if (Getresinfo.prange=="HIGH"){
                            pricelow.background = getDrawable(R.drawable.redmoney)
                            pricemid.background = getDrawable(R.drawable.redmoney)
                            pricehigh.background = getDrawable(R.drawable.redmoney)
                        }
                        if (Getresinfo.delivery=="YES")
                            deliverybutton.background = getDrawable(R.drawable.tickmarkgreen)
                        if (Getresinfo.delivery=="NO")
                            deliverybutton.background = getDrawable(R.drawable.crossmarkred)
                    }
                },2000)


                view.submitbuttondialog.setOnClickListener {
                    doAsync {
                        val bikeparkinfo: String;val carparkinfo: String;val smokinginfo: String
                        val vatinfo: String = view.vatselectplaceinfo.selectedItem.toString().toUpperCase()
                        val pricerange: String = view.priceselectplaceinfo.selectedItem.toString().toUpperCase()
                        val deliveryinfo: String
                        if (view.parkingbikecheckdialog.isChecked)
                            bikeparkinfo="YES"
                        else bikeparkinfo="NO"
                        if (view.parkingcarcheckdialog.isChecked)
                            carparkinfo="YES"
                        else carparkinfo="NO"
                        if (view.smokingcheckdialog.isChecked)
                            smokinginfo="YES"
                        else smokinginfo="NO"
                        if (view.deliverycheckdialog.isChecked)
                            deliveryinfo="YES"
                        else deliveryinfo="NO"
                        val restinfo = RestaurantInfos(placeid,bikeparkinfo
                                ,carparkinfo,smokinginfo,vatinfo,pricerange,deliveryinfo)
                        Log.i("selecteditem","posting posting posting "+ restinfo.placeid)
                        AndroidNetworking.post(defaulturl+"/api/editres/")
                                .addBodyParameter(restinfo).setPriority(Priority.MEDIUM)
                                .setTag("restaurantinfo").build()
                                .getAsJSONArray(object : JSONArrayRequestListener{
                                    override fun onResponse(response: JSONArray?) {

                                    }

                                    override fun onError(anError: ANError?) {

                                    }

                                })
                        dialog.dismiss()
                        finish()
                        startActivity(intent)
                    }
                }
                addfoodbutton.setOnClickListener {
                    dialog2.show()
                    dialog2.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    view2.sendfooditem.setOnClickListener {
                        when {
                            view2.foodname.text.toString().isNullOrEmpty() -> Toast.makeText(this@PlaceInfo,"Food Name is Empty.",Toast.LENGTH_LONG).show()
                            view2.foodprice.text.toString().isNullOrEmpty() -> Toast.makeText(this@PlaceInfo,"Food Price is Empty.",Toast.LENGTH_LONG).show()
                            else -> {
                                var placeid = intent.getStringExtra("placeid")
                                val newfood: Menus = Menus(view2.foodname.text.toString(),view2.foodprice.text.toString().toInt(),0,placeid)
                                doAsync {
                                    AndroidNetworking.post(defaulturl+"/api/newfood/")
                                            .addBodyParameter(newfood).setPriority(Priority.MEDIUM)
                                            .setTag("newfood").build()
                                            .getAsJSONArray(object: JSONArrayRequestListener{
                                                override fun onResponse(response: JSONArray?) {


                                                }

                                                override fun onError(anError: ANError?) {


                                                }

                                            })

                                    uiThread {
                                        view2.foodname.setText("")
                                        view2.foodprice.setText("")
                                        dialog2.dismiss()
                                        finish()
                                        startActivity(intent)
                                    }


                                }

                            }
                        }
                    }
                    //dialog2.window.setLayout(700, wrapContent)
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

    class Menus(val name: String,val price: Int,val votes: Int,val placeid: String)

    class Review(val name: String,val text: String)

    class RestaurantInfos(val placeid: String,val bike_parking: String,val car_parking: String,val smoking: String,val vat: String,val prange: String,val delivery: String)

    class ResInfoGet(val bike_parking: String,val car_parking: String,val smoking: String,val vat: String,val prange: String,val delivery: String)

    class SendReview(val email: String,val placeid: String,val rating: Int,val comment: String)

    class ReceiveReview(val reviews: List<SingleReview>)

    class SingleReview(val email: String,val rating: Int,val comment: String)
}
