package com.example.ppaud.bhoodie_new2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.ppaud.bhoodie_new2.R.id.recommend_id
import kotlinx.android.synthetic.main.activity_recommendation.*
import org.jetbrains.anko.startActivity

class Recommendation : AppCompatActivity() {
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)
        //viewManager=LinearLayoutManager(this)
        //viewAdapter=MyAdapter()

        mDrawerLayout=findViewById(R.id.drawer_layout_recommend)
        val navigationView: NavigationView =findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener {
            it.isChecked = true
            if (it.itemId == R.id.show_map)
                startActivity<MapsActivity>()
            if (it.itemId==recommend_id)
                mDrawerLayout.closeDrawers()
            true
        }
        preferencesbutton.setOnClickListener {

        }

//        recyclerView=findViewById<RecyclerView>(R.id.recycler).apply {
//            setHasFixedSize(true)
//            layoutManager=viewManager
//            adapter=viewAdapter
//        }
    }

    class MyAdapter(private val myDataset: Array<String>) :
            RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyAdapter.ViewHolder {
            // create a new view
            val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.mapsdialogbox, parent, false) as TextView
            // set the view's size, margins, paddings and layout parameters

            return ViewHolder(textView)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.textView.text = myDataset[position]
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }
}
