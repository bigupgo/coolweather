package com.coolweather.app.service;

import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
		});

		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int ahour = 8 * 60 * 60 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + ahour;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,
				pintent);
		return super.onStartCommand(intent, flags, startId);
	}

	public void updateWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather_code", "");

		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";

		HttpUtil.sendHttpClientRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinsh(String response) {
				// TODO Auto-generated method stub
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
			}
		});
	}
}
