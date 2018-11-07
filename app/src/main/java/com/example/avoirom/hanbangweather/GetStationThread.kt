package com.example.avoirom.hanbangweather

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

// TM 좌표로 근접 미세먼지 측정소를 탐색하는 쓰레드
class GetStationThread(private val x: String, private val y: String) : Thread() {

    private val mRequestAddress = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList"
    private val mAPIkey = "서비스키"

    override fun run() {
        try {

            val url = URL("$mRequestAddress?ServiceKey=$mAPIkey&tmX=$x&tmY=$y&_returnType=json")
            val conn = url.openConnection() as HttpURLConnection

            BufferedReader(InputStreamReader (conn.inputStream, Charset.forName("UTF-8"))).use { reader ->

                val response = reader.readLine()
                val json = JSONObject(response)

                val station = (json.getJSONArray("list").get(0)) as JSONObject
                val stationName = station.getString("stationName")

                Log.d("GetStationThread", stationName)
            }

            conn.disconnect()

        } catch (e: Exception) {
            e.stackTrace
        }
    }
}