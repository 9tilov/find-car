package com.moggot.findmycarlocation.common

sealed class ErrorStatus(throwable: Throwable) {

    data class LocationError(val throwable: Throwable) : ErrorStatus(throwable)
    data class BuildPathError(val throwable: Throwable) : ErrorStatus(throwable)
    data class InternetError(val throwable: Throwable) : ErrorStatus(throwable)
}
