package com.example.avoirom.hanbangweather

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class GetWeatherThread(private val x: String, private val y: String, private val mDate: String, private val mTime: String) : Thread() {

    private val mRequestAddress = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData"
    private val mAPIkey = "XbOzEV3nuUkqfJyF8zYK4p9wu2V1Zu82VtAORedk4J6ZcKTVH5h02Xz1uxF6TD01a3O8Qm%2Fqsj4%2BA8VnIL4Rrw%3D%3D"

    override fun run() {
        try {

            val url = URL("$mRequestAddress?ServiceKey=$mAPIkey&base_date=20181201&base_time=0500&nx=60&ny=127&_type=json")
            val conn = url.openConnection() as HttpURLConnection

            BufferedReader(InputStreamReader (conn.inputStream, Charset.forName("UTF-8"))).use { reader ->

                val response = reader.readLine()
                val json = JSONObject(response)

                val mValue = (json.getJSONArray("list").get(0)) as JSONObject
                val mCategory = mValue.keys()

                for (i in mCategory)
                    Log.d("GetWeatherThread", "$i : ${mValue.getString(i)}")
            }
            conn.disconnect()

        } catch (e: Exception) {
            e.stackTrace
        }
    }
}