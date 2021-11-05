package com.inudev.ygbatikar.utils;

import com.inudev.ygbatikar.model.ShopModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
//    @GET("get/shop")
    @GET("get/cflDBavNWq?indent=2")

    Call<ShopModel> getPertokoanData();
}
