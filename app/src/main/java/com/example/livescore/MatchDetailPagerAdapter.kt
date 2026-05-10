package com.example.livescore

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MatchDetailPagerAdapter(
    fa: FragmentActivity,
    private val fixtureId: Long,
    private val leagueId: Int,
    private val season: Int,
    private val homeTeamId: Int,
    private val awayTeamId: Int,
    private val stadium: String?,
    private val round: String?
) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> PreviewFragment.newInstance(stadium, round)
            1 -> LineupFragment.newInstance(fixtureId)
            2 -> StandingsFragment.newInstance(leagueId, season, homeTeamId, awayTeamId)
            else -> H2HFragment.newInstance(homeTeamId, awayTeamId)
        }
    }
}