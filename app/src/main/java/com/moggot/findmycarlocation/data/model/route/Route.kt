package com.moggot.findmycarlocation.data.model.route

import com.squareup.moshi.Json
import java.util.ArrayList

data class Route(
    @field:Json(name = "legs") val legs: List<Leg> = ArrayList(),
    @field:Json(name = "overview_polyline") val overviewPolyline: OverviewPolyline? = null
)
