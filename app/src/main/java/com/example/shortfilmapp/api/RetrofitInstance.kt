//package com.example.shortfilmapp.api
//
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object RetrofitInstance {
//    private val retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl("https://api.themoviedb.org/3/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    val api: TmdbApi by lazy {
//        retrofit.create(TmdbApi::class.java)
//    }
//}
