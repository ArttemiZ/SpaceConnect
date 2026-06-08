package br.com.fiap.spaceconnect.data.remote.api

import br.com.fiap.spaceconnect.data.remote.dto.ApodDto
import br.com.fiap.spaceconnect.data.remote.dto.MarsRoverResponseDto
import br.com.fiap.spaceconnect.data.remote.dto.NeoFeedDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {

    @GET("planetary/apod")
    suspend fun getAstronomyPictureOfDay(
        @Query("api_key") apiKey: String,
        @Query("count")   count: Int = 10
    ): List<ApodDto>

    @GET("neo/rest/v1/feed")
    suspend fun getNearEarthObjects(
        @Query("start_date") startDate: String,
        @Query("end_date")   endDate: String,
        @Query("api_key")    apiKey: String
    ): NeoFeedDto

    @GET("mars-photos/api/v1/rovers/curiosity/photos")
    suspend fun getMarsRoverPhotos(
        @Query("sol")     sol: Int = 1000,
        @Query("page")    page: Int = 1,
        @Query("api_key") apiKey: String
    ): MarsRoverResponseDto
}