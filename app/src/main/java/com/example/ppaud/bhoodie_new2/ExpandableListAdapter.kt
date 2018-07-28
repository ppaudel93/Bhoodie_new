package com.example.ppaud.bhoodie_new2

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text

class ExpandableListAdapter(var context: Context,var expandableListView: ExpandableListView,var header: MutableList<String>,var tail: MutableList<MutableList<String>>): BaseExpandableListAdapter(){
    override fun getGroup(groupPosition: Int): String {
        return header[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (convertView == null){
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.layout_group,null)

        }
        val title = convertView?.findViewById<TextView>(R.id.listdays)
        title?.text=getGroup(groupPosition)
        title?.setOnClickListener {
            if(expandableListView.isGroupExpanded(groupPosition))
            {expandableListView.collapseGroup(groupPosition)
            }
            else
            {expandableListView.expandGroup(groupPosition)

            }
            Toast.makeText(context, getGroup(groupPosition),Toast.LENGTH_SHORT).show()
        }
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return tail[groupPosition].size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return tail[groupPosition][childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (convertView == null){
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.layout_child,null)

        }
        val title = convertView?.findViewById<TextView>(R.id.listdays)
        title?.text= getChild(groupPosition,childPosition) as CharSequence?
        title?.setOnClickListener {
            Toast.makeText(context, getChild(groupPosition,childPosition).toString(),Toast.LENGTH_SHORT).show()
        }
        return convertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return header.size
    }

}