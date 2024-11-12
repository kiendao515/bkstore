package com.hust.bkservice;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // Định nghĩa endpoint API để đẩy log
    @POST("api/v1/notifications")
    Call<Void> sendNotificationLog(@Body NotificationLog log);
}