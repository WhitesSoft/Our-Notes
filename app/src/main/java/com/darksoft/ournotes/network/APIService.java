package com.darksoft.ournotes.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAlcc3L5c:APA91bGkJErNg2X7-Z0p2MKiwOQFBAA0BiAefOq91q5VlcJMyACzUhuVLO5ctz5KfAKK80HebuBOyUa-MoZg2QBhyE4YVXldJNhAHDX_MPk4qi_oryBBQpyVy6g68W1YpAumYP8QLcyn"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);

}