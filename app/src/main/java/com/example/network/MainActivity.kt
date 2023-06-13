package com.example.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView

    lateinit var connectivityManager : ConnectivityManager

    private fun NetworkCapabilities?.isNetworkCapabilitiesValid(): Boolean =
        when {
        this == null -> false
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                (hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) -> true
        else -> false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.text_view)
        Thread() {
            //  val response = getRequest("https://jsonplaceholder.typicode.com/posts")

            val post = Post("some example", 102, "venkat", 102)
            val json = Gson().toJson(post, Post::class.java)
            val postResponse =
                postRequest("https://jsonplaceholder.typicode.com/posts", json.toString())
            runOnUiThread {
                // textView.text = postResponse
            }
        }.start()

        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        //SharedPreferences

        Prefs.putAny("ISLOGIN", "hello")
        Prefs.remove("ISLOGIN")

     /*   val sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE)


        if(sharedPreferences.contains("IS_FIRST_TIME")){
            val editor = sharedPreferences.edit()
            editor.putBoolean("IS_FIRST_TIME", true).apply()
        }

        val isFirstTime = sharedPreferences.getBoolean("IS_FIRST_TIME", false)

        if(isFirstTime){
            Toast.makeText(this, "NO", Toast.LENGTH_SHORT).show()
        }else{

            Toast.makeText(this, "YES", Toast.LENGTH_SHORT).show()
        }*/

        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            val isNetworkConnected: Boolean = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                .isNetworkCapabilitiesValid()
            if(isNetworkConnected){
                Toast.makeText(this, "Network available", Toast.LENGTH_SHORT).show()
            }else
            {
                Toast.makeText(this, "Network Not available", Toast.LENGTH_SHORT).show()
            }
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)


        val call = apiInterface.getAllPosts()
        call.enqueue(object:Callback<List<Post>>{
            override fun onResponse(call: Call<List<Post>>?, response: Response<List<Post>>?) {
                    val posts = response?.body()
            }

            override fun onFailure(call: Call<List<Post>>?, t: Throwable?) {

            }

        })
        val postCall = apiInterface.createPost(Post("ah",122,"ve",122))
        postCall.enqueue(object:Callback<Post>{
            override fun onResponse(call: Call<Post>?, response: Response<Post>?) {

            }

            override fun onFailure(call: Call<Post>?, t: Throwable?) {

            }

        })

    }

    fun getRequest(url: String):String{

        val url = URL(url)
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.doInput = true
            connection.connect()
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val responseBody = inputStream.bufferedReader().use { it.readText() }
                System.out.println("DATA "+responseBody)
                return responseBody
            }else{
                System.out.println("DATA "+connection.responseMessage)
                Log.e("ERROR", connection.responseMessage)
            }
            }catch (e:Exception){
            System.out.println("DATA "+e.localizedMessage)
            e.localizedMessage?.let { Log.e("ERROR", it) }
        }finally {
            connection.disconnect()
        }

        return ""
    }

    fun postRequest(url: String, data: String):String{
        val url = URL(url)
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.connect()
            val outputStream = connection.outputStream
            outputStream.write(data.toByteArray())
            outputStream.flush()
            outputStream.close()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                val inputStream = connection.inputStream
                val responseBody = inputStream.bufferedReader().use { it.readText() }
                return responseBody
            }else{
                System.out.println("DATA "+connection.responseMessage)
                Log.e("ERROR", connection.responseMessage)
            }
        }catch (e:Exception){
            System.out.println("DATA "+e.localizedMessage)
            e.localizedMessage?.let { Log.e("ERROR", it) }
        }finally {
            connection.disconnect()
        }

        return ""
    }



}