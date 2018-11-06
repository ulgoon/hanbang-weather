package com.example.avoirom.hanbangweather

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
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

        /*
        Fuel.get("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getNearbyMsrstnList",
            listOf("tmX" to -398702.7502,
                "tmY" to 596416.9718,
                "pageNo" to 1,
                "numOfRows" to 10,
                "ServiceKey" to "XbOzEV3nuUkqfJyF8zYK4p9wu2V1Zu82VtAORedk4J6ZcKTVH5h02Xz1uxF6TD01a3O8Qm%2Fqsj4%2BA8VnIL4Rrw%3D%3D"
            )).responseJson { request, response, result ->

            if (response.httpResponseMessage == "OK" && response.httpStatusCode == 200) {
                //Log.d(TAG, result.get().obj().toString())
            }
        }

        val date = LocalDateTime.now()
*/
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
/*
        Fuel.get("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData",
            listOf("ServiceKey" to "XbOzEV3nuUkqfJyF8zYK4p9wu2V1Zu82VtAORedk4J6ZcKTVH5h02Xz1uxF6TD01a3O8Qm%2Fqsj4%2BA8VnIL4Rrw%3D%3D",
                "ServiceKey" to "TEST_SERVICE_KEY",
                "base_date" to year + month + day,
                "base_time" to hour + minute,
                "nx" to latitude,
                "ny" to longitude,
                "numOfRows" to "10",
                "pageNo" to "1",
                "_type" to "json"
            )).responseJson { request, response, result ->

            if (response.httpResponseMessage == "OK" && response.httpStatusCode == 200) {
                weatherJsonParser(result.get().obj().getJSONObject("response").getJSONObject("body").getJSONObject("items").toString())
            }
        }
        */

        val transCoordThread = TransCoordThread(latitude, longitude)
        transCoordThread.start()

    }

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
}