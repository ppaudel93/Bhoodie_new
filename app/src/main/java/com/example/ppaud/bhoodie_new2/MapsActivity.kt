package com.example.ppaud.bhoodie_new2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val sydney=LatLng(-34.0, 151.0)

    private lateinit var b: Bitmap
    fun startRepeatingTask(){
        mStatusChecker.run()
    }
    fun stopRepeatingTask(){
        Handler().removeCallbacks(mStatusChecker)
    }
    val mStatusChecker= object: Runnable {
        override fun run(){
            if (mMap.cameraPosition.zoom>7.0)
            {mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney").snippet("Test Restaurant").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b,100,100,false))))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney)) }
            else
            {mMap.clear()}
            Log.i("asd","asd")
            Handler().postDelayed(this,5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        startRepeatingTask()
    }
}
