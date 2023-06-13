package com.example.network

import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @Headers("Accept:application/json")
    @GET("/posts")
    fun getAllPosts():Call<List<Post>>

    @GET("/posts")
    fun getOnePosts(@Query("name") id:Int):Call<List<Post>>

    @POST("/posts")
    fun createPost(@Body post: Post?): Call<Post>


}