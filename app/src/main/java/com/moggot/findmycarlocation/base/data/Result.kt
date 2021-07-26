package com.moggot.findmycarlocation.base.data

data class Result<out T>(val status: Status, val data: T? = null, val error: Throwable? = null) {

    companion object {

        fun <T> success(data: T? = null): Result<T> {
            return Result(Status.SUCCESS, data, null)
        }

        fun <T> error(exception: Throwable, data: T? = null): Result<T> {
            return Result(Status.ERROR, data, exception)
        }

        fun <T> loading(data: T? = null): Result<T> {
            return Result(Status.LOADING, data, null)
        }
    }
}
