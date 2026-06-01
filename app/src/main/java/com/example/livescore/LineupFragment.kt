package com.example.livescore

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.livescore.databinding.FragmentLineupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.max

class LineupFragment : Fragment() {
    private var _binding: FragmentLineupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLineupBinding.inflate(inflater, container, false)

        val fixtureId = arguments?.getLong("fixtureId") ?: -1L
        if (fixtureId != -1L) {
            loadLineup(fixtureId)
        }

        return binding.root
    }

    private fun loadLineup(fixtureId: Long) {
        RetrofitClient.apiService.getLineups(fixtureId).enqueue(object : Callback<List<LineupData>> {
            override fun onResponse(call: Call<List<LineupData>>, response: Response<List<LineupData>>) {
                if (response.isSuccessful) {
                    val lineups = response.body() ?: return
                    if (lineups.size >= 2) {
                        renderFullMatchLineup(lineups[0], lineups[1])
                    } else if (lineups.isNotEmpty()) {
                        binding.tvHomeCoach.text = lineups[0].coachName ?: "감독 정보 없음"
                        drawPitchLineup(lineups[0])
                    }
                } else {
                    Log.e("LineupFragment", "서버 응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<LineupData>>, t: Throwable) {
                Log.e("LineupFragment", "통신 실패: ${t.message}")
            }
        })
    }

    private fun renderFullMatchLineup(homeLineup: LineupData, awayLineup: LineupData) {
        binding.tvTargetTeamName.text = "${homeLineup.teamName} vs ${awayLineup.teamName}"
        binding.tvFormation.text = "포메이션: ${homeLineup.formation ?: "-"} (홈) / ${awayLineup.formation ?: "-"} (원정)"

        // 그라운드 선발 라인업 드로우
        drawPitchLineup(homeLineup)

        // 감독 데이터 세팅
        binding.tvHomeCoach.text = homeLineup.coachName ?: "감독 미정"
        binding.tvAwayCoach.text = awayLineup.coachName ?: "감독 미정"

        // 교체 선수 레이아웃 빌드
        val subContainer = binding.substitutesContainer
        subContainer.removeAllViews()

        val homeSubs = homeLineup.substitutes
        val awaySubs = awayLineup.substitutes
        val maxSubCount = max(homeSubs.size, awaySubs.size)

        for (i in 0 until maxSubCount) {
            val rowView = layoutInflater.inflate(R.layout.item_substitute_row, subContainer, false)

            // 홈팀 레이아웃 및 뷰 참조
            val layoutHome = rowView.findViewById<View>(R.id.layoutHomeSub)
            val ivHomePhoto = rowView.findViewById<ImageView>(R.id.ivHomeSubPhoto)
            val tvHomeNumber = rowView.findViewById<TextView>(R.id.tvHomeSubNumber)
            val tvHomePlayer = rowView.findViewById<TextView>(R.id.tvHomeSubPlayer)

            // 원정팀 레이아웃 및 뷰 참조
            val layoutAway = rowView.findViewById<View>(R.id.layoutAwaySub)
            val ivAwayPhoto = rowView.findViewById<ImageView>(R.id.ivAwaySubPhoto)
            val tvAwayNumber = rowView.findViewById<TextView>(R.id.tvAwaySubNumber)
            val tvAwayPlayer = rowView.findViewById<TextView>(R.id.tvAwaySubPlayer)

            // 홈팀 교체선수 데이터 바인딩
            if (i < homeSubs.size) {
                val p = homeSubs[i]
                layoutHome.visibility = View.VISIBLE
                tvHomeNumber.text = (p.number ?: "").toString()
                tvHomePlayer.text = p.name

                p.id?.let { playerId ->
                    Glide.with(this)
                        .load("https://media.api-sports.io/football/players/$playerId.png")
                        .transform(CircleCrop())
                        .into(ivHomePhoto)
                }
            }

            // 원정팀 교체선수 데이터 바인딩
            if (i < awaySubs.size) {
                val p = awaySubs[i]
                layoutAway.visibility = View.VISIBLE
                tvAwayNumber.text = (p.number ?: "").toString()
                tvAwayPlayer.text = p.name

                p.id?.let { playerId ->
                    Glide.with(this)
                        .load("https://media.api-sports.io/football/players/$playerId.png")
                        .transform(CircleCrop())
                        .into(ivAwayPhoto)
                }
            }

            subContainer.addView(rowView)
        }
    }

    private fun drawPitchLineup(lineup: LineupData) {
        val pitch = binding.pitchContainer
        pitch.removeAllViews()

        val formationStr = lineup.formation ?: "4-4-2"
        val formationLines = formationStr.split("-")

        val rowCountMap = mutableMapOf<Int, Int>()
        rowCountMap[1] = 1

        for (i in formationLines.indices) {
            val rowNum = i + 2
            val count = formationLines[i].toIntOrNull() ?: 4
            rowCountMap[rowNum] = count
        }

        val totalRows = formationLines.size + 1

        lineup.startXI.forEach { player ->
            val grid = player.grid ?: return@forEach
            val parts = grid.split(":")
            val row = parts.getOrNull(0)?.toFloatOrNull() ?: return@forEach
            val col = parts.getOrNull(1)?.toFloatOrNull() ?: return@forEach

            // 가로 정렬로 교정된 레이아웃 인플레이트
            val playerView = layoutInflater.inflate(R.layout.item_player_pitch, pitch, false)
            playerView.id = View.generateViewId()

            val ivPhoto = playerView.findViewById<ImageView>(R.id.ivPlayerPhoto)
            val tvResultNumber = playerView.findViewById<TextView>(R.id.tvPlayerNumber)
            val tvResultName = playerView.findViewById<TextView>(R.id.tvPlayerName)

            // 데이터 세팅
            tvResultNumber.text = (player.number ?: "").toString()
            tvResultName.text = player.name

            // 맨 위 이미지 뷰에 페이스샷 로드 (원형)
            player.id?.let { playerId ->
                Glide.with(this)
                    .load("https://media.api-sports.io/football/players/$playerId.png")
                    .transform(CircleCrop())
                    .into(ivPhoto)
            }

            pitch.addView(playerView)

            // 수직/수평 정밀 배치 알고리즘 수식 계산
            val vBias = when (row.toInt()) {
                1 -> 0.92f
                else -> {
                    val minV = 0.10f
                    val maxV = 0.75f
                    maxV - ((row - 2) * (maxV - minV) / (totalRows - 1))
                }
            }

            val maxColInThisRow = rowCountMap[row.toInt()]?.toFloat() ?: 4f
            val hBias = col / (maxColInThisRow + 1f)

            val set = ConstraintSet()
            set.clone(pitch)

            set.connect(playerView.id, ConstraintSet.TOP, pitch.id, ConstraintSet.TOP)
            set.connect(playerView.id, ConstraintSet.BOTTOM, pitch.id, ConstraintSet.BOTTOM)
            set.connect(playerView.id, ConstraintSet.START, pitch.id, ConstraintSet.START)
            set.connect(playerView.id, ConstraintSet.END, pitch.id, ConstraintSet.END)

            set.setHorizontalBias(playerView.id, hBias)
            set.setVerticalBias(playerView.id, vBias)

            set.applyTo(pitch)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}