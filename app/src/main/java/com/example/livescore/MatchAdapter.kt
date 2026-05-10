package com.example.livescore // 본인 프로젝트 패키지명에 맞게 꼭 확인하세요!

import android.content.Intent // Intent 사용을 위해 추가됨
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MatchAdapter(private var currentList: List<MatchData>) : RecyclerView.Adapter<MatchAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvLeague: TextView = v.findViewById(R.id.tvLeague)
        val tvTime: TextView = v.findViewById(R.id.tvTime)

        // 정중앙 배치를 위해 5개로 쪼개진 뷰 연결
        val tvHomeTeam: TextView = v.findViewById(R.id.tvHomeTeam)
        val ivHomeLogo: ImageView = v.findViewById(R.id.ivHomeLogo)
        val tvScore: TextView = v.findViewById(R.id.tvScore)
        val ivAwayLogo: ImageView = v.findViewById(R.id.ivAwayLogo)
        val tvAwayTeam: TextView = v.findViewById(R.id.tvAwayTeam)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val m = currentList[position]

        // 1. 리그 ID를 실제 리그 이름으로 변환
        val leagueName = when(m.leagueId) {
            39 -> "Premier League"
            140 -> "La Liga"
            135 -> "Serie A"
            78 -> "Bundesliga"
            61 -> "Ligue 1"
            else -> "유럽 리그"
        }
        holder.tvLeague.text = leagueName

        // 2. 시간, 팀 이름, 점수 텍스트 세팅
        holder.tvTime.text = m.matchTime ?: "시간 미정"
        holder.tvHomeTeam.text = m.homeTeam ?: "Unknown"
        holder.tvScore.text = m.score ?: "vs"
        holder.tvAwayTeam.text = m.awayTeam ?: "Unknown"

        // =========================================================
        // 3. 로고 이미지 동적 연결 (Glide 사용 - URL에서 직접 다운)
        // =========================================================

        val homeId = m.homeTeamId ?: 0
        val awayId = m.awayTeamId ?: 0

        val homeLogoUrl = "https://media.api-sports.io/football/teams/$homeId.png"
        val awayLogoUrl = "https://media.api-sports.io/football/teams/$awayId.png"

        // 홈팀 로고 띄우기
        Glide.with(holder.itemView.context)
            .load(homeLogoUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .into(holder.ivHomeLogo)

        // 어웨이팀 로고 띄우기
        Glide.with(holder.itemView.context)
            .load(awayLogoUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .into(holder.ivAwayLogo)

        // =========================================================
        // 4. 클릭 시 상세 화면으로 이동하는 이벤트
        // =========================================================
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            val intent = Intent(context, MatchDetailActivity::class.java).apply {
                putExtra("fixtureId", m.fixtureId)
                putExtra("homeTeam", m.homeTeam)
                putExtra("awayTeam", m.awayTeam)
                putExtra("homeTeamId", m.homeTeamId)
                putExtra("awayTeamId", m.awayTeamId)
                putExtra("matchTime", m.matchTime)
                putExtra("matchDate", m.matchDate)
                putExtra("leagueId", m.leagueId)
                putExtra("season", m.season)
                putExtra("stadium", m.stadium)
                putExtra("matchRound", m.matchRound)
            }
            context.startActivity(intent)
        }
    }

    fun updateData(newList: List<MatchData>) {
        currentList = newList
        notifyDataSetChanged()
    }
}