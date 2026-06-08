package com.example.livescore // 본인 패키지명 확인

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.POST
interface ApiService {
    // 1. 메인 화면: 전체 경기 목록 가져오기
    @GET("/api/soccer/matches")
    fun getMatches(): Call<List<MatchData>>

    // 2. 상세 화면 (라인업): fixtureId로 조회
    @GET("/api/soccer/lineups/{fixtureId}")
    fun getLineups(@Path("fixtureId") fixtureId: Long): Call<List<LineupData>>

    // 3. 상세 화면 (순위표): 리그 ID와 시즌으로 조회
    @GET("/api/soccer/standings")
    fun getStandings(
        @Query("leagueId") leagueId: Int,
        @Query("season") season: Int
    ): Call<List<StandingData>>

    // 4. 상세 화면 (상대 전적): 두 팀의 ID로 조회 🌟 (이 부분이 없어서 에러가 났던 것!)
    @GET("/api/soccer/h2h")
    fun getH2H(
        @Query("homeId") homeId: Int,
        @Query("awayId") awayId: Int
    ): Call<H2HSummaryData>

    @POST("/api/user/login")
    fun login(@Body loginData: Map<String, String>): Call<String>

    @POST("/api/user/register")
    fun register(@Body registerData: Map<String, String>): Call<String>

    @GET("/api/soccer/matches/{fixtureId}/info")
    fun getMatchInfo(@Path("fixtureId") fixtureId: Long): Call<MatchInfoData>
}