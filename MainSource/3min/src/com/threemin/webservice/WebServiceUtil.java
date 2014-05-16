package com.threemin.webservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.json.JSONObject;

import android.util.Log;


public class WebServiceUtil {
	public static final String tag = WebServiceUtil.class.getSimpleName();

	protected static final int TIMEOUT_CONNECTION = 10000;
	

	protected static String postJson(String link, JSONObject jo) throws Exception {
		Log.d(tag, link);
		Log.d(tag, jo.toString());
		URL url = new URL(link);
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(TIMEOUT_CONNECTION);
		conn.setReadTimeout(TIMEOUT_CONNECTION);
		((HttpURLConnection) conn).setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Content-Length", ""
				+ jo.toString().length());

		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(jo.toString());
		wr.flush();

		// Get the response
		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), Charset.forName("UTF-8")));
		String inputLine;

		String result = "";
		while ((inputLine = in.readLine()) != null) {
			result += inputLine;
		}
		in.close();
		Log.d(tag, result);
		return result;
	}

	protected static String getData(String url) throws Exception {
		Log.d(tag, url);
		String response = null;
		URL myUrl = new URL(url);
		HttpURLConnection con = (HttpURLConnection) myUrl.openConnection();
		con.setRequestProperty("Content-Type", "application/json");
		con.setConnectTimeout(TIMEOUT_CONNECTION);
		con.setReadTimeout(TIMEOUT_CONNECTION);
		((HttpURLConnection) con).setRequestMethod("GET");
		InputStream ins = con.getInputStream();
		InputStreamReader isr = new InputStreamReader(ins);
		BufferedReader in = new BufferedReader(isr);
		String inputLine;
		String temp = "";
		while ((inputLine = in.readLine()) != null) {
			temp += inputLine;
		}
		in.close();
		isr.close();
		ins.close();
		response = temp;
		Log.d(tag, response);
		return response;
	}
	
	// TODO other
	public static String getHttpUrl(String fileURL) {
		fileURL = fileURL.replaceAll(" ", "%20");
		return fileURL;
	}
}
