package com.myapp.instantfoodsreviewapp.restapi;

import com.myapp.instantfoodsreviewapp.model.UserAccountData;
import com.myapp.instantfoodsreviewapp.model.UserLoginData;
import com.myapp.instantfoodsreviewapp.model.UserRegisterData;
import com.myapp.instantfoodsreviewapp.model.entity.ApiResultDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitInterface {

    @FormUrlEncoded
    @POST("v1/user/regist")
    Call<UserRegisterData> regist(@Field("email") String email, @Field("nickname") String nickname, @Field("password") String password);

    @FormUrlEncoded
    @POST("v1/user/login")
    Call<ApiResultDto> login(@Field("email") String email, @Field("password") String password);


    // @Header("key : authorization","token")

    @GET("v1/user/account")
        //Call<UserAccountData> account();
    Call<ApiResultDto> account(@Header("authorization") String token);

    @FormUrlEncoded
    @PUT("v1/user/change/pwd")
    Call<ApiResultDto> change(@Header("authorization") String token,
                              @Field("original_password") String originPassword,
                              @Field("request_password") String requestPassword);


    @POST("v1/user/secession")
    Call<ApiResultDto> secession(@Header("authorization") String token);


    @FormUrlEncoded
    @PUT("v1/user/nickname")
    Call<ApiResultDto> nickname(@Header("authorization") String token,
                                @Field("nickname") String newNickName);

    @FormUrlEncoded
    @POST("v1/user/primage")
    Call<ApiResultDto>  primage(@Header("authorization") String token);

}