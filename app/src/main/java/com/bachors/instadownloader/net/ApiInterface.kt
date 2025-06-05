package com.bachors.instadownloader.net

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("exec")
    fun getVideo(@Query("u") postUrl: String?): Call<ResponseBody?>?
}
