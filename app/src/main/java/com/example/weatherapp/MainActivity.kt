package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//c8c4fe36e1a335cb351e92b2a6aaf645 API Key

//https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}

class MainActivity : AppCompatActivity() {

    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        fetchin weather data through Api
        fetchWeatherData("Bageshwar")
//        search method
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchBar
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName:String){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName, "c8c4fe36e1a335cb351e92b2a6aaf645", "metric")
        response.enqueue(object : Callback<dataset>{
            override fun onResponse(call: Call<dataset>, response: Response<dataset>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
//                    storing the api value in variables..

                   val temprature = responseBody.main.temp.toString()
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    val humidity = responseBody.main.humidity
                    val seaLevel = responseBody.main.pressure
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val wind = responseBody.wind.speed
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"


//                    setted the value in main activity..
                    binding.temp.text = "$temprature °C"
                    binding.maxTemp.text = "$maxTemp °C"
                    binding.minTemp.text = "$minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.sea.text = "$seaLevel hPa"
                    binding.sunrise.text = "${time(sunrise)} "
                    binding.sunset.text = "${time(sunset)} "
                    binding.wind.text = "$wind m/s"
                    binding.weather.text = condition
                    binding.condition.text = condition
//                    setting the time stap in orginal form of day..  EEEE is there orginal form
                    binding.day.text = dayName(System.currentTimeMillis())
//                    setting the date in orginal
                        binding.date.text= date()
                        binding.cityName.text="$cityName"

                    changeImagesAccordingCondition(condition)
                }
            }

            override fun onFailure(call: Call<dataset>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeImagesAccordingCondition(conditions: String) {
        when(conditions){
            "Clear Sky",  "Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sunny)
            }

             "Sunny" ->{
                binding.root.setBackgroundResource(R.drawable.cloudsun_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

             "Overcast", "Mist", "Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Partly Clouds", "Clouds" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloudyy)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return  sdf.format((Date()))
    }
//this is setting the date in format
    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return  sdf.format((Date()))
    }
//time
    private fun time(timeStamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return  sdf.format((Date(timeStamp*1000)))
    }
    }

