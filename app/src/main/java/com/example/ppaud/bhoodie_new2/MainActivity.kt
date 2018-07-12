package com.example.ppaud.bhoodie_new2

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
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
        setColor(Color.parseColor("#FF4081"))
        setStroke(2, Color.parseColor("#d06666"))
    }
    fun shape_roundeddialog()= GradientDrawable().apply{
        shape= GradientDrawable.RECTANGLE
        cornerRadius=0f
        setColor(Color.parseColor("#474747"))
        setStroke(5, Color.parseColor("#d06666"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        verticalLayout {
            padding = dip(30)
            editText {
                hint = "Email"
                textSize = 20f
                gravity= Gravity.CENTER
                background=shape_roundedrect()
            }.lparams() { topPadding=dip(50)
                width= matchParent
                height=dip(60)
                topMargin=dip(20)
                leftMargin=dip(20)
                rightMargin=dip(20)
            }
            editText {
                hint = "Password"
                textSize = 20f
                gravity= Gravity.CENTER
                background=shape_roundedrect()
            }.lparams() {
                width= matchParent
                height=dip(60)
                topMargin=dip(20)
                leftMargin=dip(20)
                rightMargin=dip(20)
            }
            var loginbutton = button {
                text = getString(R.string.login)
                textSize=20f
                allCaps=false
                gravity= Gravity.CENTER
                background=shape_roundedrectbut()
                setOnClickListener{
                    startActivity<MapsActivity>()
                }
            }.lparams(){
                width = dip(120)
                height = dip(60)
                topMargin=dip(20)
                //leftMargin=dip(120)
            }
            var registerbutton =  button{
                text=getString(R.string.register)
                textSize=20f
                allCaps=false
                gravity= Gravity.CENTER
                background=shape_roundedrectbut()
                setOnClickListener(){
                    alert(){
                        customView() {
                            width= wrapContent
                            height= wrapContent
                            verticalLayout {
                                background= shape_roundeddialog()
                                bottomPadding=dip(20)
                                editText {
                                    hint = "Full Name"
                                    textSize = 20f
                                    gravity = Gravity.CENTER
                                    background = shape_roundedrect()
                                }.lparams() {
                                    topPadding = dip(50)
                                    width = matchParent
                                    height = dip(60)
                                    topMargin = dip(20)
                                    leftMargin = dip(20)
                                    rightMargin = dip(20)
                                }
                                editText {
                                    hint = "Email"
                                    textSize = 20f
                                    gravity = Gravity.CENTER
                                    background = shape_roundedrect()
                                }.lparams() {
                                    topPadding = dip(50)
                                    width = matchParent
                                    height = dip(60)
                                    topMargin = dip(20)
                                    leftMargin = dip(20)
                                    rightMargin = dip(20)
                                }
                                editText {
                                    hint = "Password"
                                    textSize = 20f
                                    gravity = Gravity.CENTER
                                    background = shape_roundedrect()
                                }.lparams() {
                                    topPadding = dip(50)
                                    width = matchParent
                                    height = dip(60)
                                    topMargin = dip(20)
                                    leftMargin = dip(20)
                                    rightMargin = dip(20)
                                }
                                button {
                                    text = getString(R.string.register)
                                    textSize = 20f
                                    allCaps = false
                                    gravity = Gravity.CENTER
                                    background = shape_roundedrectbut()
                                    setOnClickListener {
                                        //startActivity<MapsActivity>()
                                    }
                                }.lparams() {
                                    width = dip(120)
                                    height = dip(60)
                                    topMargin = dip(20)
                                    horizontalGravity = Gravity.CENTER
                                    //leftMargin=dip(120)
                                }

                            }
                        }
                        overridePendingTransition(R.anim.abc_popup_enter,R.anim.abc_popup_exit)
                    }.show()
                    //overridePendingTransition(R.anim.abc_popup_enter,R.anim.abc_popup_exit)
                }
            }.lparams(){
                width = dip(120)
                height = dip(60)
                topMargin=dip(20)
                horizontalGravity= Gravity.CENTER
                //leftMargin=dip(120)
            }

        }

    }
}
