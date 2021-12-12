package com.example.l2

import android.content.Context
import android.content.Intent
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import android.content.res.Resources
import android.view.*

// give up hope everyone who enters here
class WriteYourOwnRecyclerViewAdapter(val context: Context, val data: MutableList<Flight>):
    RecyclerView.Adapter<WriteYourOwnRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnCreateContextMenuListener {

        val resources = context.resources
        val flights = data

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
//            if (v!!.id == R.id.rv) {
//                val info = menuInfo as AdapterView.AdapterContextMenuInfo
//                val menuItems = resources.getStringArray(R.array.menu)
//                for (i in menuItems.indices) {
//                    menu!!.add(menuItems[i])
//                    menu[i].intent = Intent()
//                    menu[i].intent.putExtra("id", i)
//                }
//            }
        }

        var tv_number: TextView? = null
        var tv_planeType: TextView? = null
        var tv_destination: TextView? = null
        var tv_deportationTime: TextView? = null

        init {
            tv_number = view.findViewById(R.id.number)
            tv_planeType = view.findViewById(R.id.planeType)
            tv_destination = view.findViewById(R.id.destination)
            tv_deportationTime = view.findViewById(R.id.deportationTime)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.writeyourownadapter_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val flight = data[position]

        holder.tv_number?.text = flight.number.toString()
        holder.tv_planeType?.text = flight.planeType.toString()
        holder.tv_destination?.text = flight.destination.toString()
        holder.tv_deportationTime?.text = flight.deportationTime.toString()
    }

    fun getCount(): Int {
        return data.size
    }

    fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return data.size
    }
}