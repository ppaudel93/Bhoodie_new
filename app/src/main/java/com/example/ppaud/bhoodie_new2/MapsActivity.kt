package com.example.ppaud.bhoodie_new2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.graphics.Color
import android.graphics.Color.RED
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import android.location.LocationProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.beust.klaxon.*
import com.example.ppaud.bhoodie_new2.R.id.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import org.jetbrains.anko.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_maps.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.io.StringReader
import java.net.URL
import java.util.jar.Manifest
import java.util.regex.Pattern

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    fun shape_roundedrect()= GradientDrawable().apply{
        shape= GradientDrawable.RECTANGLE
        setColor(Color.parseColor("#ffffff"))
        cornerRadius=200f
        setStroke(2, Color.parseColor("#d06666"))
    }

    private lateinit var mMap: GoogleMap
    private val sydney=LatLng(-34.0, 151.0)
    private val opera = LatLng(-33.9320447,151.1597271)
    private val LOCATION_REQUEST_CODE=101
    private lateinit var b: Bitmap
    lateinit var locationManager: LocationManager
    private var hasGps=false
    private var hasNetwork=false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private lateinit var userlocation: Location
    private lateinit var userlocationlatLng: LatLng
    private lateinit var mDrawerLayout: DrawerLayout
    private var options= PolylineOptions()
    private var points: ArrayList<LatLng> = ArrayList(50)


    //private lateinit var currentlocation: LatLng
    //private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun shape_roundeddialog()= GradientDrawable().apply{
        shape= GradientDrawable.RECTANGLE
        cornerRadius=0f
        setColor(getColor(R.color.colorPrimaryDark))
        //setColor(Color.parseColor("#474747"))
        setStroke(5, Color.parseColor("#d06666"))
    }
    fun shape_roundedrectbut()= GradientDrawable().apply{
        shape= GradientDrawable.RECTANGLE
        cornerRadius=200f
        setColor(getColor(R.color.colorPrimary))
        setStroke(2, Color.parseColor("#d06666"))
    }
    fun shape_rectbut(): GradientDrawable = GradientDrawable().apply{
        shape= GradientDrawable.RECTANGLE
        cornerRadius=0f
        setColor(getColor(R.color.colorPrimary))
        setStroke(2, Color.parseColor("#d06666"))
    }
    fun startRepeatingTask(){
        mStatusChecker.run()
    }
    fun stopRepeatingTask(){
        Handler().removeCallbacks(mStatusChecker)
    }

    private fun getURL(from: LatLng,to: LatLng): String{
        val origin = "origin="+from.latitude+","+from.longitude
        val dest="destination="+to.latitude+","+to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor"
        val output = "json"
        Log.i("urlbro","https://maps.googleapis.com/maps/api/directions/json?$params")
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }
    class End_location(val lat: Double,val lng: Double)

    class Start_location(val lat: Double,val lng: Double)

    class Distance(val text: String, val value: Int)

    class Duration(val text: String, val value: Int)

    class Steps(val distance: Distance, val duration: Duration, val end_location: End_location,val start_location: Start_location, val travel_mode: String)

    class Legs(val steps: List<Steps>)

    class Routes(val legs: List<Legs>)

    class directionmain(val routes: List<Routes>)

    private fun getdirections(){
        val url = getURL(sydney,opera)
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call?, e: IOException?) {
                println("Failed to execute request")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val body=response?.body()?.string()
                //println(body)

                val gson = GsonBuilder().create()
                val Directionmain=gson.fromJson(body, directionmain::class.java)
                val temproute=Directionmain.routes
                val templeg=temproute[0].legs
                val directionsteps = templeg[0].steps
                var templist: ArrayList<Double> =ArrayList(50)
                var startinglatlng: ArrayList<LatLng> = ArrayList(50)
                var endingLatLng: ArrayList<LatLng> = ArrayList(50 )
                //var points: ArrayList<LatLng> = ArrayList(50)
                var loopint: Int=0
                for(item in directionsteps){
                    templist.add(item.start_location.lat)
                    points.add(LatLng(item.start_location.lat,item.start_location.lng))
                    points.add(LatLng(item.end_location.lat,item.end_location.lng))
                    //startinglatlng.add(LatLng(item.start_location.lat,item.start_location.lng))
                    //endingLatLng.add(LatLng(item.end_location.lat,item.end_location.lng))
//                    var polyline: Polyline =mMap.addPolyline(PolylineOptions().clickable(false).color(RED).add(
//                            startinglatlng,endingLatLng
//                  ))

                }
                //val options = PolylineOptions()
                options.color(Color.RED)
                options.width(5f)
                options.clickable(false)
                options.add(sydney).addAll(points).add(opera)
                //for(item in points) options.add(item)
                //mMap!!.addPolyline(options)

//                while(directionsteps?.isNotEmpty()){
//                    startinglatlng= LatLng(directionsteps[loopint].start_location.lat,directionsteps[loopint].start_location.lng)
//                    endingLatLng= LatLng(directionsteps[loopint].end_location.lat,directionsteps[loopint].end_location.lng)
//                    var polyline: Polyline =mMap.addPolyline(PolylineOptions().clickable(false).color(RED).add(
//                            startinglatlng,endingLatLng
//                    ))
//                    directionsteps[loopint]==null
//                    loopint++
//
//                }

//                directionsteps?.forEach {
//                    val asd=directionsteps[loopint].start_location.lat
//                    lats[loopint]=directionsteps[loopint].start_location.lat
//                    longs[loopint]=directionsteps[loopint].start_location.lng
//                    loopint=loopint+1
//
//                }
                //val stepsarray=gson.fromJson(body,Steps::class.java)
            }

        })
//        doAsync {
//            val result = URL(url).readText()
//            uiThread {
//                //Log.i("Request", result)
//                val gson = GsonBuilder().create()
//                val routes = gson.fromJson(result,routes::class.java)
//                println(routes)
//            }
//        }
    }

    private fun requestPermission(permissionType: String,requestCode: Int){
        ActivityCompat.requestPermissions(this, arrayOf(permissionType),requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"unable to show location - permission required",Toast.LENGTH_LONG).show()
                }
                else{
                    val mapFragment=supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }
        }
    }
    val mStatusChecker= object: Runnable {
        override fun run(){
            if (mMap.cameraPosition.zoom>7.0)
            {
                mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney").snippet("Test Restaurant").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
                mMap.addMarker(MarkerOptions().position(opera).title("Opera House"))
                mMap.setOnMarkerClickListener {
                    alert{
                        customView {
                            linearLayout {
                                this.gravity=Gravity.CENTER

                                //orientation=LinearLayout.HORIZONTAL
                                background=shape_roundeddialog()
                                //bottomPadding=dip(20)
                                button {
                                    //text="info"
                                    textSize=20f
                                    gravity=Gravity.CENTER
                                    allCaps=false
                                    background=shape_roundedrectbut()
                                    backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.if_info_372922)
                                    setOnClickListener {

                                    }
                                }.lparams(){
                                    width=dip(40)
                                    height=dip(40)
                                    rightMargin=dip(25)
                                    topMargin=dip(5)
                                    bottomMargin=dip(5)

                                }
                                button {
                                    //text="chat"
                                    textSize=20f
                                    gravity=Gravity.CENTER
                                    allCaps=false
                                    background=shape_roundedrectbut()
                                    backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.if_untitled_2_36_536270)
                                    setOnClickListener {

                                    }
                                }.lparams(){
                                    width=dip(40)
                                    height=dip(40)
                                    leftMargin=dip(25)
                                    rightMargin=dip(25)
                                    topMargin=dip(5)
                                    bottomMargin=dip(5)

                                }
                                button {
                                    //text="direct"
                                    textSize=20f
                                    gravity=Gravity.CENTER
                                    allCaps=false
                                    background=shape_roundedrectbut()
                                    backgroundDrawable=ContextCompat.getDrawable(context,R.drawable.icons8_waypoint_map_50)
                                    setOnClickListener {

                                    }
                                }.lparams(){
                                    width=dip(40)
                                    height=dip(40)
                                    leftMargin=dip(25)
                                    topMargin=dip(5)
                                    bottomMargin=dip(5)

                                }


                            }//.layoutParams= LinearLayout.LayoutParams(wrap_content, wrap_content)
                        }
                        //apply { window.setLayout(wrap_content, wrap_content) }
                    }.show()
                    return@setOnMarkerClickListener false
                }
            }
            else
            {mMap.clear()}
            Log.i("asd","asd")
            Handler().postDelayed(this,5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        //createLocationRequest()
        //fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        mDrawerLayout=findViewById(R.id.drawer_layout)
        val navigationView: NavigationView=findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            it.isChecked = true
            if (it.itemId == recommend_id)
                startActivity<Recommendation>()
            if (it.itemId== show_map)
                mDrawerLayout.closeDrawers()

            mDrawerLayout.closeDrawers()
            true
        }
        mapsearch.background=shape_roundedrect()
        b= Bitmap.createBitmap(BitmapFactory.decodeResource(resources,R.drawable.foodmarker))
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    /*fun createLocationRequest(){
        val locationRequest=LocationRequest().apply {
            interval = 10000
            fastestInterval=5000
            priority=LocationRequest.PRIORITY_HIGH_ACCURACY
            Log.i("asd","REPEATING LOCATION REQUEST")
        }
        val builder=LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient=LocationServices.getSettingsClient(this)
        val task:Task<LocationSettingsResponse> =client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            Log.i("asd","location available")
        }
        task.addOnFailureListener { exception ->
            Log.i("asd","location not available")
        }
    }*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    private fun getLocation(){
        locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork){
            if (hasGps){
                Log.d("UserLocation","hasGPS")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0F,object: LocationListener{
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String?) {
                    }

                    override fun onProviderDisabled(provider: String?) {
                    }

                    override fun onLocationChanged(location: Location?) {
                        if (location!=null){
                            locationGps=location
                            userlocation=location
                        }

                    }
                })
               val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                {locationGps=localGpsLocation
                userlocation=localGpsLocation}
            }
            if (hasNetwork){
                Log.d("UserLocation","hasNetwork")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0F,object: LocationListener{
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String?) {
                    }

                    override fun onProviderDisabled(provider: String?) {
                    }

                    override fun onLocationChanged(location: Location?) {
                        if (location!=null){
                            locationNetwork=location
                            userlocation=location
                        }

                    }
                })
                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                {locationNetwork=localNetworkLocation
                userlocation=localNetworkLocation}
            }
            if (locationGps!=null && locationNetwork!=null){
                if (locationGps!!.accuracy>locationNetwork!!.accuracy){
                    Log.d("UserLocation","Network Latitude : "+ locationNetwork!!.latitude)
                    Log.d("UserLocation","Network Longitude : "+ locationNetwork!!.longitude)
                    userlocation= locationNetwork as Location

                }else{
                    Log.d("UserLocation","GPS Latitude : "+ locationGps!!.latitude)
                    Log.d("UserLocation","GPS Longitude : "+ locationGps!!.longitude)
                    userlocation=locationGps as Location

                }
            }
        }else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (mMap != null){
            val permission=ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            if (permission == PackageManager.PERMISSION_GRANTED){
                mMap?.isMyLocationEnabled=true
                getLocation()
                //userlocationlatLng=LatLng(userlocation.latitude,userlocation.longitude)
               // Log.d("userlocation","Lat:"+userlocation.latitude+"long:"+userlocation.longitude)
                //mMap.addMarker(MarkerOptions().position(userlocationlatLng))
               // mMap.moveCamera(CameraUpdateFactory.newLatLng(userlocationlatLng))
            } else{
                requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,LOCATION_REQUEST_CODE)
            }

        }
        startRepeatingTask()
        getdirections()
        //val option2 = PolylineOptions().add(sydney,LatLng(-34.0001918,150.9985159), LatLng(-33.99960780000001,150.9980761)).add(LatLng(-33.9991642,150.9982047)).color(RED).width(10f)
        val option2 = PolylineOptions().add(sydney)
        for (item in points) option2.add(item)
        points.add(opera)
        //, LatLng(-33.9991642,150.9982047)
        val polyline: Polyline=mMap!!.addPolyline(option2)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
