package com.example.mymeteomockup

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
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
    private lateinit var dateTF: TextView

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
        dateTF = findViewById(R.id.dateTF)

        val dateNow = java.text.SimpleDateFormat("EEEE, dd MMMM", java.util.Locale.getDefault())
        val formattedDate = dateNow.format(java.util.Date())
        dateTF.text = formattedDate.replaceFirstChar { it.uppercase() }

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
                    fetchWeeklyForecast(lat, lon) // 👈 NE PAS OUBLIER CETTE LIGNE
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

    private fun fetchWeeklyForecast(lat: Double, lon: Double) {
        val client = OkHttpClient()
        val apiKey = "e434db3dc6c276133cd4235cc81db83f"
        val url = "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&appid=$apiKey&units=metric"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Forecast", "Erreur réseau forecast", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string() ?: return)
                val list = json.getJSONArray("list")

                val forecastList = mutableListOf<ForecastDay>()

                for (i in 0 until list.length() step 8) {
                    val obj = list.getJSONObject(i)
                    val main = obj.getJSONObject("main")
                    val weather = obj.getJSONArray("weather").getJSONObject(0)
                    val timestamp = obj.getLong("dt")

                    val temp = main.getDouble("temp").roundToInt()
                    val icon = weather.getString("icon")

                    val sdf = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())
                    val dateStr = sdf.format(java.util.Date(timestamp * 1000))

                    forecastList.add(ForecastDay(temp, icon, dateStr))
                }

                runOnUiThread {
                    updateForecast(forecastList)
                }
            }
        })
    }

    data class ForecastDay(
        val temp: Int,
        val iconCode: String,
        val dateFormatted: String
    )

    private fun updateForecast(forecastList: List<ForecastDay>) {
        val forecastContainer = findViewById<LinearLayout>(R.id.forecastContainer)
        forecastContainer.removeAllViews()

        for (forecast in forecastList.take(7)) {
            val itemView = layoutInflater.inflate(R.layout.item_forecast, forecastContainer, false)

            val tempText = itemView.findViewById<TextView>(R.id.tempText)
            val iconView = itemView.findViewById<ImageView>(R.id.weatherIcon)
            val dateText = itemView.findViewById<TextView>(R.id.dateText)

            tempText.text = "${forecast.temp}°"
            dateText.text = forecast.dateFormatted

            val iconResId = when (forecast.iconCode) {
                "01d", "01n" -> R.drawable.sun
                "02d", "02n" -> R.drawable.sun_cloud
                "09d", "10d" -> R.drawable.rain
                "11d" -> R.drawable.thunder
                "13d" -> R.drawable.snow
                else -> R.drawable.cloud
            }

            iconView.setImageResource(iconResId)
            forecastContainer.addView(itemView)
        }
    }
}