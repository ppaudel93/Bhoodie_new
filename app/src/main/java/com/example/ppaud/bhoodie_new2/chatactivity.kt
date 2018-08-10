package com.example.ppaud.bhoodie_new2

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Slide
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_chatactivity.*
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "ChatActivity"

class chatactivity : AppCompatActivity() {
    private lateinit var adapter: MessageAdapter
    private var mAuth = FirebaseAuth.getInstance()
    private lateinit var placedetails: PlaceInfo.mainobject
    private var defaulturl = "https://bhoodie.herokuapp.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(window){
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            enterTransition= Slide()
            exitTransition= Slide()

        }
        setContentView(R.layout.activity_chatactivity)
        val placeid = intent.getStringExtra("placeid")
        var rating = intent.getStringExtra("rating")
        if (rating.isNullOrBlank())
            rating="4"
        val isitopen = intent.getBooleanExtra("openornot",true)
//        doAsync {
//            val request = Request.Builder().url(defaulturl+"/api/pdetail/id=$placeid").build()
//            val client = OkHttpClient()
//            client.newCall(request).enqueue(object: Callback{
//                override fun onFailure(call: Call?, e: IOException?) {
//                    Log.i("requeststatus","Request Failed")
//
//                }
//
//                override fun onResponse(call: Call?, response: Response?) {
//
//                    Log.i("requeststatus","Request Success 123123123")
//                    val body = response?.body()?.string()
//                    val gson = GsonBuilder().create()
//                    placedetails = gson.fromJson(body, PlaceInfo.mainobject::class.java)
//                }
//
//            })
//        }

        messagelist.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(this)
        messagelist.adapter = adapter

        chatback.setOnClickListener {
            finish()
        }


        btnSend.setOnClickListener {
            if (txtMessage.text.isNullOrBlank()){
                longToast("Message cannot be blank")
            }else{
                doAsync {
                    val message: Message = Message(txtMessage.text.toString()
                            ,mAuth.currentUser?.email.toString()
                            ,DateUtils.fromMillisToTimeString(Calendar.getInstance().timeInMillis),placeid)
                    AndroidNetworking.post("https://bhoodie.herokuapp.com/chatbot/").addBodyParameter(message)
                            .setPriority(Priority.MEDIUM).setTag("test").build()
                            .getAsJSONObject(object: JSONObjectRequestListener{
                                override fun onResponse(response: JSONObject?) {
                                    Log.i("requeststatus","MESSAGE RECEIVED")
                                    val body = response.toString()
                                    val gson = GsonBuilder().create()
                                    var receivedmessage = gson.fromJson(body,message::class.java)
                                    receivedmessage.time = DateUtils.fromMillisToTimeString(receivedmessage.time.toLong())
                                    Log.i("requeststatus","asdasd"+receivedmessage.message)
                                    adapter.addMessage(message)
                                    adapter.addMessage(receivedmessage)
                                    messagelist.scrollToPosition(adapter.itemCount-1)
                                }

                                override fun onError(anError: ANError?) {
                                    Log.i("requeststatus","MESSAGE NOT RECEIVED")

                                }

                            })
                }


                resetInput()

            }
        }





    }

    object DateUtils{
        fun fromMillisToTimeString(millis: Long): String{
            val format = SimpleDateFormat("hh:mm a",Locale.getDefault())
            return format.format(millis)
        }
    }

    private fun resetInput() {
        // Clean text box
        txtMessage.text.clear()

        // Hide keyboard
        val inputManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
                currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    class Message(val message: String, val sender: String, var time: String, val receiver: String)

}
