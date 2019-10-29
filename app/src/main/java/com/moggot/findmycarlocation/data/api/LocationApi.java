package com.moggot.findmycarlocation.data.api;

import androidx.annotation.WorkerThread;

import com.moggot.findmycarlocation.data.model.route.Path;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationApi {

    String BASE_LOCATION_URL = "https://maps.googleapis.com/maps/api/directions/";
    String GOOGLE_DIRECTIONS_API = "AIzaSyB788vg91lcVGviuApiPJBfpBERnvJ4HDI";
    String mode = "walking";

    @GET("json?key=" + GOOGLE_DIRECTIONS_API + "&mode=" + mode)
    @WorkerThread
    Single<Path> getLocation(@Query(value = "origin") String start,
                             @Query(value = "destination") String finish);
}
