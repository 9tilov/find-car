package com.moggot.findmycarlocation.data.model.route

import com.squareup.moshi.Json

data class OverviewPolyline(@field:Json(name = "points") val points: String? = null)
