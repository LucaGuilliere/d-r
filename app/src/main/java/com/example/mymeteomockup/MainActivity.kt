package com.example.mymeteomockup

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var humidityTF: TextView
    private lateinit var visibilityTF: TextView
    private lateinit var windTF: TextView
    private lateinit var temperatureTF: TextView
    private lateinit var cityTF: TextView
    private lateinit var weatherTF: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        humidityTF = findViewById(R.id.humidityTF)
        visibilityTF = findViewById(R.id.visibilityTF)
        windTF = findViewById(R.id.windTF)
        temperatureTF = findViewById(R.id.temperatureTF)
        cityTF = findViewById(R.id.cityTF)
        weatherTF = findViewById(R.id.weatherTF)

        requestLocationAndWeather()
    }

    private fun requestLocationAndWeather() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    fetchWeather(lat, lon)
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }
    private fun fetchWeather(lat: Double, lon: Double) {
        val client = OkHttpClient()
        val apiKey = "e434db3dc6c276133cd4235cc81db83f"
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$apiKey&units=metric"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Weather", "Erreur réseau", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string() ?: return)
                Log.d("API_RESPONSE", json.toString(2))
                val windSpeed = json.optJSONObject("wind")?.optDouble("speed", 0.0) ?: 0.0
                val humidity = json.optJSONObject("main")?.optInt("humidity", 0) ?: 0
                val weatherArray = json.optJSONArray("weather")
                val weatherDescription = weatherArray?.optJSONObject(0)?.optString("description", "Unknown")
                val visibility = json.optInt("visibility", 0)
                val temperature = json.optJSONObject("main")?.optDouble("temp", 0.0) ?: 0.0
                val tempRounded = temperature.roundToInt()
                val cityName = json.optString("name", "Unknown")

                val windKmH = (windSpeed * 3.6).roundToInt()
                val visibilityKm = visibility / 1000.0

                runOnUiThread {
                    windTF.text = "$windKmH km/h"
                    humidityTF.text = "$humidity%"
                    visibilityTF.text = String.format("%.1f km", visibilityKm)
                    temperatureTF.text = "$tempRounded°C"
                    cityTF.text = "$cityName"
                    weatherTF.text = "$weatherDescription"
                }
            }
        })
    }
}