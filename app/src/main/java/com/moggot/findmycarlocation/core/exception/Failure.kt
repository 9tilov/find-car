package com.moggot.findmycarlocation.core.exception

sealed class Failure {
    object NetworkConnection : Failure()
    object ServerError : Failure()
    object WrongEmail : Failure()
    object WrongPassword : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure : Failure()
}
