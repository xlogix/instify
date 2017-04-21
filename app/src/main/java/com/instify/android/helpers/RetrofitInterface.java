package com.instify.android.helpers;

import com.instify.android.models.UserModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by krsnv on 21-Apr-17.
 */

public interface RetrofitInterface {
    @FormUrlEncoded
    @POST("get-info.php")
    Call<UserModel> Login(
            @Field("regno") String username,
            @Field("pass") String password);
}
