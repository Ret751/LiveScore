package com.example.livescore

import com.google.gson.annotations.SerializedName

// 기존 MatchData에 새 필드 추가
data class MatchData(
    @SerializedName("fixtureId") val fixtureId: Long?,
    @SerializedName("homeTeam") val homeTeam: String?,
    @SerializedName("awayTeam") val awayTeam: String?,
    @SerializedName("homeTeamId") val homeTeamId: Int?,
    @SerializedName("awayTeamId") val awayTeamId: Int?,
    @SerializedName("matchDate") val matchDate: String?,
    @SerializedName("matchTime") val matchTime: String?,
    @SerializedName("score") val score: String?,
    @SerializedName("season") val season: Int?,
    @SerializedName("leagueId") val leagueId: Int?,
    @SerializedName("matchRound") val matchRound: String?,
    @SerializedName("stadium") val stadium: String?
)

// --- 상세 페이지용 DTO 추가 ---
data class LineupData(
    val teamName: String,
    val formation: String?,
    val coachName: String?,
    val startXI: List<PlayerData>,
    val substitutes: List<PlayerData>
)

data class PlayerData(
    val name: String,
    val number: Int?,
    val pos: String?,
    val grid: String?
)

data class StandingData(
    val rank: Int,
    val teamName: String,
    val teamId: Int,
    val points: Int,
    val played: Int,
    val goalsDiff: Int,
    val form: String?
)

data class H2HSummaryData(
    val homeWins: Int,
    val draws: Int,
    val awayWins: Int,
    val lastMatches: List<MatchData>
)