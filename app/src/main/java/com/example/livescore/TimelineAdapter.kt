package com.example.livescore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 🌟 홈/원정을 구분하여 타임라인을 그려주는 어댑터입니다.
class TimelineAdapter(
    private val events: List<MatchEventData>,
    private val homeTeamId: Int
) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    class TimelineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEventTime: TextView = view.findViewById(R.id.tvEventTime)

        val layoutHomeEvent: LinearLayout = view.findViewById(R.id.layoutHomeEvent)
        val tvHomePlayer: TextView = view.findViewById(R.id.tvHomePlayer)
        val tvHomeAssist: TextView = view.findViewById(R.id.tvHomeAssist)
        val tvHomeIcon: TextView = view.findViewById(R.id.tvHomeIcon)

        val layoutAwayEvent: LinearLayout = view.findViewById(R.id.layoutAwayEvent)
        val tvAwayPlayer: TextView = view.findViewById(R.id.tvAwayPlayer)
        val tvAwayAssist: TextView = view.findViewById(R.id.tvAwayAssist)
        val tvAwayIcon: TextView = view.findViewById(R.id.tvAwayIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        // R.layout.item_timeline 으로 수정하여 빨간 줄(에러)을 완벽히 해결합니다.
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timeline, parent, false)
        return TimelineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val event = events[position]

        // 1. 시간 표시 (추가 시간이 있으면 + 기호 포함)
        val timeStr = if (event.extraTime != null) "${event.time}+${event.extraTime}'" else "${event.time}'"
        holder.tvEventTime.text = timeStr

        // 2. 이벤트 아이콘 파싱 로직
        val icon = when (event.type) {
            "Goal" -> "⚽"
            "Card" -> if (event.detail?.contains("Yellow", ignoreCase = true) == true) "🟨" else "🟥"
            "subst" -> "🔄"
            else -> "📌"
        }

        // 3. 서브 텍스트 (어시스트 혹은 교체 아웃 선수 표기)
        val subText = when (event.type) {
            "Goal" -> if (event.assistPlayerName != null && event.assistPlayerName != "null") "어시스트: ${event.assistPlayerName}" else null
            "subst" -> if (event.assistPlayerName != null && event.assistPlayerName != "null") "Out: ${event.assistPlayerName}" else null
            else -> null
        }

        // 4. 홈/원정 구분하여 레이아웃 표시
        if (event.teamId == homeTeamId) {
            // 홈팀 이벤트인 경우: 왼쪽 표시, 오른쪽 숨김
            holder.layoutHomeEvent.visibility = View.VISIBLE
            holder.layoutAwayEvent.visibility = View.INVISIBLE

            holder.tvHomePlayer.text = event.playerName ?: "알 수 없는 선수"
            holder.tvHomeIcon.text = icon

            if (subText != null) {
                holder.tvHomeAssist.visibility = View.VISIBLE
                holder.tvHomeAssist.text = subText
            } else {
                holder.tvHomeAssist.visibility = View.GONE
            }
        } else {
            // 원정팀 이벤트인 경우: 오른쪽 표시, 왼쪽 숨김
            holder.layoutHomeEvent.visibility = View.INVISIBLE
            holder.layoutAwayEvent.visibility = View.VISIBLE

            holder.tvAwayPlayer.text = event.playerName ?: "알 수 없는 선수"
            holder.tvAwayIcon.text = icon

            if (subText != null) {
                holder.tvAwayAssist.visibility = View.VISIBLE
                holder.tvAwayAssist.text = subText
            } else {
                holder.tvAwayAssist.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = events.size
}