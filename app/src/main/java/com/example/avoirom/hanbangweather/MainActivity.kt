package com.example.avoirom.hanbangweather

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.kittinunf.fuel.Fuel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Fuel.get("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData",
                listOf("ServiceKey" to "abc",
                        "ServiceKey" to "TEST_SERVICE_KEY",
                        "base_date" to "20180822",
                        "base_time" to "1700",
                        "nx" to "60",
                        "ny" to "127",
                        "numOfRows" to "10",
                        "pageNo" to "1",
                        "_type" to "json"
                )).response { request, response, result ->
            val (data, error) = result
            if (error == null) {
                txt_location_name.text = data.toString()
            } else {
                txt_location_name.text = error.toString()
            }
        }
    }
}
