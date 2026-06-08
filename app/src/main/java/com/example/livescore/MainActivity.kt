package com.example.livescore

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.example.livescore.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var matchAdapter: MatchAdapter
    private lateinit var dateAdapter: DateAdapter

    private var allMatches = mutableListOf<MatchData>()

    private var selectedDate: LocalDate = LocalDate.now()
    private var currentLeagueId: Int? = null
    // 초기화 시 centerPosition을 정확히 세팅하기 위해 Int.MAX_VALUE / 2 로 맞춤
    private var currentPosition: Int = Int.MAX_VALUE / 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        setupLeagueTabs()
        setupCalendar()
        updateCalendarButtonText()

        loadDataFromServer()
    }

    private fun updateCalendarButtonText() {
        val shortYear = selectedDate.year % 100
        val month = selectedDate.monthValue
        val day = selectedDate.dayOfMonth
        binding.btnCalendar.text = "$shortYear/$month/$day ▼"
    }

    private fun setupRecyclerViews() {
        dateAdapter = DateAdapter { date, position ->
            selectedDate = date
            moveDateListToPosition(position)
            updateCalendarButtonText()
            applyFilters()
        }

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDateList.layoutManager = layoutManager
        binding.rvDateList.adapter = dateAdapter

        binding.rvDateList.post {
            val offset = (binding.rvDateList.width / 2) - 100
            layoutManager.scrollToPositionWithOffset(dateAdapter.centerPosition, offset)
        }
        currentPosition = dateAdapter.centerPosition

        matchAdapter = MatchAdapter(mutableListOf())
        binding.rvMatches.layoutManager = LinearLayoutManager(this)
        binding.rvMatches.adapter = matchAdapter
    }

    private fun setupLeagueTabs() {
        val tabs = listOf(
            binding.tabAll, binding.tabEpl, binding.tabLaLiga,
            binding.tabSerieA, binding.tabBundesliga, binding.tabLigue1
        )

        binding.tabAll.setOnClickListener {
            updateTabUI(binding.tabAll, tabs)
            currentLeagueId = null
            applyFilters()
        }
        binding.tabEpl.setOnClickListener {
            updateTabUI(binding.tabEpl, tabs)
            currentLeagueId = 39
            applyFilters()
        }
        binding.tabLaLiga.setOnClickListener {
            updateTabUI(binding.tabLaLiga, tabs)
            currentLeagueId = 140
            applyFilters()
        }
        binding.tabSerieA.setOnClickListener {
            updateTabUI(binding.tabSerieA, tabs)
            currentLeagueId = 135
            applyFilters()
        }
        binding.tabBundesliga.setOnClickListener {
            updateTabUI(binding.tabBundesliga, tabs)
            currentLeagueId = 78
            applyFilters()
        }
        binding.tabLigue1.setOnClickListener {
            updateTabUI(binding.tabLigue1, tabs)
            currentLeagueId = 61
            applyFilters()
        }
    }

    private fun updateTabUI(selectedTab: TextView, allTabs: List<TextView>) {
        allTabs.forEach { it.setTextColor(Color.parseColor("#888888")) }
        selectedTab.setTextColor(Color.parseColor("#20E07A"))
    }

    private fun setupCalendar() {
        binding.btnCalendar.setOnClickListener {
            val currentYear = selectedDate.year
            val currentM = selectedDate.monthValue - 1
            val currentD = selectedDate.dayOfMonth

            DatePickerDialog(this, { _, year, month, day ->
                selectedDate = LocalDate.of(year, month + 1, day)

                val diffDays = ChronoUnit.DAYS.between(LocalDate.now(), selectedDate).toInt()
                val targetPosition = dateAdapter.centerPosition + diffDays

                moveDateListToPosition(targetPosition)

                dateAdapter.setSelectedPosition(targetPosition)
                updateCalendarButtonText()
                applyFilters()

            }, currentYear, currentM, currentD).show()
        }
    }

    private fun moveDateListToPosition(targetPosition: Int) {
        val layoutManager = binding.rvDateList.layoutManager as LinearLayoutManager
        val jumpDistance = abs(targetPosition - currentPosition)

        // 🌟 15일 이상은 애니메이션 없이 즉시 화면 전환(순간이동)
        if (jumpDistance >= 15) {
            val offset = if (binding.rvDateList.width > 0) {
                (binding.rvDateList.width / 2) - 100
            } else {
                300
            }
            layoutManager.scrollToPositionWithOffset(targetPosition, offset)
        } else {
            // 가까운 날짜는 스르륵 스크롤
            val centerSmoothScroller = object : LinearSmoothScroller(this) {
                override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                    return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
                }
            }
            centerSmoothScroller.targetPosition = targetPosition
            layoutManager.startSmoothScroll(centerSmoothScroller)
        }
        currentPosition = targetPosition
    }

    private fun applyFilters() {
        val month = selectedDate.monthValue
        val targetSeason = if (month <= 7) selectedDate.year - 1 else selectedDate.year
        val targetDateStr = selectedDate.format(DateTimeFormatter.ofPattern("MM-dd"))

        // 🌟 [진단 로그 1] 앱이 필터링하려고 하는 기준값 콘솔 출력
        Log.d("SOCCER_DIAGNOSIS", "=======================================")
        Log.d("SOCCER_DIAGNOSIS", "🟢 현재 앱 선택 날짜 문자열: $targetDateStr")
        Log.d("SOCCER_DIAGNOSIS", "🟢 현재 앱 계산 타겟 시즌: $targetSeason")
        Log.d("SOCCER_DIAGNOSIS", "🟢 서버에서 들고 있는 총 경기 수: ${allMatches.size}")

        val filteredList = allMatches.filter { match ->
            val dbDate = match.matchDate ?: ""
            val isDateMatch = dbDate.contains(targetDateStr)
            val isSeasonMatch = match.season == targetSeason
            val isLeagueMatch = if (currentLeagueId == null) true else (match.leagueId == currentLeagueId)

            // 🌟 [진단 로그 2] 데이터가 왜 걸러지는지 상위 2개만 샘플로 매칭 상태 출력
            if (allMatches.indexOf(match) < 2) {
                Log.d("SOCCER_DIAGNOSIS", "👉 샘플 매칭 확인 -> DB날짜: '$dbDate' (일치: $isDateMatch) | DB시즌: ${match.season} (일치: $isSeasonMatch)")
            }

            isDateMatch && isSeasonMatch && isLeagueMatch
        }

        Log.d("SOCCER_DIAGNOSIS", "🟢 필터링 통과해서 화면에 그려질 경기 수: ${filteredList.size}")
        Log.d("SOCCER_DIAGNOSIS", "=======================================")

        matchAdapter.updateData(filteredList)

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "조건에 맞는 경기가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadDataFromServer() {
        RetrofitClient.apiService.getMatches().enqueue(object : Callback<List<MatchData>> {
            override fun onResponse(call: Call<List<MatchData>>, response: Response<List<MatchData>>) {
                Log.d("SOCCER_DIAGNOSIS", "📡 서버 응답 성공 여부: ${response.isSuccessful}, 코드: ${response.code()}")
                if (response.isSuccessful) {
                    response.body()?.let { matches ->
                        Log.d("SOCCER_DIAGNOSIS", "📡 서버가 실제로 던져준 순수 데이터 개수: ${matches.size}")
                        if (matches.isNotEmpty()) {
                            Log.d("SOCCER_DIAGNOSIS", "📡 첫번째 경기 데이터 통짜 샘플: ${matches[0]}")
                        }

                        allMatches.clear()
                        val uniqueMatches = matches.distinctBy {
                            "${it.homeTeam}_${it.awayTeam}_${it.matchDate}_${it.season}"
                        }
                        allMatches.addAll(uniqueMatches)
                        applyFilters()
                    }
                }
            }

            override fun onFailure(call: Call<List<MatchData>>, t: Throwable) {
                Log.e("SOCCER_DIAGNOSIS", "❌ 서버 통신 자체 실패 (네트워크/IP 끊김)", t)
            }
        })
    }
}