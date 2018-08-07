package com.example.ppaud.bhoodie_new2

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.hardware.input.InputManager
import android.inputmethodservice.Keyboard
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.telephony.SmsManager
import android.text.InputType
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.github.kittinunf.fuel.Fuel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.registerdialog.*
import kotlinx.android.synthetic.main.registerdialog.view.*
import okhttp3.*
import org.jetbrains.anko.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.text.Normalizer
import kotlin.concurrent.thread


val defaulturl: String = "https://bhoodie.herokuapp.com/"
private val SMS_REQUEST_CODE=101
class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 123
    var mAuth = FirebaseAuth.getInstance()!!
    var user: FirebaseUser? = null
    fun showSnackbar(id: Int,layout: LinearLayout){
        Snackbar.make(layout,resources.getString(id),Snackbar.LENGTH_LONG).show()
    }

    private val JSON = MediaType.parse("application/json; charset=utf-8")
    private val plaintext = MediaType.parse("text/plain; charset=utf-8")

    override fun onStart() {
        super.onStart()
//        val currentuser: FirebaseUser = mAuth.currentUser!!

    }
    class userinfo(val name: String,val email: String)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window){
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition=Slide()
            exitTransition= Slide()

        }
        setContentView(R.layout.activity_main)
        val permission = ContextCompat.checkSelfPermission(this@MainActivity,android.Manifest.permission.SEND_SMS)
//        if (permission == PackageManager.PERMISSION_GRANTED)
//            //SmsManager.getDefault().sendTextMessage("+9779843378124",null,"I Am Using Bhoodie App.",null,null)
//            //sendSMS("+9779843378124","I Am Using Bhoodie App.")
//        else{
//            requestPermission(android.Manifest.permission.SEND_SMS,SMS_REQUEST_CODE)
//            if (permission == PackageManager.PERMISSION_GRANTED
//                //SmsManager.getDefault().sendTextMessage("+9779843378124",null,"I Am Using Bhoodie App.",null,null)
//                //sendSMS("+9779843378124","I Am Using Bhoodie App.")
//        }
        val view = View.inflate(this,R.layout.registerdialog,null)

        mAuth=FirebaseAuth.getInstance()
        loginbutton.setOnClickListener {
            login(emaillogin.text.toString(),passwordlogin.text.toString())
            val context: Context= this@MainActivity
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            val dialog = indeterminateProgressDialog(message = "Please wait a bit...",title="Logging in")
        }
        registerbutton.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setView(view)
            val dialog: AlertDialog = builder.create()
            val newdialog: Dialog ?= null
            dialog.show()
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            view.alertbuttonregister.setOnClickListener {
                //dialog.dismiss()
                val context: Context= this@MainActivity
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
                val dialog2 = indeterminateProgressDialog(message = "Creating new user...",title="Registering")
                mAuth.createUserWithEmailAndPassword(view.registeremail.text.toString(),view.registerpass.text.toString())
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful){
                                                        //view.dialogname
                                                        Log.i("task_successful","New User created")
                                                        //val mD = FirebaseDatabase.getInstance().getReference("users")
                                                        Toast.makeText(this@MainActivity,"New User Created",Toast.LENGTH_LONG).show()
                                                        user = mAuth.currentUser
                                                        doAsync {
                                                            val thenewuser = userinfo(view.dialogname.text.toString(),view.registeremail.text.toString())
                                                            AndroidNetworking.post(defaulturl+"api/newuser/").addBodyParameter(thenewuser)
                                                                    .setTag("test").setPriority(Priority.MEDIUM)
                                                                    .build()
                                                                    .getAsJSONArray(object: JSONArrayRequestListener{
                                                                        override fun onResponse(response: JSONArray?) {

                                                                        }

                                                                        override fun onError(anError: ANError?) {

                                                                        }

                                                                    })
                                                        }
                                                        dialog.dismiss()
                                                        login(view.registeremail.text.toString(),view.registerpass.text.toString())
                                                    }
                                                    else{

                                                        Log.i("task_failed","User Creation falied")
                                                        Toast.makeText(this@MainActivity,"User Registration Failed",Toast.LENGTH_LONG).show()
                                                    }
                                                }
            }

        }

    }

    private fun requestPermission(permissionType: String,requestCode: Int){
        ActivityCompat.requestPermissions(this, arrayOf(permissionType),requestCode)
    }


    private fun login(email: String, password: String){
        val context: Context= this@MainActivity
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
        //val dialog = progressDialog(message="Please wait a bit...",title="Logging In")
        val dialog = indeterminateProgressDialog(message = "Please wait a bit...",title="Logging in")
        mAuth.signInWithEmailAndPassword(email,password)
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
}
