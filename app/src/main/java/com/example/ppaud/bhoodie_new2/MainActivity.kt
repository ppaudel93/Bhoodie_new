package com.example.ppaud.bhoodie_new2

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.hardware.input.InputManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.registerdialog.*
import kotlinx.android.synthetic.main.registerdialog.view.*
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {
    fun shape_roundedrect()= GradientDrawable().apply{
        shape= GradientDrawable.RECTANGLE
        cornerRadius=200f
        setStroke(2, Color.parseColor("#d06666"))
    }
    fun shape_roundedrectbut()= GradientDrawable().apply{
        shape= GradientDrawable.RECTANGLE
        cornerRadius=200f
        setColor(getColor(R.color.colorAccent))
        //setColor(Color.parseColor("#FF4081"))
        setStroke(2, Color.parseColor("#d06666"))
    }
    fun shape_roundeddialog()= GradientDrawable().apply{
        shape= GradientDrawable.RECTANGLE
        cornerRadius=30f
        setColor(getColor(R.color.colorPrimaryDark))
        //setColor(Color.parseColor("#474747"))
        setStroke(5, Color.parseColor("#d06666"))
    }
    private val RC_SIGN_IN = 123
    var mAuth = FirebaseAuth.getInstance()!!
    lateinit var mDatabase: DatabaseReference
    var user: FirebaseUser? = null
    fun showSnackbar(id: Int,layout: LinearLayout){
        Snackbar.make(layout,resources.getString(id),Snackbar.LENGTH_LONG).show()
    }

    class userinfo(email: String,name: String,key: String)

    override fun onStart() {
        super.onStart()
//        val currentuser: FirebaseUser = mAuth.currentUser!!

    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view = View.inflate(this,R.layout.registerdialog,null)
//        myRef.setValue("Hello").addOnSuccessListener {
//            Log.i("pushsuccessful","push is successful")
//        }.addOnFailureListener {
//            Log.i("pushfailed","push has failed")
//        }
        emaillogin.background=shape_roundedrect()
        passwordlogin.background=shape_roundedrect()
        loginbutton.background=shape_roundedrectbut()
        registerbutton.background=shape_roundedrectbut()


        //val loginbut = findViewById(R.id.emaillogin)

        mAuth=FirebaseAuth.getInstance()
        loginbutton.setOnClickListener {
            mAuth.signInWithEmailAndPassword(emaillogin.text.toString(),passwordlogin.text.toString())
                                .addOnCompleteListener{
                                if(it.isSuccessful){
                                    Log.i("user_login","Logged In Successfully")
                                    user= mAuth.currentUser!!
                                    Toast.makeText(this@MainActivity,"Logged in as ${user!!.email.toString()}",Toast.LENGTH_LONG).show()
                                    startActivity<MapsActivity>()
                                    finish()

                                }
                                else{
                                    Log.i("user_login","Login Failed")

                                }
                            }
                    //startActivity<MapsActivity>()
        }
        registerbutton.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setView(view)
            val dialog: AlertDialog = builder.create()
            val newdialog: Dialog ?= null
            dialog.show()
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            view.alertbuttonregister.background=shape_roundedrectbut()
//            view.dialogname.background=shape_roundedrect()
//            view.registeremail.background=shape_roundedrect()
//            view.registerpass.background=shape_roundedrect()
            //alertbuttonregister.background=shape_roundedrectbut()

            view.alertbuttonregister.setOnClickListener {
                mAuth.createUserWithEmailAndPassword(view.registeremail.text.toString(),view.registerpass.text.toString())
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful){
                                                        Log.i("task_successful","New User created")
                                                        //val mD = FirebaseDatabase.getInstance().getReference("users")
                                                        Toast.makeText(this@MainActivity,"New User Created",Toast.LENGTH_LONG).show()
                                                        user = mAuth.currentUser
                                                        //val newuser: userinfo = userinfo(view.registeremail.text.toString(),view.dialogname.text.toString(),user!!.uid)
                                                        //mD.child(user!!.uid).setValue(newuser)
                                                        dialog.dismiss()
                                                        mAuth.signInWithEmailAndPassword(view.registeremail.text.toString(),view.registerpass.text.toString())
                                                                .addOnCompleteListener{
                                                                    if(it.isSuccessful){
                                                                        Log.i("user_login","Logged In Successfully")
                                                                        user= mAuth.currentUser!!
                                                                        Toast.makeText(this@MainActivity,"Logged in as ${user!!.email.toString()}",Toast.LENGTH_LONG).show()
                                                                        startActivity<MapsActivity>()
                                                                        finish()

                                                                    }
                                                                    else{
                                                                        Log.i("user_login","Login Failed")

                                                                    }
                                                                }

                                                    }
                                                    else{
                                                        Log.i("task_failed","User Creation falied")
                                                    }
                                                }
                                        //startActivity<MapsActivity>()
            }

        }

    }
}
