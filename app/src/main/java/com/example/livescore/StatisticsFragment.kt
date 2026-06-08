package com.example.livescore

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatisticsFragment : Fragment() {
    private lateinit var containerStats: LinearLayout
    private lateinit var tvPossessionHome: TextView
    private lateinit var tvPossessionAway: TextView
    private lateinit var viewPossessionHome: View
    private lateinit var viewPossessionAway: View

    // 🌟 랭킹 뷰 참조 추가
    private lateinit var layoutTopPlayers: LinearLayout
    private lateinit var containerTopHome: LinearLayout
    private lateinit var containerTopAway: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)
        containerStats = view.findViewById(R.id.containerStats)
        tvPossessionHome = view.findViewById(R.id.tvPossessionHome)
        tvPossessionAway = view.findViewById(R.id.tvPossessionAway)
        viewPossessionHome = view.findViewById(R.id.viewPossessionHome)
        viewPossessionAway = view.findViewById(R.id.viewPossessionAway)

        layoutTopPlayers = view.findViewById(R.id.layoutTopPlayers)
        containerTopHome = view.findViewById(R.id.containerTopHome)
        containerTopAway = view.findViewById(R.id.containerTopAway)

        val fixtureId = arguments?.getLong("fixtureId") ?: -1L
        val homeTeamName = arguments?.getString("homeTeamName") ?: ""
        val awayTeamName = arguments?.getString("awayTeamName") ?: ""

        viewPossessionHome.setBackgroundColor(Color.parseColor(getTeamColor(homeTeamName, true)))
        viewPossessionAway.setBackgroundColor(Color.parseColor(getTeamColor(awayTeamName, false)))

        if (fixtureId != -1L) {
            loadStats(fixtureId)
        }
        return view
    }

    private fun getTeamColor(teamName: String, isHome: Boolean): String {
        val name = teamName.lowercase()
        return when {
            name.contains("liverpool") -> "#E31B23"
            name.contains("chelsea") -> "#034694"
            name.contains("city") || name.contains("manchester city") -> "#6CABDD"
            name.contains("united") || name.contains("manchester utd") -> "#DA291C"
            name.contains("arsenal") -> "#EF0107"
            name.contains("tottenham") || name.contains("spurs") -> "#132257"
            name.contains("real madrid") -> "#FFFFFF"
            name.contains("barcelona") -> "#004D98"
            name.contains("bayern") || name.contains("munich") -> "#DC052D"
            name.contains("paris") || name.contains("psg") -> "#004170"
            else -> if (isHome) "#20E07A" else "#FF4D4D"
        }
    }

    private fun loadStats(fixtureId: Long) {
        RetrofitClient.apiService.getMatchInfo(fixtureId).enqueue(object : Callback<MatchInfoData> {
            override fun onResponse(call: Call<MatchInfoData>, response: Response<MatchInfoData>) {
                if (response.isSuccessful) {
                    val matchInfo = response.body() ?: return
                    renderStats(matchInfo.statistics)

                    // 🌟 TOP 5 렌더링 호출
                    if (matchInfo.homeTopPlayers.isNotEmpty() || matchInfo.awayTopPlayers.isNotEmpty()) {
                        renderTopPlayers(matchInfo.homeTopPlayers, matchInfo.awayTopPlayers)
                    }
                }
            }
            override fun onFailure(call: Call<MatchInfoData>, t: Throwable) {}
        })
    }

    private fun renderStats(stats: List<MatchStatItemData>) {
        containerStats.removeAllViews()

        val possessionStat = stats.find { it.name == "점유율" }
        if (possessionStat != null) {
            val homePos = possessionStat.homeValue.replace("%", "").toIntOrNull() ?: 50
            val awayPos = possessionStat.awayValue.replace("%", "").toIntOrNull() ?: 50

            tvPossessionHome.text = "$homePos%"
            tvPossessionAway.text = "$awayPos%"

            val layoutParamsHome = viewPossessionHome.layoutParams as LinearLayout.LayoutParams
            layoutParamsHome.weight = homePos.toFloat()
            viewPossessionHome.layoutParams = layoutParamsHome

            val layoutParamsAway = viewPossessionAway.layoutParams as LinearLayout.LayoutParams
            layoutParamsAway.weight = awayPos.toFloat()
            viewPossessionAway.layoutParams = layoutParamsAway
        }

        var currentCategory = ""
        for (stat in stats) {
            if (stat.name == "점유율") continue

            if (stat.category != currentCategory) {
                currentCategory = stat.category
                val headerView = layoutInflater.inflate(R.layout.item_stat_header, containerStats, false) as TextView
                headerView.text = when(currentCategory) {
                    "OVERALL" -> "공격 및 전체"
                    "PASSES" -> "패스 세부 지표"
                    "DEFENSE" -> "수비 세부 지표"
                    "DUELS" -> "볼 경합 및 드리블" // 🌟 볼 경합 카테고리 추가
                    else -> currentCategory
                }
                containerStats.addView(headerView)
            }

            val rowView = layoutInflater.inflate(R.layout.item_stat_row, containerStats, false)
            val tvHome = rowView.findViewById<TextView>(R.id.tvHomeStat)
            val tvTitle = rowView.findViewById<TextView>(R.id.tvStatTitle)
            val tvAway = rowView.findViewById<TextView>(R.id.tvAwayStat)

            tvHome.text = stat.homeValue
            tvTitle.text = stat.name
            tvAway.text = stat.awayValue

            containerStats.addView(rowView)
        }
    }

    // 🌟 TOP 5 선수를 좌우에 차례대로 그려주는 로직
    private fun renderTopPlayers(homePlayers: List<MomPlayerData>, awayPlayers: List<MomPlayerData>) {
        layoutTopPlayers.visibility = View.VISIBLE
        containerTopHome.removeAllViews()
        containerTopAway.removeAllViews()

        homePlayers.forEach { player ->
            val rowView = layoutInflater.inflate(R.layout.item_top_player_row, containerTopHome, false)
            rowView.findViewById<TextView>(R.id.tvTopPlayerName).text = player.name
            rowView.findViewById<TextView>(R.id.tvTopPlayerRating).text = String.format("%.1f", player.rating)

            val ivPhoto = rowView.findViewById<ImageView>(R.id.ivTopPlayerPhoto)
            Glide.with(this).load(player.photoUrl).transform(CircleCrop()).into(ivPhoto)

            containerTopHome.addView(rowView)
        }

        awayPlayers.forEach { player ->
            val rowView = layoutInflater.inflate(R.layout.item_top_player_row, containerTopAway, false)
            rowView.findViewById<TextView>(R.id.tvTopPlayerName).text = player.name
            rowView.findViewById<TextView>(R.id.tvTopPlayerRating).text = String.format("%.1f", player.rating)

            val ivPhoto = rowView.findViewById<ImageView>(R.id.ivTopPlayerPhoto)
            Glide.with(this).load(player.photoUrl).transform(CircleCrop()).into(ivPhoto)

            containerTopAway.addView(rowView)
        }
    }

    companion object {
        fun newInstance(fixtureId: Long, homeTeamName: String, awayTeamName: String) = StatisticsFragment().apply {
            arguments = Bundle().apply {
                putLong("fixtureId", fixtureId)
                putString("homeTeamName", homeTeamName)
                putString("awayTeamName", awayTeamName)
            }
        }
    }
}