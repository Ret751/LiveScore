package com.example.livescore

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timeline, parent, false)
        return TimelineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val event = events[position]

        // 1. 시간 표시
        val timeStr = if (event.extraTime != null) "${event.time}+${event.extraTime}'" else "${event.time}'"
        holder.tvEventTime.text = timeStr

        // 🌟 2. 뷰 재사용(Recycling) 오류 방지: 모든 텍스트의 옆면 화살표(Drawable)와 색상을 초기 상태로 강제 리셋
        holder.tvHomePlayer.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        holder.tvHomeAssist.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        holder.tvAwayPlayer.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        holder.tvAwayAssist.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

        holder.tvHomePlayer.setTextColor(Color.WHITE)
        holder.tvHomeAssist.setTextColor(Color.parseColor("#888888"))
        holder.tvAwayPlayer.setTextColor(Color.WHITE)
        holder.tvAwayAssist.setTextColor(Color.parseColor("#888888"))

        holder.tvHomeIcon.visibility = View.VISIBLE
        holder.tvAwayIcon.visibility = View.VISIBLE

        // 3. 이벤트 분류
        val isSubst = event.type == "subst"

        val icon = when {
            isSubst -> "" // 교체일 경우 메인 이모티콘은 비워둠
            event.type == "Goal" -> "⚽"
            event.type == "Card" -> if (event.detail?.contains("Yellow", ignoreCase = true) == true) "🟨" else "🟥"
            else -> "📌"
        }

        val subText = when {
            isSubst -> event.assistPlayerName // 교체일 땐 "Out:" 글자를 빼고 순수 이름만 남김
            event.type == "Goal" -> if (event.assistPlayerName != null && event.assistPlayerName != "null") "어시스트: ${event.assistPlayerName}" else null
            else -> null
        }

        // 4. 데이터 바인딩
        if (event.teamId == homeTeamId) { // ======== [홈팀 이벤트 (왼쪽)] ========
            holder.layoutHomeEvent.visibility = View.VISIBLE
            holder.layoutAwayEvent.visibility = View.INVISIBLE

            holder.tvHomeIcon.text = icon
            holder.tvHomePlayer.text = event.playerName ?: "알 수 없는 선수"

            if (isSubst) {
                // 🌟 교체 로직: 이모티콘 자리를 지우고 텍스트뷰 오른쪽에 IN/OUT 화살표 부착
                holder.tvHomeIcon.visibility = View.GONE

                holder.tvHomePlayer.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sub_in, 0)
                holder.tvHomePlayer.compoundDrawablePadding = 12

                if (subText != null && subText != "null") {
                    holder.tvHomeAssist.visibility = View.VISIBLE
                    holder.tvHomeAssist.text = subText

                    holder.tvHomeAssist.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sub_out, 0)
                    holder.tvHomeAssist.compoundDrawablePadding = 12
                    holder.tvHomeAssist.setTextColor(Color.parseColor("#FF4D4D")) // 나가는 선수는 텍스트도 빨간빛으로 강조
                } else {
                    holder.tvHomeAssist.visibility = View.GONE
                }
            } else {
                if (subText != null && subText != "null") {
                    holder.tvHomeAssist.visibility = View.VISIBLE
                    holder.tvHomeAssist.text = subText
                } else {
                    holder.tvHomeAssist.visibility = View.GONE
                }
            }

        } else { // ======== [원정팀 이벤트 (오른쪽)] ========
            holder.layoutHomeEvent.visibility = View.INVISIBLE
            holder.layoutAwayEvent.visibility = View.VISIBLE

            holder.tvAwayIcon.text = icon
            holder.tvAwayPlayer.text = event.playerName ?: "알 수 없는 선수"

            if (isSubst) {
                // 🌟 교체 로직: 이모티콘 자리를 지우고 텍스트뷰 왼쪽에 IN/OUT 화살표 부착
                holder.tvAwayIcon.visibility = View.GONE

                holder.tvAwayPlayer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sub_in, 0, 0, 0)
                holder.tvAwayPlayer.compoundDrawablePadding = 12

                if (subText != null && subText != "null") {
                    holder.tvAwayAssist.visibility = View.VISIBLE
                    holder.tvAwayAssist.text = subText

                    holder.tvAwayAssist.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sub_out, 0, 0, 0)
                    holder.tvAwayAssist.compoundDrawablePadding = 12
                    holder.tvAwayAssist.setTextColor(Color.parseColor("#FF4D4D")) // 나가는 선수는 텍스트도 빨간빛으로 강조
                } else {
                    holder.tvAwayAssist.visibility = View.GONE
                }
            } else {
                if (subText != null && subText != "null") {
                    holder.tvAwayAssist.visibility = View.VISIBLE
                    holder.tvAwayAssist.text = subText
                } else {
                    holder.tvAwayAssist.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int = events.size
}