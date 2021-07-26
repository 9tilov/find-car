package com.moggot.findmycarlocation.data.model.route

import com.squareup.moshi.Json

data class Distance(
    @field:Json(name = "text") val text: String? = null,
    @field:Json(name = "value") val value: Int = 0
)
