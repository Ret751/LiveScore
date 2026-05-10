package com.example.livescore

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StandingsAdapter(private val list: List<StandingData>, private val currentHomeTeamId: Int, private val currentAwayTeamId: Int) : RecyclerView.Adapter<StandingsAdapter.VH>() {
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvRank: TextView = v.findViewById(R.id.tvRank)
        val tvTeam: TextView = v.findViewById(R.id.tvTeam)
        val tvPlayed: TextView = v.findViewById(R.id.tvPlayed)
        val tvPoints: TextView = v.findViewById(R.id.tvPoints)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        // 아이템 레이아웃은 TableRow 형식을 사용합니다.
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_standing, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = list[position]
        holder.tvRank.text = s.rank.toString()
        holder.tvTeam.text = s.teamName
        holder.tvPlayed.text = s.played.toString()
        holder.tvPoints.text = s.points.toString()

        // 🌟 현재 경기의 두 팀은 네온 그린 색상으로 하이라이트 처리
        if (s.teamId == currentHomeTeamId || s.teamId == currentAwayTeamId) {
            holder.tvTeam.setTextColor(Color.parseColor("#20E07A"))
        } else {
            holder.tvTeam.setTextColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int = list.size
}