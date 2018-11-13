package com.example.avoirom.hanbangweather

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

// Android GPS 경도, 위도 값을 TM 좌표로 변경해주는 쓰레드
class TransCoordThread(private val latitude: String, private val longitude: String) : Thread() {

    private val mRequestAddress = "https://dapi.kakao.com/v2/local/geo/transcoord"
    private val mAPIkey = "0aea8adb196b0c94e06dfc9bb6e95510"

    private var x: String = ""
    private var y: String = ""

    override fun run() {
        try {

            val url = URL("$mRequestAddress.json?x=$longitude&y=$latitude&input_coord=WGS84&output_coord=TM")
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "KakaoAK $mAPIkey")

            BufferedReader(InputStreamReader (conn.inputStream, Charset.forName("UTF-8"))).use { reader ->

                val response = reader.readLine()
                val json = JSONObject(response)
                val coord = (json.getJSONArray("documents").get(0)) as JSONObject
                val x = coord.getString("x")
                val y = coord.getString("y")

                Log.d("TransCoordThread", "$x $y")

                this.x = x
                this.y = y
            }

            conn.disconnect()

            val getStationThread = GetStationThread(x , y)
            getStationThread.start()

        } catch (e: Exception) {
            e.stackTrace
        }
    }
}