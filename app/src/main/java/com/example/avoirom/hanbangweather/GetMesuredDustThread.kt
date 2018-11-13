package com.example.avoirom.hanbangweather

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

// 미세먼지 측정소로부터 측정된 미세먼지 값을 받아오는 쓰레드
class GetMesuredDustThread(private val station: String) : Thread() {

    private val mRequestAddress = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"
    private val mAPIkey = "XbOzEV3nuUkqfJyF8zYK4p9wu2V1Zu82VtAORedk4J6ZcKTVH5h02Xz1uxF6TD01a3O8Qm%2Fqsj4%2BA8VnIL4Rrw%3D%3D"

    override fun run() {
        try {

            val url = URL("$mRequestAddress?ServiceKey=$mAPIkey&stationName=$station&dataTerm=DAILY&_returnType=json&ver=1.3")
            val conn = url.openConnection() as HttpURLConnection

            BufferedReader(InputStreamReader (conn.inputStream, Charset.forName("UTF-8"))).use { reader ->

                val response = reader.readLine()
                val json = JSONObject(response)

                val mValue = (json.getJSONArray("list").get(0)) as JSONObject
                val mCategory = mValue.keys()

                for (i in mCategory)
                Log.d("GetMeasuredDustThread", "$i : ${mValue.getString(i)}")
            }
            conn.disconnect()

        } catch (e: Exception) {
            e.stackTrace
        }
    }

}