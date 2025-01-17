package com.hust.bkservice;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/v1/notifications")
    Call<Void> sendNotificationLog(@Body NotificationLog log);
}