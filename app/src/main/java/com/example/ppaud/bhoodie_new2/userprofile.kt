package com.example.ppaud.bhoodie_new2

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_userprofile.*
import org.jetbrains.anko.startActivity

class userprofile : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userprofile)

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
}
