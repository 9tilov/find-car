package com.moggot.findmycarlocation.data.api

import com.moggot.findmycarlocation.data.model.route.Path
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale

interface LocationApi {
    @GET("json?key=$GOOGLE_DIRECTIONS_API&mode=$mode")
    suspend fun getLocation(
        @Query(value = "origin") start: String,
        @Query(value = "destination") finish: String,
        @Query(value = "language") lang: String = Locale.getDefault().language
    ): Path

    companion object {
        const val BASE_LOCATION_URL = "https://maps.googleapis.com/maps/api/directions/"
        const val GOOGLE_DIRECTIONS_API = "AIzaSyB788vg91lcVGviuApiPJBfpBERnvJ4HDI"
        const val mode = "walking"
    }
}