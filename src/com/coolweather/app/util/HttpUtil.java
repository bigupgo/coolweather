package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				try {

					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					// connection.setRequestProperty("Accept-Charset", "UTF-8");

					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					// PrintWriter out = new PrintWriter(new
					// OutputStreamWriter(connection.getOutputStream(),"utf-8"));
					String line = "";
					StringBuilder response = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					if (listener != null) {
						listener.onFinsh(response.toString());
					}

				} catch (Exception e) {
					e.printStackTrace();
					if (listener != null) {
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}

			}
		}).start();

	}

	public static void sendHttpClientRequest(final String address,
			final HttpCallbackListener listener) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpClient httpClient = null;
				try {
					HttpGet httpget = new HttpGet(address);
					httpClient = new DefaultHttpClient();
					HttpResponse httpResponse = httpClient.execute(httpget);
					String response = "";

					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						response = EntityUtils.toString(entity, "utf-8");
					}
					if (listener != null) {
						listener.onFinsh(response);
					}

				} catch (Exception e) {
					e.printStackTrace();
					if (listener != null) {
						listener.onError(e);
					}
				} 

			}
		}).start();

	}
}
