package com.example.livescore

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var navMatch: TextView
    private lateinit var navSearch: TextView
    private lateinit var tvMenu: TextView
    private lateinit var tvToday: TextView

    private val BASE_URL = "http://192.168.0.2:8080"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navMatch = findViewById(R.id.navMatch)
        navSearch = findViewById(R.id.navSearch)
        tvMenu = findViewById(R.id.tvMenu)
        tvToday = findViewById(R.id.tvToday)

        setupClickEvents()
    }

    private fun setupClickEvents() {
        navMatch.setOnClickListener {
            navMatch.setTextColor(getColor(android.R.color.holo_green_light))
            navSearch.setTextColor(getColor(android.R.color.white))

            Toast.makeText(this, "경기 화면입니다", Toast.LENGTH_SHORT).show()
        }

        navSearch.setOnClickListener {
            navSearch.setTextColor(getColor(android.R.color.holo_green_light))
            navMatch.setTextColor(getColor(android.R.color.white))

            Toast.makeText(this, "검색 화면은 다음 단계에서 구현합니다", Toast.LENGTH_SHORT).show()
        }

        tvMenu.setOnClickListener {
            Toast.makeText(this, "설정 / 알림 메뉴", Toast.LENGTH_SHORT).show()
        }

        tvToday.setOnClickListener {
            Toast.makeText(this, "날짜 선택 기능", Toast.LENGTH_SHORT).show()
        }
    }
}