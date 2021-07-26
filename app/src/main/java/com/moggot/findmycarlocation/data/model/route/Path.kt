package com.moggot.findmycarlocation.data.model.route

import com.squareup.moshi.Json
import java.util.ArrayList

data class Path(@field:Json(name = "routes") val routes: List<Route> = ArrayList())
