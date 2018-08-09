package com.example.ppaud.bhoodie_new2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_aboutus.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.email

class aboutus : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutus)
        aboutusback.setOnClickListener {
            finish()
        }
        prayogemail.setOnClickListener {
            email(prayogemail.text.toString(),"About Bhoodie Project"
                    ,"I am contacting regarding Bhoodie project")
        }
        shreyamemail.setOnClickListener {
            email(shreyamemail.text.toString(),"About Bhoodie Project"
                    ,"I am contacting regarding Bhoodie project")
        }
        githubandroid.setOnClickListener {
            browse(githubandroid.text.toString())
        }
        githubserver.setOnClickListener {
            browse(githubserver.text.toString())
        }
    }
}
