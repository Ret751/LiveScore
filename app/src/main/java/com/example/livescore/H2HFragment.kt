package com.example.livescore

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.livescore.databinding.FragmentH2hBinding
import retrofit2.*

class H2HFragment : Fragment() {
    private var _binding: FragmentH2hBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(homeId: Int, awayId: Int) = H2HFragment().apply {
            arguments = Bundle().apply {
                putInt("homeId", homeId)
                putInt("awayId", awayId)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentH2hBinding.inflate(inflater, container, false)

        val homeId = arguments?.getInt("homeId") ?: 0
        val awayId = arguments?.getInt("awayId") ?: 0

        if (homeId != 0 && awayId != 0) {
            loadH2H(homeId, awayId)
        }

        return binding.root
    }

    private fun loadH2H(homeId: Int, awayId: Int) {
        RetrofitClient.apiService.getH2H(homeId, awayId).enqueue(object : Callback<H2HSummaryData> {
            override fun onResponse(call: Call<H2HSummaryData>, response: Response<H2HSummaryData>) {
                if (response.isSuccessful) {
                    val data = response.body() ?: return
                    // 데이터 반영
                    binding.tvHomeWins.text = data.homeWins.toString()
                    binding.tvDraws.text = data.draws.toString()
                    binding.tvAwayWins.text = data.awayWins.toString()

                    // 최근 5경기 리스트 세팅
                    binding.rvH2HMatches.layoutManager = LinearLayoutManager(context)
                    binding.rvH2HMatches.adapter = MatchAdapter(data.lastMatches)
                }
            }
            override fun onFailure(call: Call<H2HSummaryData>, t: Throwable) {
                Log.e("DEBUG", "H2H 로드 실패: ${t.message}")
            }
        })
    }
}