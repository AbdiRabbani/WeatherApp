package com.abdi.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdi.weatherapp.BuildConfig
import com.abdi.weatherapp.R
import com.abdi.weatherapp.data.response.ForecastResponse
import com.abdi.weatherapp.data.response.WeatherResponse
import com.abdi.weatherapp.databinding.ActivityMainBinding
import com.abdi.weatherapp.utils.HelperFunctions.formatterDegree
import com.abdi.weatherapp.utils.LOCATION_PERMISSION_REQ_CODE
import com.abdi.weatherapp.utils.iconSize2x
import com.abdi.weatherapp.utils.iconSize4x
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var _viewModel: MainViewModelActivity? = null
    private val viewModel get() = _viewModel as MainViewModelActivity

    private val weatherAdapter by lazy { WeatherAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetControler = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetControler?.isAppearanceLightNavigationBars = true

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _viewModel = ViewModelProvider(this)[MainViewModelActivity::class.java]

        searchCity()

        viewModel.weatherByCity().observe(this) {
            setupView(it, null)
        }

        viewModel.getForecastByCity().observe(this) {
            setupView(null, it)
        }

        getWeatherCurrentLocation()

    }

    private fun getWeatherCurrentLocation() {
        val fusedLocationCilent: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_PERMISSION_REQ_CODE
            )
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationCilent.lastLocation
            .addOnSuccessListener {
                try {
                    val lat = it.latitude
                    val lon = it.longitude
                    viewModel.weatherByCurrentLocation(lat, lon)
                    viewModel.forecastByCurrentLocation(lat, lon)
                } catch (e: Throwable) {
                    Log.i("MainActivity", "last location : it")
                    Log.e("MainActivity", "Couldn't get latilude and longitude")
                }

            }
            .addOnFailureListener {
                Log.e("MainActivity", "FusedLocationError: Failed getting current location")
            }
        viewModel.getWeatherByCurrentLocation().observe(this) {
            setupView(it, null)
        }
        viewModel.getForecastByCurrentLocation().observe(this) {
            setupView(null, it)
        }
    }

    private fun setupView(weather: WeatherResponse?, forecast: ForecastResponse?) {
        binding.apply {
            weather?.let{
                city.text = weather.name
                tvDegree.text = formatterDegree(it.main?.temp)

                val icon = weather.weather?.get(0)?.icon
                val iconUrl = BuildConfig.IMAGE_URL + icon + iconSize4x
                Glide.with(applicationContext).load(iconUrl).into(icWeather)


                setupBackground(weather.weather?.get(0)?.id, icon)
            }

            rv.apply {
                weatherAdapter.setData(forecast?.list)
                layoutManager = LinearLayoutManager(
                    applicationContext,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = weatherAdapter
            }
        }
    }

    private fun setupBackground(idWeather : Int?, icon : String?) {
        idWeather?.let {
            when (idWeather) {
                in resources.getIntArray(R.array.thunderstorm_id_list) -> setImageBackground(R.drawable.thunderstorm)
                in resources.getIntArray(R.array.drizzle_id_list) -> setImageBackground(R.drawable.drizzle)
                in resources.getIntArray(R.array.rain_id_list) -> setImageBackground(R.drawable.rain)
                in resources.getIntArray(R.array.freezing_rain_id_list) -> setImageBackground(R.drawable.freezing_rain)
                in resources.getIntArray(R.array.snow_id_list) -> setImageBackground(R.drawable.snow)
                in resources.getIntArray(R.array.sleet_id_list) -> setImageBackground(R.drawable.sleet)
                in resources.getIntArray(R.array.clear_id_list) -> setImageBackground(R.drawable.clear)
                in resources.getIntArray(R.array.light_clouds_id_list) -> setImageBackground(R.drawable.lightcloud)
                in resources.getIntArray(R.array.heavy_clouds_id_list) -> setImageBackground(R.drawable.heavycloud)
                in resources.getIntArray(R.array.fog_id_list) -> setImageBackground(R.drawable.fog)
                in resources.getIntArray(R.array.sand_id_list) -> setImageBackground(R.drawable.sand)
                in resources.getIntArray(R.array.dust_id_list) -> setImageBackground(R.drawable.dust)
                in resources.getIntArray(R.array.volcanic_ash_id_list) -> setImageBackground(R.drawable.volcanic)
                in resources.getIntArray(R.array.squalls_id_list) -> setImageBackground(R.drawable.squalls)
                in resources.getIntArray(R.array.tornado_id_list) -> setImageBackground(R.drawable.tornado)

                in resources.getIntArray(R.array.clear_id_list) -> {
                    when(icon) {
                        "01d" -> setImageBackground(R.drawable.clear)
                        "01n" -> setImageBackground(R.drawable.clear)
                    }
                }


                else -> {}
            }
        }
    }

    private fun setImageBackground(image : Int) {
        Glide.with(this).load(image).into(binding.imgBgWeather)
    }

    private fun searchCity() {
        binding.edtSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        viewModel.searchByCity((it))
                        viewModel.forecastByCity(it)
                    }
                    try {
                        val inputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
                    } catch (e: Throwable) {
                        Log.e("MainActivity", "hideSoftWindow: $e")
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            }
        )
    }
}