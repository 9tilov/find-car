package com.moggot.findmycarlocation.data.repository.local

import android.content.Context
import com.moggot.findmycarlocation.constants.DataConstants.Companion.BASE_NAMESPACE
import com.moggot.findmycarlocation.extensions.bool
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceStore @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences = context.getSharedPreferences(
        PREFERENCE_NAME,
        Context.MODE_PRIVATE
    )

    var requestingLocationUpdates by sharedPreferences.bool(
        defaultValue = false,
        key = { KEY_REQUESTING_LOCATION_UPDATES }
    )

    val isAlreadyParked: Boolean
        get() = sharedPreferences.getBoolean(PARKING_STATE, false)

    companion object {
        private const val PREFERENCE_NAME = "$BASE_NAMESPACE.storage"
        private const val KEY_REQUESTING_LOCATION_UPDATES =
            "$BASE_NAMESPACE.requesting_location_updates"
        private const val PARKING_STATE = "parking_state"
        private const val LATITUDE = "latitude"
        private const val LONGITUDE = "longitude"
        private const val TIME = "time"
    }
}