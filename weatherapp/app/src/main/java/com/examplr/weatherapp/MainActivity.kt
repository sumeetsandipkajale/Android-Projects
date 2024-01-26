package com.examplr.weatherapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.room.util.query
import com.examplr.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.Condition

//be2c89c69c9661e22340cbd47500c24d
// https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
// ... (import statements)

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("mumbai")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    fetchWeatherData(query)
                }


                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
               return true
            }

        })
    }

    private fun fetchWeatherData(Cityname:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(apiinterface::class.java)

        val response = retrofit.getWeatherData(Cityname, "be2c89c69c9661e22340cbd47500c24d", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val temperature = responseBody.main.temp.toString()
                        val humidity = responseBody.main.humidity
                        val windspeed = responseBody.wind.speed
                        val sunrise = responseBody.sys.sunrise.toLong()
                        val sunset = responseBody.sys.sunset.toLong()
                        val sealevel = responseBody.main.pressure
                        val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                        val maxtemp = responseBody.main.temp_max
                        val mintemp = responseBody.main.temp_min


                        binding.temperature.text = "$temperature°C"
                        binding.weather.text = condition
                        binding.maxTemp.text = "Max Temp :$maxtemp°C"
                        binding.minTemp.text = "Min Temp :$mintemp°C"
                        binding.humidity.text = "$humidity%"
                        binding.winds.text = "$windspeed m/s"
                        binding.sunrise.text = "${time(sunrise)}"
                        binding.sunset.text = "${time(sunset)}"
                        binding.sea.text = "$sealevel hpa"
                        binding.condition.text = condition
                        binding.date.text = date()
                            binding.day.text= dayname(System.currentTimeMillis())
                            binding.Cityname.text = "$Cityname"


//                        Log.i("TAG", "onResponse: $temperature")

                        changeconditionaccordingtoweather(condition)
                    } else {
                        Log.e("TAG", "Response body is null")
                    }
                } else {
                    Log.e("TAG", "Response not successful. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e("TAG", "Failed to fetch weather data", t)
                // Handle failure appropriately (e.g., show an error message to the user)
            }
        })

        }

    private fun changeconditionaccordingtoweather(condition:String) {
       when(condition){
           "sunny","clear" ->{
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieAnimationView.setAnimation(R.raw.sun)
           }
           "Partly Clouds","Clouds","Overcast","Mist","Foggy" ->{
               binding.root.setBackgroundResource(R.drawable.colud_background)
               binding.lottieAnimationView.setAnimation(R.raw.cloud)
           }
           "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
               binding.root.setBackgroundResource(R.drawable.rain_background)
               binding.lottieAnimationView.setAnimation(R.raw.rain)
           }
           "Light Snow","Moderate Snow","Heavy Snow", "Blizzard" ->{
               binding.root.setBackgroundResource(R.drawable.snow_background)
               binding.lottieAnimationView.setAnimation(R.raw.snow)
           }
           "Haze" ->{
               binding.root.setBackgroundResource(R.drawable.haze)
               binding.lottieAnimationView.setAnimation(R.raw.cloud)
           }
           "Smoke" ->{
               binding.root.setBackgroundResource(R.drawable.smog)
               binding.lottieAnimationView.setAnimation(R.raw.cloud)
           }
           else->{
               binding.root.setBackgroundResource(R.drawable.clr)
               binding.lottieAnimationView.setAnimation(R.raw.sun)
           }
       }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp:Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayname(timestamp:Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}
