package com.abdi.weatherapp.data.response

import com.google.gson.annotations.SerializedName

data class WeatherResponse(

    @field:SerializedName("name")
    val name : String? = null,

    @field:SerializedName("weather")
    val weather: List<WeatherItem>? = null,

    @field:SerializedName("main")
    val main: Main? = null
)

data class WeatherItem(
    @field:SerializedName("id")
    val id : Int? = null,

    @field:SerializedName("icon")
    val icon : String? = null
)
