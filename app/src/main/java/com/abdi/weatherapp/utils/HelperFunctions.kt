package com.abdi.weatherapp.utils

import java.math.RoundingMode

object HelperFunctions {

    fun formatterDegree(temperature : Double?) : String {
        val temp = temperature as Double
        val tempToCelcius = temp - 273.0
        val formatDegree = tempToCelcius.toBigDecimal().setScale(2, RoundingMode.CEILING)

        return "$formatDegree ºC"
    }
}