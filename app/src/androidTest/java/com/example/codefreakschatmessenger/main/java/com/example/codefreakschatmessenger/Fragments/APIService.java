package com.example.codefreakschatmessenger.Fragments;

import com.example.codefreakschatmessenger.Notifications.MyResponse;
import com.example.codefreakschatmessenger.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAlVg_8ZY:APA91bH7_yBkH1utsT-6V5A0ARUDlGsyrPZ5yXXYewJwvuUxSQRe7C41F84s7LBu4hkX0AfqOfUEhFF0hBHRwIcqF58b3ajtuSCOHM52wwkwo0ghaw0ZvXuMG8Eg2cFz3b9JrIXM-khw"
    })

    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);


}
