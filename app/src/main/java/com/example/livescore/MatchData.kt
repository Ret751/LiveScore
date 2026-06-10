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
    val win: Int = 0,
    val draw: Int = 0,
    val lose: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val goalsDiff: Int = 0,
    val form: String? = null
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

// ── 구단 경기 페이지 ──────────────────────────────────
data class TeamMatchesPageData(
    val matches:     List<MatchData>,
    val hasMore:     Boolean,
    val currentPage: Int,
    val totalPages:  Int
)

// ── 구단 개요 ──────────────────────────────────────
data class TeamOverviewData(
    val recentMatches:  List<RecentMatchData>,
    val miniStandings:  List<StandingData>,
    val venue:          VenueData?,
    val leagueTrophies: List<TrophyData>
)

data class RecentMatchData(
    val fixtureId:  Long?,
    val homeTeamId: Int?,
    val awayTeamId: Int?,
    val homeTeam:   String?,
    val awayTeam:   String?,
    val score:      String?,
    val result:     String?,   // "W" | "D" | "L"
    val matchDate:  String?
)

data class VenueData(
    val name:     String?,
    val address:  String?,
    val city:     String?,
    val capacity: Int?,
    val surface:  String?,
    val imageUrl: String?
)

data class TrophyData(
    val league:        String?,
    val country:       String?,
    val winCount:      Int,
    val lastWinSeason: String?
)

// ── 구단 통계 ──────────────────────────────────────
data class TeamStatsData(
    val homeRecord:     RecordData?,
    val awayRecord:     RecordData?,
    val ratingRanking:  List<PlayerRankData>,
    val goalRanking:    List<PlayerRankData>,
    val assistRanking:  List<PlayerRankData>
)

data class RecordData(
    val played:       Int,
    val win:          Int,
    val draw:         Int,
    val lose:         Int,
    val goalsFor:     Int,
    val goalsAgainst: Int,
    val goalsDiff:    Int,
    val points:       Int
)

data class PlayerRankData(
    val rank:        Int,
    val playerId:    Long,
    val playerName:  String,
    val photoUrl:    String?,
    val value:       Double,
    val appearances: Int
)