package com.moggot.findmycarlocation.data.model.route

import com.squareup.moshi.Json

data class Leg(
    @field:Json(name = "distance") val distance: Distance? = null,
    @field:Json(name = "duration") val duration: Duration? = null,
    @field:Json(name = "start_address") val startAddress: String? = null,
    @field:Json(name = "end_address") val endAddress: String? = null
)
