package com.moggot.findmycarlocation.map.domain

import com.moggot.findmycarlocation.data.model.route.Path
import kotlinx.coroutines.flow.Flow

interface MapRepo {

    fun buildRoute(latitude: Double, longitude: Double): Flow<Path>
}
