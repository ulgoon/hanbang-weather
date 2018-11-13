package com.example.avoirom.hanbangweather

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val latitude = intent.getStringExtra("latitude")
        val longitude = intent.getStringExtra("longitude")

        val date = LocalDateTime.now()

        /*
        val minute = date.minute
        var hour: Int

        if (minute > 10) {
            hour = date.hour + 1
        }

        hour = (hour / 3) * 3 - 1

        if (hour == -1) {

        }

        */

/*
        val year = date.year.toString()
        val month = date.monthValue.toString().padStart(2, '0')
        val day = date.dayOfMonth.toString().padStart(2, '0')
        val hour = (3 * (date.hour / 3 ) - 1).toString().padStart(2, '0')
        val minute = date.minute.toString().padStart(2, '0')
        */


        val transCoordThread = TransCoordThread(latitude, longitude)
        transCoordThread.start()

    }

    /*
    private fun weatherJsonParser(jsonString: String) {

        var category = ""
        var fcstValue = ""

        try {
            val jarray = JSONObject(jsonString).getJSONArray("item")

            Log.d(TAG, "JSON: $jarray")
            Log.d(TAG, "JSON: ${jarray.length()}")
            for (i in 0..jarray.length()) {
                val jObject = jarray.getJSONObject(i)

                category = jObject.optString("category")
                fcstValue = jObject.optString("fcstValue")

            }
        } catch (e: JSONException) {
            e.stackTrace
        }


        Log.d(TAG, "category: $category")
        Log.d(TAG, "fcstValue: $fcstValue")
    }
    */
}