package com.example.ppaud.bhoodie_new2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import com.example.ppaud.bhoodie_new2.R.id.recommend_id
import org.jetbrains.anko.startActivity

class Recommendation : AppCompatActivity() {
    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)

        mDrawerLayout=findViewById(R.id.drawer_layout)
        val navigationView: NavigationView =findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            it.isChecked = true
            if (it.itemId == R.id.show_map)
                startActivity<MapsActivity>()
            if (it.itemId==recommend_id)
                mDrawerLayout.closeDrawers()
            true
        }
    }
}
