package com.example.l2

data class Flight(val number: Int, val planeType: String,
             val destination: String, val deportationTime: String) {

    fun toArray(): Array<String> {
        return arrayOf(number.toString(), planeType.toString(), destination.toString(), deportationTime.toString())
    }

    override fun toString(): String {
        return toArray().toString()
    }
}