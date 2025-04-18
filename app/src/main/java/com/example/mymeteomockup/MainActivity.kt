package com.example.mymeteomockup

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    /*private var humidityTF= findViewById<TextView>(R.id.humidityTF)
    private var visibilityTF= findViewById<TextView>(R.id.visibilityTF)
    private var windTF= findViewById<TextView>(R.id.windTF)*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /*val windSpeed = location.weather.wind.speed
        val windKmH = (windSpeed * 3.6).roundToInt()
        windTF.setText("$windKmH km/h")

        val humidity = location.weather.main.humidity
        humidityTF.setText("$humidity%")

        val visibilityMeters = location.weather.visibility
        val visibilityKm = visibilityMeters / 1000.0
        visibilityTF.setText("$visibilityKm km")*/

    }
}