package com.example.avoirom.hanbangweather

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val latitude = intent.getStringExtra("latitude")
        val longitude = intent.getStringExtra("longitude")

        Log.d("GetOne", "${latitude.toDouble()} // ${longitude.toDouble()}")
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

        val transWeatherCoordThread = TransWeatherCoordThread(latitude, longitude)
        transWeatherCoordThread.start()
    }

    inner class TransCoordThread(private val latitude: String, private val longitude: String) : Thread() {

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
                    x = coord.getString("x")
                    y = coord.getString("y")
                }

                val getStationThread = GetStationThread(x , y)
                getStationThread.start()

                conn.disconnect()

            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }

    inner class GetStationThread(private val x: String, private val y: String) : Thread() {

        private val mRequestAddress = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList"
        private val mAPIkey = "XbOzEV3nuUkqfJyF8zYK4p9wu2V1Zu82VtAORedk4J6ZcKTVH5h02Xz1uxF6TD01a3O8Qm%2Fqsj4%2BA8VnIL4Rrw%3D%3D"

        private var stationName: String = ""

        override fun run() {
            try {

                val url = URL("$mRequestAddress?ServiceKey=$mAPIkey&tmX=$x&tmY=$y&_returnType=json")
                val conn = url.openConnection() as HttpURLConnection

                BufferedReader(InputStreamReader (conn.inputStream, Charset.forName("UTF-8"))).use { reader ->

                    val response = reader.readLine()
                    val json = JSONObject(response)

                    val station = (json.getJSONArray("list").get(0)) as JSONObject
                    stationName = station.getString("stationName")
                }

                handler.post {
                    txt_station_name.text = stationName
                }

                conn.disconnect()

                val getMesuredDustThread = GetMesuredDustThread(stationName)
                getMesuredDustThread.start()


            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }

    inner class GetMesuredDustThread(private val station: String) : Thread() {

        private val mRequestAddress = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"
        private val mAPIkey = "XbOzEV3nuUkqfJyF8zYK4p9wu2V1Zu82VtAORedk4J6ZcKTVH5h02Xz1uxF6TD01a3O8Qm%2Fqsj4%2BA8VnIL4Rrw%3D%3D"

        private var pm10: String = ""
        private var pm25: String = ""

        override fun run() {
            try {

                val url = URL("$mRequestAddress?ServiceKey=$mAPIkey&stationName=$station&dataTerm=DAILY&_returnType=json&ver=1.3")
                val conn = url.openConnection() as HttpURLConnection

                BufferedReader(InputStreamReader (conn.inputStream, Charset.forName("UTF-8"))).use { reader ->

                    val response = reader.readLine()
                    val json = JSONObject(response)

                    val mValue = (json.getJSONArray("list").get(0)) as JSONObject

                    pm10 = mValue.getString("pm10Value")
                    pm25 = mValue.getString("pm25Value")
                }

                handler.post {
                    txt_pm10.text = pm10
                    txt_pm25.text = pm25
                }

                conn.disconnect()

            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }

    inner class TransWeatherCoordThread(private val latitude: String, private val longitude: String) : Thread() {

        var RE = 6371.00877 // 지구 반경(km)
        var GRID = 5.0 // 격자 간격(km)
        var SLAT1 = 30.0 // 투영 위도1(degree)
        var SLAT2 = 60.0 // 투영 위도2(degree)
        var OLON = 126.0 // 기준점 경도(degree)
        var OLAT = 38.0 // 기준점 위도(degree)
        var XO = 210 / GRID // 기준점 X좌표(GRID)
        var YO = 675 / GRID// 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //

        
        var PI = Math.PI
        var DEGRAD = PI / 180.0
        var RADDEG = 180.0 / PI

        var re = RE / GRID
        var slat1 = SLAT1 * DEGRAD
        var slat2 = SLAT2 * DEGRAD
        var olon = OLON * DEGRAD
        var olat = OLAT * DEGRAD

        override fun run() {
            try {
                var sn = Math.tan(PI * 0.25 + slat2 * 0.5) / Math.tan(PI * 0.25 + slat1 * 0.5)
                sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
                var sf = Math.tan(PI * 0.25 + slat1 * 0.5)
                sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
                var ro = Math.tan(PI * 0.25 + olat * 0.5)
                ro = re * sf / Math.pow(ro, sn)

                Log.d("Get", "${latitude.toDouble()} // ${longitude.toDouble()}")
                var ra = Math.tan(PI * 0.25 + (latitude.toDouble()) * DEGRAD * 0.5)
                ra = re * sf / Math.pow(ra, sn)
                var theta = longitude.toDouble() * DEGRAD - olon

                if (theta > PI) theta -= 2.0 * PI
                if (theta < -PI) theta += 2.0 * PI
                theta *= sn;

                val x = Math.floor(ra * Math.sin(theta) + XO + 1.5).toString()
                val y = Math.floor(ro - ra * Math.cos(theta) + YO + 1.5).toString()

                Log.d("GetTransThread", "$x // $y")
                //val getWeatherThread = GetWeatherThread(x, y, "20190103", "0900")
                //getWeatherThread.start()

            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }

    inner class GetWeatherThread(private val x: String, private val y: String, private val mDate: String, private val mTime: String) : Thread() {

        private val mRequestAddress = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastGrib"
        private val mAPIkey = "XbOzEV3nuUkqfJyF8zYK4p9wu2V1Zu82VtAORedk4J6ZcKTVH5h02Xz1uxF6TD01a3O8Qm%2Fqsj4%2BA8VnIL4Rrw%3D%3D"

        private var t1h: String = ""
        private var sky: String = ""
        private var pty: String = ""
        private var rn1: String = ""

        override fun run() {
            try {

                val url = URL("$mRequestAddress?ServiceKey=$mAPIkey&base_date=$mDate&base_time=$mTime&nx=$x&ny=$y&_type=json")
                val conn = url.openConnection() as HttpURLConnection

                BufferedReader(InputStreamReader (conn.inputStream, Charset.forName("UTF-8"))).use { reader ->

                    val response = reader.readLine()
                    val json = JSONObject(response).getJSONObject("response").getJSONObject("body")

                    /*
                    val mValue = (json.getJSONArray("item").get(0)) as JSONObject
                    t1h = mValue.getString("t1h")
                    sky = mValue.getString("sky")
                    pty = mValue.getString("pty")
                    rn1 = mValue.getString("rn1")
*/
                    Log.d("GetWeatherThread", json.getJSONObject("items").toString())
                }
                conn.disconnect()

                handler.post {
                    txt_t1h.text = "100"
                    txt_sky.text = "200"
                    txt_pty.text = "300"
                    txt_rn1.text = "400"
                }

            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }
}