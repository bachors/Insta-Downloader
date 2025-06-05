package com.bachors.instadownloader.net

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiIg {
    const val BASE_URL: String =
        "https://script.google.com/macros/s/AKfycbyHQ9twIO2CKf4TFsPDHrB2JARDK66yVDlUrmPukDefHnWsAAnRuFjE8DR1R-qU-nC9/"
    private var retrofit: Retrofit? = null

    @JvmStatic
    val client: Retrofit
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
}
