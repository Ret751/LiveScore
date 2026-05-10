package com.example.livescore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.livescore.databinding.ActivityMatchDetailBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MatchDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Intent에서 데이터 꺼내기
        val fixtureId = intent.getLongExtra("fixtureId", -1L)
        val homeTeam = intent.getStringExtra("homeTeam")
        val awayTeam = intent.getStringExtra("awayTeam")
        val homeTeamId = intent.getIntExtra("homeTeamId", 0)
        val awayTeamId = intent.getIntExtra("awayTeamId", 0)
        val matchTime = intent.getStringExtra("matchTime")
        val matchDate = intent.getStringExtra("matchDate")
        val leagueId = intent.getIntExtra("leagueId", 0)
        val season = intent.getIntExtra("season", 2024)
        val stadium = intent.getStringExtra("stadium")
        val matchRound = intent.getStringExtra("matchRound")

        // 2. 상단 헤더 UI 세팅
        binding.tvDetailHomeName.text = homeTeam
        binding.tvDetailAwayName.text = awayTeam
        binding.tvDetailTime.text = matchTime

        // 날짜 형식 가공 (예: "05-07" -> "5월 7일")
        if (matchDate != null && matchDate.contains("-")) {
            try {
                val dateParts = matchDate.split("-")
                val month = dateParts[0].toInt().toString()
                val day = dateParts[1].toInt().toString()

                val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"))
                if (matchDate == todayStr) {
                    binding.tvDetailDateStatus.text = "오늘 (${month}월 ${day}일)"
                } else {
                    binding.tvDetailDateStatus.text = "${month}월 ${day}일"
                }
            } catch (e: Exception) {
                binding.tvDetailDateStatus.text = matchDate
            }
        } else {
            binding.tvDetailDateStatus.text = matchDate ?: "날짜 미정"
        }

        // 팀 로고 로딩
        val homeLogoUrl = "https://media.api-sports.io/football/teams/$homeTeamId.png"
        val awayLogoUrl = "https://media.api-sports.io/football/teams/$awayTeamId.png"

        Glide.with(this).load(homeLogoUrl).into(binding.ivDetailHomeLogo)
        Glide.with(this).load(awayLogoUrl).into(binding.ivDetailAwayLogo)

        // 3. 뷰페이저(ViewPager2) 및 탭 레이아웃 설정
        val adapter = MatchDetailPagerAdapter(
            this,
            fixtureId,
            leagueId,
            season,
            homeTeamId,
            awayTeamId,
            stadium,     // 🌟 추가됨
            matchRound   // 🌟 추가됨
        )
        binding.viewPager.adapter = adapter

        // 탭 제목 설정
        val tabTitles = listOf("미리보기", "라인업", "순위", "상대전적")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // 4. 뒤로가기 버튼 기능
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}