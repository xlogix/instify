package com.instify.android.helpers;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by krsnv on 21-Apr-17.
 */

public class RetrofitBuilder {

  //Api Url
  public static final String API_BASE_URL = "https://hashbird.com/gogrit.in/workspace/srm-api/";

  // Log requests in Stetho
  public static OkHttpClient.Builder httpClient =
      new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor());

  //Single Instance of Builder
  public static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL)
      .addConverterFactory(GsonConverterFactory.create());

  public static Retrofit retrofit = builder.client(httpClient.build()).build();

  public static <S> S createService(Class<S> serviceClass) {
    return retrofit.create(serviceClass);
  }
}