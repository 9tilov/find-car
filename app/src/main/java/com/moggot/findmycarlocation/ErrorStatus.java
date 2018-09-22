package com.moggot.findmycarlocation;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class ErrorStatus {

    @IntDef({LOCATION_ERROR, BUILD_PATH_ERROR, INTERNET_ERROR})
    @Retention(SOURCE)
    public @interface ParkingType {
    }

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

    public void setStatus(int type) {
        this.status = type;
    }

    @ParkingType
    public int getStatus() {
        return status;
    }
}
