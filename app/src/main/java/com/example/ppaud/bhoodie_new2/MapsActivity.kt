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
import android.graphics.drawable.ColorDrawable
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
import android.support.v7.app.AlertDialog
import android.text.Editable
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
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.mapsdialogbox.view.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.io.StringReader
import java.net.URL
import java.util.jar.Manifest
import java.util.regex.Pattern
import kotlin.concurrent.fixedRateTimer

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    fun shape_roundedrect()= GradientDrawable().apply{
        shape= GradientDrawable.RECTANGLE
        setColor(Color.parseColor("#ffffff"))
        cornerRadius=200f
        setStroke(2, Color.parseColor("#d06666"))
    }

    private  var mMap: GoogleMap?=null
    private val sydney=LatLng(-34.0, 151.0)
    private val opera = LatLng(-33.9320447,151.1597271)
    private val LOCATION_REQUEST_CODE=101
    private lateinit var b: Bitmap
    lateinit var locationManager: LocationManager
    private var hasGps=false
    private var hasNetwork=false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private  var userlocation: Location? = null
    private lateinit var userlocationlatLng: LatLng
    private lateinit var mDrawerLayout: DrawerLayout
    private var defaulturl = "https://bhoodie.herokuapp.com"
    private var points: ArrayList<LatLng> = ArrayList(50)
    private var Results: List<Result> = ArrayList(50)

    fun startRepeatingTask(){
        mStatusChecker.run()
    }
    fun stopRepeatingTask(){
        Handler().removeCallbacks(mStatusChecker)
    }

    private fun senduserlocation(userlocation: LatLng){
            val request = Request.Builder().url(defaulturl+"/api/places/lat=${userlocation.latitude}&long=${userlocation.longitude}").build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call?, e: IOException?) {

                }

                override fun onResponse(call: Call?, response: Response?) {
                    val body=response?.body()?.string()
                    val gson = GsonBuilder().create()
                    val Mainclass = gson.fromJson(body,mainclass::class.java)
                    Results = Mainclass.results

                }

            })


//        val client=OkHttpClient()
//        val response=client.newCall(request).execute()

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
    class placeloc(val lat: Double,val lng: Double)

    class mainclass(val results: List<Result>)

    class Result(val id: String,val name: String,val location: placeloc,val open: Boolean,val rating: Float)

    class End_location(val lat: Double,val lng: Double)

    class Start_location(val lat: Double,val lng: Double)

    class Distance(val text: String, val value: Int)

    class Duration(val text: String, val value: Int)

    class Poly(val points: String)

    class Steps(val distance: Distance, val duration: Duration, val end_location: End_location,val start_location: Start_location, val travel_mode: String,val polyline: Poly)

    class Legs(val steps: List<Steps>)

    class Routes(val legs: List<Legs>)

    class directionmain(val routes: List<Routes>)

    private fun getdirections(from: LatLng,to: LatLng){
        mMap!!.clear()
        doAsync {
            val url = getURL(from,to)
            val request = Request.Builder().url(url).build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call?, e: IOException?) {
                    println("Failed to execute request")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    val body=response?.body()?.string()
                    val gson = GsonBuilder().create()
                    val Directionmain=gson.fromJson(body, directionmain::class.java)
                    val temproute=Directionmain.routes
                    val templeg=temproute[0].legs
                    val directionsteps = templeg[0].steps
                    var polypts: List<LatLng> = ArrayList(2000)
                    polypts = directionsteps.flatMap { decodePoly(it.polyline.points) }
                    var options = PolylineOptions()
                    options.color(Color.RED)
                    options.width(10f)
                    options.addAll(polypts)
                    uiThread {
                        val polyline = mMap!!.addPolyline(options)


                    }
                }

            })
        }
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
            if (mMap!!.cameraPosition.zoom>7.0)
            {

                mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney").snippet("Test Restaurant").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.713289,85.312888)).title("Gaia Restaurant & Coffee Shop").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.6796815,85.31903369999999)).title("The Embers Restaurant").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.7158428,85.3095554)).title("Places Restaurant & Bar").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.7143677,85.324899)).title("1905 Restaurant").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.712169,85.3112139)).title("Yak Restaurant").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.7159773,85.3071411)).title("Yangling Restaurant").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.7143912,85.3101976)).title("Third Eye Restaurant").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.688586, 85.315282)).title("Kalinchowk Chinese Kitchen").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.688500, 85.314521)).title("Enjoy Momo & Naan Center").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.687426, 85.314102)).title("Mongolian Restaurant").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.686229, 85.313094)).title("Be There Lounge & Bar").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.691692, 85.316602)).title("KFC Restaurant").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.688281, 85.308598)).title("Shabri The Restaurant").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
//                mMap!!.addMarker(MarkerOptions().position(LatLng(27.685964, 85.306917)).title("Momotarou Restaurant").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
                for (item in Results){
                    mMap!!.addMarker(MarkerOptions().position(LatLng(item.location.lat,item.location.lng)).title(item.name).icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
                    mMap!!.setOnMarkerClickListener {
                        val view = View.inflate(this@MapsActivity,R.layout.mapsdialogbox,null)
                        val builder = AlertDialog.Builder(this@MapsActivity)
                        builder.setView(view)
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        view.placename.text=it.title
                        val destination: LatLng = it.position
                        view.directionbutton.setOnClickListener {
                            getdirections(LatLng(userlocation!!.latitude,userlocation!!.longitude),destination)
                            dialog.dismiss()
                        }
                        return@setOnMarkerClickListener false
                    }
                }
//                mMap!!.addMarker(MarkerOptions().position(opera).title("Opera House"))
//                mMap!!.setOnMarkerClickListener {
//                    val view = View.inflate(this@MapsActivity,R.layout.mapsdialogbox,null)
//                    val builder = AlertDialog.Builder(this@MapsActivity)
//                    builder.setView(view)
//                    val dialog: AlertDialog = builder.create()
//                    dialog.show()
//                    dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                    val destination: LatLng = it.position
//                    view.directionbutton.setOnClickListener {
//                        getdirections(LatLng(userlocation!!.latitude,userlocation!!.longitude),destination)
//                        dialog.dismiss()
//                    }
//                    return@setOnMarkerClickListener false
//                }
            }
            else
            {mMap!!.clear()}
            Log.i("asd","asd")
            Handler().postDelayed(this,5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
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
        mMap!!.clear()
        if (mMap != null){
            val permission=ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            if (permission == PackageManager.PERMISSION_GRANTED){
                mMap?.isMyLocationEnabled=true
                getLocation()
            } else{
                requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,LOCATION_REQUEST_CODE)
            }

        }
        val timer= fixedRateTimer(name="locationsender",initialDelay = 10000,period=10000000){
            senduserlocation(LatLng(userlocation!!.latitude,userlocation!!.longitude))

        }
        startRepeatingTask()
    }
}

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }

