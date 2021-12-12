package com.example.l2

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toFile
import androidx.core.view.get
import java.lang.Exception
import java.net.URISyntaxException


class MainActivity : AppCompatActivity() {
    private var context: Context? = null

    private var flights: MutableList<Flight> = mutableListOf()

    private var lv: ListView? = null
    private var adapter: WriteYourOwnAdapter? = null

    private var main_layout: ConstraintLayout? = null
    private var second_layout: LinearLayout? = null

    private var layoutAddAndEditFileButtons: ConstraintLayout? = null
    private var layoutDoneButton: ConstraintLayout? = null

    private var et_number: EditText? = null
    private var et_planeType: EditText? = null
    private var et_destination: EditText? = null
    private var et_deportationTime: EditText? = null

    private var scnd_layout_action = Action.NONE
    private var editing_item = -1

    private val dataPath = "data.json"
    private var tempDataPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = applicationContext

        Utils(this).getFlightsFromFile(dataPath).sortedBy { it.deportationTime }.forEach {
            flights.add(it)
        }
        adapter = WriteYourOwnAdapter(context!!, flights, dataPath)
        lv = findViewById(R.id.lv)
        //rv!!.layoutManager = LinearLayoutManager(this)
        lv!!.adapter = adapter
        adapter!!.notifyDataSetChanged()

        registerForContextMenu(lv)

        main_layout = findViewById(R.id.main_layout)
        second_layout = findViewById(R.id.second_layout)

        layoutAddAndEditFileButtons = findViewById(R.id.layoutAddAndEditFileButtons)
        layoutDoneButton = findViewById(R.id.layoutDoneButton)

        et_number = findViewById(R.id.et_number)
        et_planeType = findViewById(R.id.et_planeType)
        et_destination = findViewById(R.id.et_destination)
        et_deportationTime = findViewById(R.id.et_deportationTime)

        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            scnd_layout_action = Action.ADD
            invertLayouts()
        }

        findViewById<Button>(R.id.btnEditFile).setOnClickListener {
            ChooseFile()
            invertButtonsLayouts()
        }

        findViewById<Button>(R.id.btnApply).setOnClickListener {
            val number = Integer.parseInt(et_number!!.text.toString())
            val planeType = et_planeType!!.text.toString()
            val destination = et_destination!!.text.toString()
            val deportationTime = et_deportationTime!!.text.toString()
            val flight = Flight(number, planeType, destination, deportationTime)

            if (scnd_layout_action == Action.ADD) {
                flights.add(flight)
            }
            else if (scnd_layout_action == Action.EDIT) {
                flights[editing_item] = flight
                editing_item = -1
            }
            clearETS()
            scnd_layout_action = Action.NONE
            invertLayouts()
            adapter!!.notifyDataSetChanged()
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            clearETS()
            scnd_layout_action = Action.NONE
            invertLayouts()
        }

        findViewById<Button>(R.id.btnDone).setOnClickListener {
            adapter = WriteYourOwnAdapter(context!!, Utils(context!!).getFlightsFromFile(dataPath), dataPath)
            lv!!.adapter = adapter
            invertButtonsLayouts()
        }
    }

    private var FILE_SELECT_CODE = 0;

    private fun ChooseFile() {
        var data = Intent(Intent.ACTION_GET_CONTENT)
        data.type = "*/*"
        data = Intent.createChooser(data, "Choose a file")
        startActivityForResult(data, FILE_SELECT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            if (data == null) {
                invertButtonsLayouts()
                return
            }

            try {
                val fileName = data.data!!.toFile().name

                tempDataPath = data.dataString!!
                adapter = WriteYourOwnAdapter(
                    this,
                    Utils(context!!).readFlightsFromFile(fileName).sortedBy { it.deportationTime }.toMutableList(),
                    tempDataPath
                )
                lv!!.adapter = adapter
            }
            catch (e: Exception) {
                Log.d("Debug", "onActivityResult: " + e.toString() + " " + e.message)
            }

        }
        else
        {
            invertButtonsLayouts()
            return
        }
    }

    fun clearETS() {
        et_number!!.text.clear()
        et_planeType!!.text.clear()
        et_destination!!.text.clear()
        et_deportationTime!!.text.clear()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        if (v.id == R.id.lv) {
            val info = menuInfo as AdapterView.AdapterContextMenuInfo
            val menuItems = resources.getStringArray(R.array.menu)
            for (i in menuItems.indices) {
                menu.add(menuItems[i])
                menu[i].intent = Intent()
                menu[i].intent.putExtra("id", i)
            }
        }
        else {
            super.onCreateContextMenu(menu, v, menuInfo)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo

        //item.itemId == 0  // true
        val menuItemId = item.intent.getIntExtra("id", -1)
        val menuItemName = resources.getStringArray(R.array.menu)[menuItemId]
        val flight = flights[info.position]

        when(menuItemName) {
            getString(R.string.Delete) -> {
                flights.remove(flight)
                adapter!!.notifyDataSetChanged()
            }
            getString(R.string.Edit) -> {
                scnd_layout_action = Action.EDIT
                editing_item = info.position

                et_number!!.text = SpannableStringBuilder(flights[editing_item].number.toString())
                et_planeType!!.text = SpannableStringBuilder(flights[editing_item].planeType.toString())
                et_destination!!.text = SpannableStringBuilder(flights[editing_item].destination.toString())
                et_deportationTime!!.text = SpannableStringBuilder(flights[editing_item].deportationTime.toString())

                invertLayouts()
            }
        }

        return true
    }

    fun invertLayouts() {
        if (main_layout!!.visibility == View.VISIBLE) {
            main_layout!!.visibility = View.INVISIBLE
            second_layout!!.visibility = View.VISIBLE
        }
        else {
            main_layout!!.visibility = View.VISIBLE
            second_layout!!.visibility = View.INVISIBLE
        }
    }

    fun invertButtonsLayouts() {
        if (layoutAddAndEditFileButtons!!.visibility == View.VISIBLE) {
            layoutAddAndEditFileButtons!!.visibility = View.INVISIBLE
            layoutDoneButton!!.visibility = View.VISIBLE
        }
        else {
            layoutAddAndEditFileButtons!!.visibility = View.VISIBLE
            layoutDoneButton!!.visibility = View.INVISIBLE
        }
    }
}

