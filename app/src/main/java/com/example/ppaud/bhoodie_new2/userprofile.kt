package com.example.ppaud.bhoodie_new2

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_userprofile.*
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.io.IOException

class userprofile : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    private lateinit var dialog2: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userprofile)
        dialog2 = indeterminateProgressDialog(message = "Please Wait...",title="Fetching Data")
        doAsync {
            val request = Request.Builder()
                    .url("https://bhoodie.herokuapp.com/api/getuser/email=${mAuth.currentUser?.email.toString()}")
                    .build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object: Callback{
                override fun onFailure(call: Call?, e: IOException?) {
                    Log.i("requeststatus","Request Failed")
                }

                override fun onResponse(call: Call?, response: Response?) {
                    Log.i("requeststatus","Request Success 123123123")
                    val body = response?.body()?.string()
                    val gson = GsonBuilder().create()
                    val userinfo=gson.fromJson(body,Userinfo::class.java)
                    uiThread {
                        Handler().postDelayed({
                            profilefullname.text = userinfo.name
                            dialog2.dismiss()
                        },1000)
                    }
                }

            })
        }

        profileback.setOnClickListener {
            startActivity<MapsActivity>()
            finish()
        }
        userlogout.setOnClickListener{
            val intent = Intent(applicationContext,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            mAuth.signOut()
            startActivity(intent)
            finish()
        }
        profileemail.text = mAuth.currentUser?.email.toString()
        //profilefullname.text =
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity<MapsActivity>()
        finish()
    }

    class Userinfo(val email: String,val name: String)
}
