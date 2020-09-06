package com.dorukaneskiceri.kotlinmessenger.service;

import retrofit2.http.Headers;
import retrofit2.http.POST;
import com.dorukaneskiceri.kotlinmessenger.notifications.MyResponse;
import com.dorukaneskiceri.kotlinmessenger.notifications.Sender;
import retrofit2.http.Body;
import retrofit2.Call;


public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAASy1yEEg:APA91bGN9giSwWFpHbG77GURD9N-SSOzuCnn44v-JvKDXOWglsCxf3PEIUjdsMSejaHMx3VttoHbc3WLv2y-EIM6_QHyd4YCJd5B3xeTcQ571V4AStf5ycnrjadFSBk3daR-L0I2Jb8x"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}