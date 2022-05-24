package com.abdi.weatherapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdi.weatherapp.BuildConfig
import com.abdi.weatherapp.data.response.ListItem
import com.abdi.weatherapp.databinding.RowItemBinding
import com.abdi.weatherapp.utils.HelperFunctions.formatterDegree
import com.abdi.weatherapp.utils.iconSize2x
import com.abdi.weatherapp.utils.iconSize4x
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.MyViewHolder>() {

    private var listWeather = ArrayList<ListItem>()

    class MyViewHolder(val binding: RowItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        RowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = listWeather[position]
        holder.binding.apply {
            val maxDegree = "Max:" + formatterDegree(data.main?.tempMax)
            val minDegree = "Min:" + formatterDegree(data.main?.tempMin)

            tvMaxDegree.text = maxDegree
            tvMinDegree.text = minDegree

            val date = data.dtTxt?.take(10)
            val time = data.dtTxt?.takeLast(8)
            val dateArray = date?.split("-")?.toTypedArray()
            val timeArray = time?.split(":")?.toTypedArray()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, Integer.parseInt(dateArray?.get(0) as String))
            calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1)

            calendar.set(Calendar.DAY_OF_YEAR, Integer.parseInt(dateArray[2]))
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray?.get(0) as String))
            calendar.set(Calendar.MINUTE, 0)

            val dateFormat =
                SimpleDateFormat("EE, MM d", Locale.getDefault()).format(calendar.time).toString()
            val timeFormat =
                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time).toString()

            tvItemDate.text = dateFormat
            tvItemTime.text = timeFormat

            val icon = data.weather?.get(0)?.icon
            val iconUrl = BuildConfig.IMAGE_URL + icon + iconSize2x
            Glide.with(imgItemWeather.context).load(iconUrl).into(imgItemWeather)
        }
    }

    override fun getItemCount() = listWeather.size

    fun setData(data: List<ListItem>?) {
        if (data == null) return
        listWeather.clear()
        listWeather.addAll(data)
    }
}