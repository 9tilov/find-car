package com.moggot.findmycarlocation.map

import android.location.Location
import com.moggot.findmycarlocation.base.BaseView
import com.moggot.findmycarlocation.data.model.route.Path

interface GoogleMapView : BaseView {

    fun updateLocation(location: Location?)
    fun drawRoute(path: Path?)
    fun onCarFound()
}