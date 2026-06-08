package com.example.livescore

import com.google.gson.annotations.SerializedName

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
    @SerializedName("stadium") val stadium: String?,
    @SerializedName("referee") val referee: String?
)

data class LineupData(
    val teamName: String,
    val formation: String?,
    val coachName: String?,
    val coachPhotoUrl: String?,
    val startXI: List<PlayerData>,
    val substitutes: List<PlayerData>
)

data class PlayerData(
    val id: Long?,
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

data class MatchInfoData(
    val fixtureId: Long,
    val momPlayer: MomPlayerData?,
    val referee: String?,
    val matchFullDate: String?,
    val events: List<MatchEventData> = emptyList(),
    val statistics: List<MatchStatItemData> = emptyList(),
    val homeTopPlayers: List<MomPlayerData> = emptyList(), // 🌟 홈팀 평점 TOP 5
    val awayTopPlayers: List<MomPlayerData> = emptyList()  // 🌟 원정팀 평점 TOP 5
)

data class MomPlayerData(
    val playerId: Long,
    val name: String,
    val teamName: String,
    val photoUrl: String?,
    val rating: Double
)

data class MatchEventData(
    val time: Int,
    val extraTime: Int?,
    val teamId: Int,
    val teamName: String?,
    val type: String,
    val detail: String?,
    val playerName: String?,
    val assistPlayerName: String?
)

data class MatchStatItemData(
    val category: String,
    val name: String,
    val homeValue: String,
    val awayValue: String
)