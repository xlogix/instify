package xyz.fnplus.instify.interfaces;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import xyz.fnplus.instify.models.NewsItemModelList;
import xyz.fnplus.instify.models.TestPerformanceResponseModel;
import xyz.fnplus.instify.models.UserModel;

/**
 * Created by krsnv on 21-Apr-17.
 */

public interface RetrofitInterface {
    @FormUrlEncoded
    @POST("get-info.php")
    Call<UserModel> Login(
            @Field("regno") String username,
            @Field("pass") String password);

    @GET("univ-news.php")
    Call<NewsItemModelList> GetUnivNews();


    @GET("get-marks.php")
    Call<TestPerformanceResponseModel> GetTestPerformance(@Query("regno") String username,
                                                          @Query("pass") String password);
}
