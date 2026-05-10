package com.example.livescore // 본인 패키지명 확인

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.example.livescore.databinding.FragmentLineupBinding
import retrofit2.*

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
                    if (lineups.isNotEmpty()) {
                        // 홈팀 라인업 기준으로 먼저 구현
                        drawLineup(lineups[0])
                    }
                }
            }
            override fun onFailure(call: Call<List<LineupData>>, t: Throwable) {
                Log.e("DEBUG", "라인업 로드 실패: ${t.message}")
            }
        })
    }

    // 🌟 축구장에 선수를 그리는 핵심 함수
    private fun drawLineup(data: LineupData) {
        context ?: return
        val pitch = binding.pitchContainer
        pitch.removeAllViews() // 기존 선수 제거

        // 1. 포메이션 & 팀 이름 업데이트
        binding.tvFormation.text = "포메이션: ${data.formation}"
        binding.tvTargetTeamName.text = data.teamName

        // 2. 선발 선수 배치 (startXI)
        for (player in data.startXI) {
            val grid = player.grid ?: continue // 좌표 데이터 없으면 패스

            // 아이템 인플레이트
            val playerView = LayoutInflater.from(context).inflate(R.layout.item_player_pitch, pitch, false)
            playerView.id = View.generateViewId() // 동적 ID 부여

            // 데이터 세팅
            playerView.findViewById<TextView>(R.id.tvPlayerNumber).text = player.number?.toString() ?: ""
            playerView.findViewById<TextView>(R.id.tvPlayerName).text = player.name

            // 뷰 추가
            pitch.addView(playerView)

            // 3. 🌟 ConstraintSet을 이용한 좌표 배치 (핵심!)
            val coords = grid.split(":") // "2:3" -> ["2", "3"]
            val row = coords[0].toFloat() // 수직 (골대 -> 공격)
            val col = coords[1].toFloat() // 수전 (왼쪽 -> 오른쪽)

            // API 좌표계를 화면 좌표(Bias)로 변환 (0.0 ~ 1.0)
            // 수직(Row): 1은 골키퍼, 높을수록 공격수 (보통 5까지)
            val vBias = when(row) {
                1f -> 0.92f // 골키퍼 (맨 아래)
                2f -> 0.70f // 수비수
                3f -> 0.45f // 미드필더
                4f -> 0.20f // 공격수
                5f -> 0.08f // 최전방
                else -> 0.5f
            }

            // 수평(Col): 총 개수에 따라 다름 (예: 수비 4명이면 1,2,3,4)
            // 해당 로우에 선수가 몇 명인지 파악해서 비율로 배치하는 것이 정확하나,
            // 간단하게 4분할/3분할 비율로 기본 구현
            val maxCol = data.formation?.split("-")?.let { it.getOrNull(row.toInt()-2)?.toFloat() } ?: 4f // 임시
            val hBias = (col / (maxCol + 1))

            val set = ConstraintSet()
            set.clone(pitch)
            // 중앙(부모)에 정렬하고 Bias로 위치 조절
            set.connect(playerView.id, ConstraintSet.TOP, pitch.id, ConstraintSet.TOP)
            set.connect(playerView.id, ConstraintSet.BOTTOM, pitch.id, ConstraintSet.BOTTOM)
            set.connect(playerView.id, ConstraintSet.START, pitch.id, ConstraintSet.START)
            set.connect(playerView.id, ConstraintSet.END, pitch.id, ConstraintSet.END)

            set.setHorizontalBias(playerView.id, hBias)
            set.setVerticalBias(playerView.id, vBias)
            set.applyTo(pitch)
        }

        // 4. 교체 명단은 기존 RecyclerView 어댑터 구현 방법과 동일 (생략)
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