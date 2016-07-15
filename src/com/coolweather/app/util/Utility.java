package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.model.WeatherInfo;
import com.google.gson.Gson;


public class Utility {

	/**
	* 解析和处理服务器返回的省级数据
	*/
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvince = response.split(",");
			if (allProvince != null && allProvince.length > 0) {
				for (String p : allProvince) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(province);
				}

				return true;
			}
		}
		return false;
	}

	/**
	* 解析和处理服务器返回的市级数据
	*/
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					// 将解析出来的数据存储到City表
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					// 将解析出来的数据存储到County表
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	public static void handleWeatherResponse(Context context, String response) {
		try{
			
			Gson gson =new Gson();
			WeatherInfo weather = gson.fromJson(response, WeatherInfo.class);
			//List<WeatherInfo> list = gson.fromJson(response, new TypeToken<List<WeatherInfo>>(){}.getType());
			saveWeatherInfo(context, weather);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void saveWeatherInfo(Context context, WeatherInfo weatherInfo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", weatherInfo.getCity());
		editor.putString("weather_code", weatherInfo.getCityid());
		editor.putString("temp1", weatherInfo.getTemp1());
		editor.putString("temp2", weatherInfo.getTemp2());
		editor.putString("weather_desp", weatherInfo.getWeather());
		editor.putString("publish_time", weatherInfo.getPtime());
		editor.putString("img1", weatherInfo.getImg1());
		editor.putString("img2", weatherInfo.getImg2());
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
	

}
