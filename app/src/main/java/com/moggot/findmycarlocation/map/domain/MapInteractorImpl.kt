package com.moggot.findmycarlocation.map.domain

import com.moggot.findmycarlocation.data.model.route.Path
import kotlinx.coroutines.flow.Flow

class MapInteractorImpl(private val mapRepo: MapRepo) : MapInteractor {

    override fun getRoute(latitude: Double, longitude: Double): Flow<Path> =
        mapRepo.buildRoute(latitude, longitude)
}
