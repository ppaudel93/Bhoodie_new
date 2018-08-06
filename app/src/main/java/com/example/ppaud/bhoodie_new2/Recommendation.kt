package com.example.ppaud.bhoodie_new2

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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.example.ppaud.bhoodie_new2.R.id.recommend_id
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_place_info.view.*
import kotlinx.android.synthetic.main.activity_recommendation.*
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
    private val preferencesurl: String = "https://bhoodie.herokuapp.com/api/preferences/"
    private val listofmainobject: MutableList<mainobject> = ArrayList()
    private val defaulturl: String = "https://bhoodie.herokuapp.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)
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
        var arraystring: MutableList<String> = ArrayList()
        arraystring.add("ChIJNWXIzkUZ6zkR272DWRQBzOs")
        arraystring.add("ChIJQ28430UZ6zkRiIYNKQyDLdk")
        arraystring.add("ChIJXV1YH1kZ6zkRkKbWi67aVc4")

        val testerboolean = doAsyncResult {
            for (item in arraystring){
                val request = Request.Builder().url(defaulturl+"/api/pdetail/id=$item").build()
                val client = OkHttpClient()
                client.newCall(request).enqueue(object: Callback{
                    override fun onFailure(call: Call?, e: IOException?) {
                        Log.i("requeststatus","Request Failed")

                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        Log.i("requeststatus","Request Success")
                        val body = response?.body()?.string()
                        Log.i("requeststatus",body)

                        val gson = GsonBuilder().create()
                        val Mainobject = gson.fromJson(body, mainobject::class.java)
                        Log.i("requeststatus",Mainobject.name+Mainobject.address+Mainobject.photos[0])
                        listofmainobject.add(Mainobject)

                    }

                })
            }


        }
        Handler().postDelayed({
            if (testerboolean.isDone){
                val rv = findViewById<RecyclerView>(R.id.recommendationrv)
                val llm = LinearLayoutManager(this@Recommendation)
                rv.layoutManager=llm
                Log.i("requeststatus",listofmainobject.size.toString())
                rv.adapter = recommendationrecycleradapter(listofmainobject,this@Recommendation)
            }
        },4000)


        preferencesbutton.setOnClickListener {
            val view = View.inflate(this,R.layout.preferences,null)
            val builder = AlertDialog.Builder(this@Recommendation)
            builder.setView(view)
            val dialog: AlertDialog = builder.create()
            dialog.show()
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
                vattemp = view.vatselect.selectedItem.toString()
                prangetemp = view.priceselect.selectedItem.toString()
                vatbool = vattemp != "No"
                prangebool = prangetemp != "Any"
                val userpreferences: preferences_class = preferences_class(mAuth.currentUser?.email.toString()
                        ,view.parkingbikecheckpreferences.isChecked
                        ,view.parkingcarcheckpreferences.isChecked
                        ,view.smokingcheckpreferences.isChecked,vatbool
                        ,prangebool,view.deliverycheckpreferences.isChecked)
                Log.i("selecteditem",userpreferences.email+userpreferences.bike_park.toString()+userpreferences.car_park.toString()+userpreferences.vat.toString())
                doAsync {
                    AndroidNetworking.post(preferencesurl).addBodyParameter(userpreferences)
                            .setTag("preferencesetup").setPriority(Priority.MEDIUM)
                            .build()
                }
            }
        }

    }
    class preferences_class(val email: String,val bike_park: Boolean,val car_park: Boolean,val smoking: Boolean,val vat: Boolean,val prange: Boolean,val delivery: Boolean)
    class mainobject(val name: String, val address: String, val photos: List<String>)
    class listofplaceid(val placeids: MutableList<String>)
}
