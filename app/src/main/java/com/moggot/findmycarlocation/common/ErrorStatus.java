package com.moggot.findmycarlocation.common;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class ErrorStatus {

    public static final int LOCATION_ERROR = 0;
    public static final int BUILD_PATH_ERROR = 1;
    public static final int INTERNET_ERROR = 2;
    private int status;
    private Throwable throwable;

    public ErrorStatus(@ParkingType int status) {
        this(status, null);
    }

    public ErrorStatus(@ParkingType int status, Throwable throwable) {
        this.throwable = throwable;
        this.status = status;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

    @ParkingType
    public int getStatus() {
        return status;
    }

    public void setStatus(int type) {
        this.status = type;
    }

    @IntDef({LOCATION_ERROR, BUILD_PATH_ERROR, INTERNET_ERROR})
    @Retention(SOURCE)
    public @interface ParkingType {
    }
}
