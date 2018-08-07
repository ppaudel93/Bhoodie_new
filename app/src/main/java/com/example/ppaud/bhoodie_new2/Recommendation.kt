package com.example.ppaud.bhoodie_new2

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.example.ppaud.bhoodie_new2.R.id.recommend_id
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_place_info.view.*
import kotlinx.android.synthetic.main.activity_recommendation.*
import kotlinx.android.synthetic.main.moreplaceinfodialog.view.*
import kotlinx.android.synthetic.main.preferences.view.*
import kotlinx.coroutines.experimental.Deferred
import okhttp3.*
import org.jetbrains.anko.*
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.sdk25.coroutines.onItemSelectedListener
import java.io.IOException
import kotlin.concurrent.fixedRateTimer

class Recommendation : AppCompatActivity() {
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var mAuth = FirebaseAuth.getInstance()
    private val vatboolean: Boolean = false
    private val postpreferencesurl: String = "https://bhoodie.herokuapp.com/api/userprefrence/"
    private val listofmainobject: MutableList<mainobject> = ArrayList()
    private val defaulturl: String = "https://bhoodie.herokuapp.com"
    private var arraystring: MutableList<String> = ArrayList()
    private var body: String? = ""
    private lateinit var dialog2: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)
        dialog2 = indeterminateProgressDialog(message = "Please Wait...",title = "Recommending")
        mDrawerLayout=findViewById(R.id.drawer_layout_recommend)
        val navigationView: NavigationView =findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            it.isChecked = true
            if (it.itemId == R.id.show_map)
                startActivity<MapsActivity>()
            if (it.itemId==recommend_id)
                mDrawerLayout.closeDrawers()
            true
        }
        doAsync {
            val request = Request.Builder()
                    .url(defaulturl+"/api/reccomres/user=${mAuth.currentUser?.email}").build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.i("requeststatus","Request Failed")

                }

                override fun onResponse(call: Call?, response: Response?) {
                    Log.i("requeststatus","Request Success")
                    body = response?.body()?.string()
                    Log.i("requeststatus",body)
                    val gson = GsonBuilder().create()
                    val Listofplaceid = gson.fromJson(body,listofplaceid::class.java)
                    Log.i("requeststatus",Listofplaceid.results[0]+Listofplaceid.results[1])
                    for(item in Listofplaceid.results){
                        arraystring.add(item)
                    }
                    Log.i("requeststatus",arraystring[0]+"  "+arraystring[1])
                    for (item in arraystring){
                        val request2 = Request.Builder().url(defaulturl+"/api/pdetail/id=$item").build()
                        Log.i("requeststatus",defaulturl+"/api/pdetail/id=$item")
                        val client2 = OkHttpClient()
                        client2.newCall(request2).enqueue(object: Callback{
                            override fun onFailure(call: Call?, e: IOException?) {
                                Log.i("requeststatus","Request Failed")

                            }

                            override fun onResponse(call: Call?, response: Response?) {
                                Log.i("requeststatus","Request Success")
                                val body2 = response?.body()?.string()
                                Log.i("requeststatus",body2)

                                val gson = GsonBuilder().create()
                                val Mainobject = gson.fromJson(body2, mainobject::class.java)
                                Log.i("requeststatus",Mainobject.name+Mainobject.address+Mainobject.photos[0])
                                listofmainobject.add(Mainobject)

//                                Handler().postDelayed({
//                                    val rv = findViewById<RecyclerView>(R.id.recommendationrv)
//                                    val llm = LinearLayoutManager(this@Recommendation)
//                                    rv.layoutManager=llm
//                                    Log.i("requeststatus",listofmainobject.size.toString())
//                                    rv.adapter = recommendationrecycleradapter(listofmainobject,this@Recommendation,arraystring)
//                                },5000)

                            }

                        })
                    }


//                    val rv = findViewById<RecyclerView>(R.id.recommendationrv)
//                        val llm = LinearLayoutManager(this@Recommendation)
//                        rv.layoutManager=llm
//                        Log.i("requeststatus",listofmainobject.size.toString())
//                        rv.adapter = recommendationrecycleradapter(listofmainobject,this@Recommendation,arraystring)

//                    Handler().postDelayed({
//                        val rv = findViewById<RecyclerView>(R.id.recommendationrv)
//                        val llm = LinearLayoutManager(this@Recommendation)
//                        rv.layoutManager=llm
//                        Log.i("requeststatus",listofmainobject.size.toString())
//                        rv.adapter = recommendationrecycleradapter(listofmainobject,this@Recommendation,arraystring)
//                    },10000)
//                    Handler().postDelayed({
////                       for(item in Listofplaceid.results){
////                           arraystring.add(item)
////                       }
////                       Log.i("requeststatus",arraystring[0]+"  "+arraystring[1])
//                       val testerboolean = doAsyncResult {
////                           for (item in arraystring){
////                               val request2 = Request.Builder().url(defaulturl+"/api/pdetail/id=$item").build()
////                               Log.i("requeststatus",defaulturl+"/api/pdetail/id=$item")
////                               val client2 = OkHttpClient()
////                               client2.newCall(request2).enqueue(object: Callback{
////                                   override fun onFailure(call: Call?, e: IOException?) {
////                                       Log.i("requeststatus","Request Failed")
////
////                                   }
////
////                                   override fun onResponse(call: Call?, response: Response?) {
////                                       Log.i("requeststatus","Request Success")
////                                       val body = response?.body()?.string()
////                                       Log.i("requeststatus",body)
////
////                                       val gson = GsonBuilder().create()
////                                       val Mainobject = gson.fromJson(body, mainobject::class.java)
////                                       Log.i("requeststatus",Mainobject.name+Mainobject.address+Mainobject.photos[0])
////                                       listofmainobject.add(Mainobject)
////
////                                   }
////
////                               })
////                           }
//                       }
//                   },3000)

//                    Handler().postDelayed({
//                        val rv = findViewById<RecyclerView>(R.id.recommendationrv)
//                        val llm = LinearLayoutManager(this@Recommendation)
//                        rv.layoutManager=llm
//                        Log.i("requeststatus",listofmainobject.size.toString())
//                        rv.adapter = recommendationrecycleradapter(listofmainobject,this@Recommendation,arraystring)
//                    },5000)

                }

            })
        }
        Handler().postDelayed({
            val rv = findViewById<RecyclerView>(R.id.recommendationrv)
            val llm = LinearLayoutManager(this@Recommendation)
            rv.layoutManager=llm
            Log.i("requeststatus",listofmainobject.size.toString())
            rv.adapter = recommendationrecycleradapter(listofmainobject,this@Recommendation,arraystring)
            dialog2.dismiss()
        },10000)



        preferencesbutton.setOnClickListener {
            dialog2 = indeterminateProgressDialog(message = "Please Wait...",title = "Recommending")
            val view = View.inflate(this,R.layout.preferences,null)
            val builder = AlertDialog.Builder(this@Recommendation)
            builder.setView(view)
            val dialog: AlertDialog = builder.create()
            dialog.show()
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            doAsync {
                val request = Request.Builder().url(postpreferencesurl+"user=${mAuth.currentUser?.email.toString()}").build()
                val client = OkHttpClient()
                client.newCall(request).enqueue(object: Callback{
                    override fun onFailure(call: Call?, e: IOException?) {
                        Log.i("requeststatus","Request Failed")

                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        Log.i("requeststatus","Request Success")
                        val body = response?.body()?.string()
                        val gson = GsonBuilder().create()
                        val UserPreferences = gson.fromJson(body,preferences_class::class.java)
                        Log.i("requeststatus",UserPreferences.bike_parking+UserPreferences.car_parking)
                        uiThread {
                            view.parkingbikecheckpreferences.isChecked = UserPreferences.bike_parking=="YES"
                            view.parkingcarcheckpreferences.isChecked = UserPreferences.car_parking=="YES"
                            view.smokingcheckpreferences.isChecked=UserPreferences.smoking=="YES"
                            if (UserPreferences.vat=="ANY")
                                view.vatselect.setSelection(0)
                            else view.vatselect.setSelection(1)
                            when {
                                UserPreferences.prange=="ANY" -> view.priceselect.setSelection(0)
                                UserPreferences.prange=="LOW" -> view.priceselect.setSelection(1)
                                UserPreferences.prange=="MED" -> view.priceselect.setSelection(2)
                                UserPreferences.prange=="HIGH" -> view.priceselect.setSelection(3)
                            }
                            view.deliverycheckpreferences.isChecked=UserPreferences.delivery=="YES"
                            dialog2.dismiss()
                        }

                    }

                })
            }
            view.preferencescancelbutton.setOnClickListener {
                dialog.dismiss()
            }
            val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this,R.array.vatoption, android.R.layout.simple_spinner_dropdown_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            view.vatselect.adapter=adapter

            val adapter2: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this,R.array.priceoption, android.R.layout.simple_spinner_dropdown_item)
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            view.priceselect.adapter=adapter2
            view.priceselect.setSelection(2)
            val temptemp = view.vatselect.selectedItem.toString()
            Log.i("selecteditem:",temptemp)
            view.preferencessubmitbutton.setOnClickListener {
                val vatbool: Boolean; val prangebool: Boolean; val deliverybool: Boolean; val vattemp: String; val prangetemp: String
                val bikepark: String; val carpark: String; val smokecheck: String; val deliverycheck: String
                if (view.parkingbikecheckpreferences.isChecked){
                    bikepark = "yes"
                }else{
                    bikepark = "no"
                }

                if (view.parkingcarcheckpreferences.isChecked){
                    carpark = "yes"
                }else{
                    carpark = "no"
                }

                if (view.smokingcheckpreferences.isChecked){
                    smokecheck = "yes"
                }else{
                    smokecheck = "no"
                }

                if (view.deliverycheckpreferences.isChecked){
                    deliverycheck = "yes"
                }else{
                    deliverycheck = "no"
                }


                vattemp = view.vatselect.selectedItem.toString()
                prangetemp = view.priceselect.selectedItem.toString()
                vatbool = vattemp != "No"
                prangebool = prangetemp != "Any"
                val userpreferences: preferences_class = preferences_class(mAuth.currentUser?.email.toString()
                        ,bikepark
                        ,carpark
                        ,smokecheck
                        ,vattemp
                        ,prangetemp
                        ,deliverycheck)
                Log.i("selecteditem",userpreferences.email
                        +userpreferences.bike_parking+userpreferences.car_parking
                        +userpreferences.vat+userpreferences.prange+userpreferences.delivery)
                doAsync {
                    AndroidNetworking.post(postpreferencesurl).addBodyParameter(userpreferences)
                            .setTag("preferencesetup").setPriority(Priority.MEDIUM)
                            .build()
                }
            }
        }

    }
    class preferences_class(val email: String,val bike_parking: String
                            ,val car_parking: String,val smoking: String
                            ,val vat: String,val prange: String,val delivery: String)
    class mainobject(val name: String, val address: String, val photos: List<String>
                     ,val location: placelocation)
    class placelocation(val lat: Double,val lng: Double)
    class listofplaceid(val results: List<String>)
}
