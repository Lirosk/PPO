package com.example.l2

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.*

class Utils(private val context: Context) {
    // only for files in context yet
    fun getFlightsFromFile(fileName: String): MutableList<Flight> {
        val path = context.filesDir

        try {
            val writer = FileInputStream(File(path, fileName))
            val fileContent = writer.bufferedReader().use {
                it.readText()
            }

            return Gson().fromJson(fileContent, Array<Flight>::class.java).toMutableList()
        }
        catch (e: FileNotFoundException) {
            return mutableListOf<Flight>()
        }

    }

    // give up hope everyone who enters here
    fun readFlightsFromFile(fileName: String): MutableList<Flight> {
        try {
            val writer = FileInputStream(File(fileName))
            val fileContent = writer.bufferedReader().use {
                it.readText()
            }

            return Gson().fromJson(fileContent, Array<Flight>::class.java).toMutableList()
        }
        catch (e: FileNotFoundException) {
            return mutableListOf<Flight>()
        }
    }

    // only for files in context yet
    fun saveFlightsToFile(flights: MutableList<Flight>, filePath: String) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val res = gson.toJson(flights)
        val path = context.filesDir

        try {
            val writer = FileOutputStream(File(path, filePath))
            writer.write(res.toByteArray())
            writer.close()
        }
        catch (e: FileNotFoundException) {
            Log.d("Debug", "Utils.saveFlightsToFile: " + e.toString() + " " + e.message)
        }

    }
}