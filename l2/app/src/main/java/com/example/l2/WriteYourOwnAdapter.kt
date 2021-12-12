package com.example.l2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class WriteYourOwnAdapter(private val context: Context, private val data: MutableList<Flight>, private val dataFileName: String): BaseAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.writeyourownadapter_layout, parent, false)

        val tvNumber: TextView = view.findViewById(R.id.number)
        val tvPlaneType: TextView = view.findViewById(R.id.planeType)
        val tvDestination: TextView = view.findViewById(R.id.destination)
        val tvDeportationType: TextView = view.findViewById(R.id.deportationTime)

        val flight: Flight = data[position]

        tvNumber.text = flight.number.toString()
        tvPlaneType.text = flight.planeType.toString()
        tvDestination.text = flight.destination.toString()
        tvDeportationType.text = flight.deportationTime.toString()

        return view
    }

    override fun notifyDataSetChanged() {
        Utils(context).saveFlightsToFile(data, dataFileName)
        data.sortBy { it.deportationTime }
        super.notifyDataSetChanged()
    }
}