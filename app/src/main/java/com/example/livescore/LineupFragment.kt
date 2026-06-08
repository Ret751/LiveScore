package com.example.livescore

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
                    }
                } else {
                    Log.e("LineupFragment", "서버 응답 실패: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<LineupData>>, t: Throwable) {}
        })
    }

    private fun renderFullMatchLineup(homeLineup: LineupData, awayLineup: LineupData) {
        binding.tvTargetTeamName.text = "${homeLineup.teamName} vs ${awayLineup.teamName}"
        binding.tvFormation.text = "포메이션: ${homeLineup.formation ?: "-"} (홈) / ${awayLineup.formation ?: "-"} (원정)"

        binding.pitchContainer.removeAllViews()
        drawPitchLineup(homeLineup, isHome = true)
        drawPitchLineup(awayLineup, isHome = false)

        binding.tvHomeCoach.text = homeLineup.coachName ?: "감독 미정"
        Glide.with(this).load(homeLineup.coachPhotoUrl).transform(CircleCrop()).placeholder(R.mipmap.ic_launcher).into(binding.ivHomeCoachPhoto)

        binding.tvAwayCoach.text = awayLineup.coachName ?: "감독 미정"
        Glide.with(this).load(awayLineup.coachPhotoUrl).transform(CircleCrop()).placeholder(R.mipmap.ic_launcher).into(binding.ivAwayCoachPhoto)

        val subContainer = binding.substitutesContainer
        subContainer.removeAllViews()

        val homeSubs = homeLineup.substitutes
        val awaySubs = awayLineup.substitutes
        val maxSubCount = max(homeSubs.size, awaySubs.size)

        for (i in 0 until maxSubCount) {
            val rowView = layoutInflater.inflate(R.layout.item_substitute_row, subContainer, false)

            val layoutHome = rowView.findViewById<View>(R.id.layoutHomeSub)
            val ivHomePhoto = rowView.findViewById<ImageView>(R.id.ivHomeSubPhoto)
            val tvHomeNumber = rowView.findViewById<TextView>(R.id.tvHomeSubNumber)
            val tvHomePlayer = rowView.findViewById<TextView>(R.id.tvHomeSubPlayer)

            val layoutAway = rowView.findViewById<View>(R.id.layoutAwaySub)
            val ivAwayPhoto = rowView.findViewById<ImageView>(R.id.ivAwaySubPhoto)
            val tvAwayNumber = rowView.findViewById<TextView>(R.id.tvAwaySubNumber)
            val tvAwayPlayer = rowView.findViewById<TextView>(R.id.tvAwaySubPlayer)

            if (i < homeSubs.size) {
                val p = homeSubs[i]
                layoutHome.visibility = View.VISIBLE
                tvHomeNumber.text = (p.number ?: "").toString()
                tvHomePlayer.text = p.name
                p.id?.let { Glide.with(this).load("https://media.api-sports.io/football/players/$it.png").transform(CircleCrop()).into(ivHomePhoto) }
            }

            if (i < awaySubs.size) {
                val p = awaySubs[i]
                layoutAway.visibility = View.VISIBLE
                tvAwayNumber.text = (p.number ?: "").toString()
                tvAwayPlayer.text = p.name
                p.id?.let { Glide.with(this).load("https://media.api-sports.io/football/players/$it.png").transform(CircleCrop()).into(ivAwayPhoto) }
            }

            subContainer.addView(rowView)
        }
    }

    private fun drawPitchLineup(lineup: LineupData, isHome: Boolean) {
        val pitch = binding.pitchContainer

        val rowCountMap = mutableMapOf<Int, Int>()
        lineup.startXI.forEach { p ->
            val row = p.grid?.substringBefore(":")?.toIntOrNull() ?: return@forEach
            rowCountMap[row] = (rowCountMap[row] ?: 0) + 1
        }
        val totalRows = rowCountMap.keys.maxOrNull() ?: 4

        lineup.startXI.forEach { player ->
            val grid = player.grid ?: return@forEach
            val parts = grid.split(":")
            val row = parts.getOrNull(0)?.toFloatOrNull() ?: return@forEach
            val col = parts.getOrNull(1)?.toFloatOrNull() ?: return@forEach

            val playerView = layoutInflater.inflate(R.layout.item_player_pitch, pitch, false)
            playerView.id = View.generateViewId()

            val ivPhoto = playerView.findViewById<ImageView>(R.id.ivPlayerPhoto)
            val tvName = playerView.findViewById<TextView>(R.id.tvPlayerName)

            val numStr = player.number?.toString() ?: "-"
            tvName.text = "$numStr - ${player.name}"

            player.id?.let { playerId ->
                Glide.with(this)
                    .load("https://media.api-sports.io/football/players/$playerId.png")
                    .transform(CircleCrop())
                    .into(ivPhoto)
            }

            pitch.addView(playerView)

            val vBias = if (isHome) {
                0.02f + (row - 1f) * (0.42f / max(1f, totalRows - 1f))
            } else {
                1f - (row - 1f) * (0.42f / max(1f, totalRows - 1f))
            }

            val maxColInThisRow = rowCountMap[row.toInt()]?.toFloat() ?: 1f

            // 🌟 [수정된 부분 1] 무조건 정중앙(0.5f)을 기준으로 설정한 간격(gap)만큼 일정하게 벌립니다.
            val gap = 0.25f // 선수들 간의 고정 간격 (화면 너비의 18%)
            val startBias = 0.5f - ((maxColInThisRow - 1f) * gap / 2f)

            // 🌟 [수정된 부분 2] 좌우가 뒤바뀐 문제를 해결하기 위해 (maxColInThisRow - col)로 역배치 적용!
            val baseHBias = startBias + ((maxColInThisRow - col) * gap)

            val hBias = if (isHome) baseHBias else (1f - baseHBias)

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

    companion object {
        fun newInstance(fixtureId: Long) = LineupFragment().apply {
            arguments = Bundle().apply { putLong("fixtureId", fixtureId) }
        }
    }
}